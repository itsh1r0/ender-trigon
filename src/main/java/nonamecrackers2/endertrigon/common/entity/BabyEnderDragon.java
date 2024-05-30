/*
 * Copyright 2022 nonamecrackers2

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package nonamecrackers2.endertrigon.common.entity;

import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nonamecrackers2.endertrigon.common.entity.goal.BabyEnderDragonAttackStrategyGoal;
import nonamecrackers2.endertrigon.common.entity.goal.BabyEnderDragonFollowOwnerGoal;
import nonamecrackers2.endertrigon.common.entity.goal.BabyEnderDragonMoveAnchorGoal;
import nonamecrackers2.endertrigon.common.entity.goal.BabyEnderDragonSweepGoal;
import nonamecrackers2.endertrigon.common.init.EnderTrigonEntityTypes;

public class BabyEnderDragon extends ShoulderRidingEntity implements NeutralMob, FlyingAnimal
{
	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	private static final EntityDataAccessor<Integer> REMAINING_ANGER_TIME = SynchedEntityData.defineId(BabyEnderDragon.class, EntityDataSerializers.INT);
	private static final EntityDimensions SITTING = EntityDimensions.fixed(0.9F, 0.85F);
	public float flapTime;
	public float oFlapTime;
	public final double[][] latencyYs = new double[64][3];
	public int latencyPointer = -1;
	private Vec3 moveTarget = Vec3.ZERO;
	private BlockPos anchor = BlockPos.ZERO;
	private BabyEnderDragon.Phase phase = BabyEnderDragon.Phase.CIRCLE;
	private @Nullable UUID persistentAngerTarget;
	private final BabyEnderDragon.TargetGoal<Monster> monsterTargeting = new BabyEnderDragon.TargetGoal<>(this, Monster.class, l -> {
		return l instanceof Zombie || l instanceof Spider || l instanceof AbstractSkeleton || l instanceof Pillager || l instanceof Phantom;
	});
	
	public BabyEnderDragon(EntityType<? extends BabyEnderDragon> type, Level level)
	{
		super(type, level);
		this.lookControl = new BabyEnderDragon.DragonLookControl(this);
		this.moveControl = new BabyEnderDragon.DragonMoveControl(this);
	}
	
	@Override
	protected PathNavigation createNavigation(Level level)
	{
		FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
		navigation.setCanOpenDoors(false);
		navigation.setCanFloat(true);
		navigation.setCanPassDoors(true);
		return navigation;
	}
	
	@Override
	protected BodyRotationControl createBodyControl()
	{
		return new BabyEnderDragon.DragonBodyControl(this);
	}
	
	//TODO: Test step height
	public static AttributeSupplier.Builder createAttributes()
	{
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.STEP_HEIGHT, 1.0F);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(1, new BabyEnderDragonFollowOwnerGoal(this));
		this.goalSelector.addGoal(2, new BabyEnderDragonAttackStrategyGoal(this));
		this.goalSelector.addGoal(3, new BabyEnderDragonSweepGoal(this));
		this.goalSelector.addGoal(4, new BabyEnderDragonMoveAnchorGoal(this));
		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(4, new BabyEnderDragon.TargetGoal<>(this, Player.class, l -> !this.isTame()));
	}
	
	@Override
	public void setTame(boolean flag, boolean tamingSideEffects)
	{
		if (flag != this.isTame())
		{
			if (flag)
				this.targetSelector.addGoal(5, this.monsterTargeting);
			else
				this.targetSelector.removeGoal(this.monsterTargeting);
		}
		super.setTame(flag, tamingSideEffects);
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		super.defineSynchedData(builder);
		builder.define(REMAINING_ANGER_TIME, 0);
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag tag)
	{
		super.addAdditionalSaveData(tag);
		tag.put("Anchor", NbtUtils.writeBlockPos(this.getAnchor()));
		tag.putInt("Phase", this.phase.ordinal());
		if (this.moveTarget != null)
		{
			CompoundTag moveTargetTag = new CompoundTag();
			moveTargetTag.putDouble("x", this.moveTarget.x);
			moveTargetTag.putDouble("y", this.moveTarget.y);
			moveTargetTag.putDouble("z", this.moveTarget.z);
			tag.put("MoveTarget", moveTargetTag);
		}
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag tag)
	{
		super.readAdditionalSaveData(tag);
		this.setAnchor(NbtUtils.readBlockPos(tag, "Anchor").orElse(BlockPos.ZERO));
		if (tag.contains("Phase", 3))
		{
			int ordinal = tag.getInt("Phase");
			if (ordinal > 0 && ordinal < BabyEnderDragon.Phase.values().length)
				this.phase = BabyEnderDragon.Phase.values()[ordinal];
		}
		if (tag.contains("MoveTarget", 10))
		{
			CompoundTag moveTargetTag = tag.getCompound("MoveTarget");
			Vec3 vec = new Vec3(moveTargetTag.getDouble("x"), moveTargetTag.getDouble("y"), moveTargetTag.getDouble("z"));
			this.moveTarget = vec;
		}
	}
	
	@Override
	public boolean hurt(DamageSource source, float damage)
	{
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
			return super.hurt(source, damage);
		
		if (this.isTame())
			return super.hurt(source, damage * 0.25F);
		else
			return super.hurt(source, damage);
	}
	
	@Override
	public boolean isFlying()
	{
		return !this.onGround();
	}
	
	protected void checkFallDamage(double amount, boolean flag, BlockState state, BlockPos pos)
	{
	}

	@Override
	public void travel(Vec3 delta)
	{
		if (this.isControlledByLocalInstance())
		{
			if (this.isInWater())
			{
				this.moveRelative(0.02F, delta);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
			}
			else if (this.isInLava())
			{
				this.moveRelative(0.02F, delta);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
			}
			else
			{
				BlockPos ground = this.getBlockPosBelowThatAffectsMyMovement();
				float f = 0.91F;
				if (this.onGround())
					f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;

				float f1 = 0.16277137F / (f * f * f);
				f = 0.91F;
				if (this.onGround())
					f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;

				this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, delta);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale((double) f));
//				if (this.getPhase() == BabyEnderDragon.Phase.LAND)
//					this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
			}
		}

		this.calculateEntityAnimation(false);
	}

	public boolean onClimbable()
	{
		return false;
	}
	
	@Override
	public boolean isFlapping()
	{
		float f = Mth.cos(this.flapTime * ((float) Math.PI * 2F));
		float f1 = Mth.cos(this.oFlapTime * ((float) Math.PI * 2F));
		return f1 <= -0.3F && f >= -0.3F;
	}
	
	@Override
	public void onFlap()
	{
		if (this.level().isClientSide && !this.isSilent())
			this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 1.7F + this.random.nextFloat() * 0.3F, false);
	}
	
	public double[] getLatency(int pointer, float partialTicks)
	{
		if (this.isDeadOrDying())
			partialTicks = 0.0F;

		partialTicks = 1.0F - partialTicks;
		int i = this.latencyPointer - pointer & 63;
		int j = this.latencyPointer - pointer - 1 & 63;
		double[] adouble = new double[3];
		double d0 = this.latencyYs[i][0];
		double d1 = Mth.wrapDegrees(this.latencyYs[j][0] - d0);
		adouble[0] = d0 + d1 * (double)partialTicks;
		d0 = this.latencyYs[i][1];
		d1 = this.latencyYs[j][1] - d0;
		adouble[1] = d0 + d1 * (double)partialTicks;
		adouble[2] = Mth.lerp((double)partialTicks, this.latencyYs[i][2], this.latencyYs[j][2]);
		return adouble;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (this.onGround())
		{
			this.setInSittingPose(true);
			this.setPose(Pose.SITTING);
		}
		else
		{
			this.setInSittingPose(false);
			this.setPose(Pose.STANDING);
		}
	}
	
	@Override
	public boolean isOrderedToSit()
	{
		return this.getPhase() == BabyEnderDragon.Phase.LAND;
	}
	
	@Override
	public EntityDimensions getDefaultDimensions(Pose pose)
	{
		if (pose == Pose.SITTING)
			return SITTING;
		return super.getDefaultDimensions(pose);
	}
	
	@Override
	public void aiStep()
	{
		super.aiStep();
		if (!this.isDeadOrDying())
		{
			if (this.latencyPointer < 0)
			{
				for (int i = 0; i < this.latencyYs.length; i++)
				{
					this.latencyYs[i][0] = this.getYRot();
					this.latencyYs[i][1] = this.getY();
				}
			}
			
			if (++this.latencyPointer == this.latencyYs.length)
				this.latencyPointer = 0;
			
			this.latencyYs[this.latencyPointer][0] = this.getYRot();
			this.latencyYs[this.latencyPointer][1] = this.getY();
		}
		
		this.oFlapTime = this.flapTime;
		Vec3 delta = this.getDeltaMovement();
		if (!this.onGround())
		{
			float flapPower = 0.2F / ((float)delta.horizontalDistance() * 2.0F + 1.0F);
			this.flapTime += flapPower;
		}
		if (this.level().isClientSide())
			this.processFlappingMovement();
		
		if (!this.level().isClientSide())
		{
			if (this.isTame())
			{
				if (this.tickCount % (this.isInSittingPose() ? 20 : 80) == 0)
					this.heal(0.5F);
				
				LivingEntity owner = this.getOwner();
				if (owner != null && EntitySelector.NO_SPECTATORS.test(owner) && !this.isOrderedToSit() && this.distanceTo(owner) > 64.0D)
					this.teleportToOwner();
			}
			
//			if (this.horizontalCollision || this.verticalCollision)
//			{
//				BlockPos.betweenClosedStream(this.getBoundingBox()).forEach(pos -> 
//				{
//					BlockState state = this.level().getBlockState(pos);
//					if (!state.is(BlockTags.DRAGON_IMMUNE))
//						this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
//				});
//			}
		}
	}
	
	private void teleportToOwner()
	{
		LivingEntity owner = this.getOwner();
		if (owner != null)
		{
			BlockPos blockpos = owner.blockPosition();
			
			for (int i = 0; i < 10; i++)
			{
				int j = this.randomIntInclusive(-3, 3);
				int k = this.randomIntInclusive(-1, 1);
				int l = this.randomIntInclusive(-3, 3);
				boolean flag = this.maybeTeleportTo(owner, blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
				if (flag)
					return;
			}
		}
	}
	
	private int randomIntInclusive(int min, int max)
	{
		return this.getRandom().nextInt(max - min + 1) + min;
	}

	private boolean maybeTeleportTo(LivingEntity owner, int x, int y, int z)
	{
		if (Math.abs((double) x - owner.getX()) < 2.0D && Math.abs((double) z - owner.getZ()) < 2.0D)
		{
			return false;
		}
		else if (!this.canTeleportTo(new BlockPos(x, y, z)))
		{
			return false;
		}
		else
		{
			this.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D, this.getYRot(), this.getXRot());
			return true;
		}
	}

	private boolean canTeleportTo(BlockPos pos)
	{
		PathType type = WalkNodeEvaluator.getPathTypeStatic(this, pos.mutable());
		if (type != PathType.WALKABLE)
		{
			return false;
		}
		else
		{
			BlockPos blockpos = pos.subtract(this.blockPosition());
			return this.level().noCollision(this, this.getBoundingBox().move(blockpos));
		}
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return SoundEvents.ENDER_DRAGON_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return SoundEvents.ENDER_DRAGON_HURT;
	}
	
	@Override
	public float getVoicePitch()
	{
		return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
	}
	
	public Vec3 getMoveTarget()
	{
		return this.moveTarget;
	}
	
	public void setMoveTarget(Vec3 moveTarget)
	{
		this.moveTarget = moveTarget;
	}
	
	public BlockPos getAnchor()
	{
		return this.anchor;
	}
	
	public void setAnchor(BlockPos anchor)
	{
		this.anchor = anchor;
	}
	
	public BabyEnderDragon.Phase getPhase()
	{
		return this.phase;
	}
	
	public void setPhase(BabyEnderDragon.Phase phase)
	{
		this.phase = phase;
	}
	
	protected boolean shouldDespawnInPeaceful()
	{
		return !this.isTame();
	}
	
	@Override
	public int getRemainingPersistentAngerTime()
	{
		return this.entityData.get(REMAINING_ANGER_TIME);
	}

	@Override
	public void setRemainingPersistentAngerTime(int time)
	{
		this.entityData.set(REMAINING_ANGER_TIME, time);
	}

	@Override
	public @Nullable UUID getPersistentAngerTarget()
	{
		return this.persistentAngerTarget;
	}

	@Override
	public void setPersistentAngerTarget(@Nullable UUID id)
	{
		this.persistentAngerTarget = id;
	}

	@Override
	public void startPersistentAngerTimer()
	{
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob)
	{
		return null;
	}
	
	@Override
	public boolean canMate(Animal animal)
	{
		return false;
	}
	
	@Override
	public boolean isFood(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public boolean isBaby()
	{
		return false;
	}
	
	@Override
	public boolean canBeLeashed(Player player)
	{
		return !this.isAngry() && super.canBeLeashed(player);
	}
	
	@Override
	protected Vec3 getLeashOffset()
	{
		return new Vec3(0.0F, this.getBbHeight() / 2.0F, 0.0F);
	}
	
	@Override
	protected int decreaseAirSupply(int supply)
	{
		return supply;
	}
	
	@Override
	public boolean canAttackType(EntityType<?> type)
	{
		return type != EnderTrigonEntityTypes.BABY_ENDER_DRAGON.get() && super.canAttackType(type);
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, SpawnGroupData groupData)
	{
		this.setAnchor(this.blockPosition().above(5));
		return super.finalizeSpawn(level, difficulty, type, groupData);
	}
	
	private static class DragonLookControl extends LookControl
	{
		public DragonLookControl(BabyEnderDragon dragon)
		{
			super(dragon);
		}
		
		@Override
		public void tick() {}
	}
	
	private static class DragonMoveControl extends MoveControl
	{
		private final BabyEnderDragon dragon;
		private float speed;
		
		public DragonMoveControl(BabyEnderDragon dragon)
		{
			super(dragon);
			this.dragon = dragon;
		}
		
		@Override
		public void tick()
		{
			double deltaX = this.dragon.moveTarget.x - this.dragon.getX();
			double deltaY = this.dragon.moveTarget.y - this.dragon.getY();
			double deltaZ = this.dragon.moveTarget.z - this.dragon.getZ();
			double horizontalDist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
			double dist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ + deltaY * deltaY);
			if (Math.abs(horizontalDist) > (double) 1.0E-5F)
			{
				if (this.dragon.getPhase() != BabyEnderDragon.Phase.LAND)
				{
					double d4 = 1.0D - Math.abs(deltaY * (double) 0.7F) / horizontalDist;
					deltaX *= d4;
					deltaZ *= d4;
					horizontalDist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
				}
//				if (this.dragon.getPhase() != BabyEnderDragon.Phase.LAND || horizontalDist > 1.5D)
//				{
					float f = this.dragon.getYRot();
					float f1 = (float) Mth.atan2(deltaZ, deltaX);
					float f2 = Mth.wrapDegrees(this.dragon.getYRot() + 90.0F);// + (this.dragon.horizontalCollision ? 25.0F : 0.0F);
					float f3 = Mth.wrapDegrees(f1 * (180F / (float) Math.PI));
					this.dragon.setYRot(Mth.approachDegrees(f2, f3, this.dragon.isTame() ? 10.0F : 4.0F) - 90.0F);
					this.dragon.yBodyRot = this.dragon.getYRot();
					if (Mth.degreesDifferenceAbs(f, this.dragon.getYRot()) < 3.0F)
						this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
					else
						this.speed = Mth.approach(this.speed, 0.8F, 0.25F);
					
					float f4 = (float) (-(Mth.atan2(-deltaY, horizontalDist) * (double) (180F / (float) Math.PI)));
					this.dragon.setXRot(f4);
					float f5 = this.dragon.getYRot() + 90.0F;
					double d6 = (double) (this.speed * Mth.cos(f5 * ((float) Math.PI / 180F))) * Math.abs(deltaX / dist);
					double d7 = (double) (this.speed * Mth.sin(f5 * ((float) Math.PI / 180F))) * Math.abs(deltaZ / dist);
					double d8 = (double) (this.speed * Mth.sin(f4 * ((float) Math.PI / 180F))) * Math.abs(deltaY / dist);
					Vec3 vec3 = this.dragon.getDeltaMovement();
					if (horizontalDist < 1.0D && this.dragon.getPhase() == BabyEnderDragon.Phase.LAND)
						this.dragon.setDeltaMovement(vec3.add(0.0D, -0.5D, 0.0D).subtract(vec3).scale(0.2D));
					else
						this.dragon.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2D)));
//				}
			}
		}
	}
	
	private static class DragonBodyControl extends BodyRotationControl
	{
		private final BabyEnderDragon dragon;
		
		public DragonBodyControl(BabyEnderDragon dragon)
		{
			super(dragon);
			this.dragon = dragon;
		}
		
		@Override
		public void clientTick()
		{
			this.dragon.yHeadRot = this.dragon.yBodyRot;
			this.dragon.yBodyRot = this.dragon.getYRot();
		}
	}
	
	public static enum Phase
	{
		CIRCLE,
		SWOOP,
		LAND
	}
	
	private static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
	{
		public TargetGoal(BabyEnderDragon dragon, Class<T> clazz, Predicate<LivingEntity> predicate)
		{
			super(dragon, clazz, false, predicate);
		}
		
		@Override
		protected AABB getTargetSearchArea(double reach)
		{
			return this.mob.getBoundingBox().inflate(reach);
		}
	}
}
