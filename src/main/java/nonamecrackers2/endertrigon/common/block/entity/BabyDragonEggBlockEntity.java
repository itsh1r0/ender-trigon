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

package nonamecrackers2.endertrigon.common.block.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import nonamecrackers2.endertrigon.common.entity.BabyEnderDragon;
import nonamecrackers2.endertrigon.common.init.EnderTrigonBlockEntityTypes;
import nonamecrackers2.endertrigon.common.init.EnderTrigonEntityTypes;
import nonamecrackers2.endertrigon.common.init.EnderTrigonSoundEvents;

public class BabyDragonEggBlockEntity extends BlockEntity
{
	private int timeTillSpawn;
	private @Nullable UUID hatcher;
	
	public BabyDragonEggBlockEntity(BlockPos pos, BlockState state)
	{
		super(EnderTrigonBlockEntityTypes.BABY_DRAGON_EGG.get(), pos, state);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, BabyDragonEggBlockEntity entity)
	{
		if (!level.isClientSide && level instanceof ServerLevel serverLevel)
		{
			if (entity.timeTillSpawn > 0)
			{
				entity.timeTillSpawn--;
				if (entity.timeTillSpawn == 0)
				{
					BabyEnderDragon dragon = EnderTrigonEntityTypes.BABY_ENDER_DRAGON.get().spawn(serverLevel, (ItemStack)null, (Player)null, pos, MobSpawnType.TRIGGERED, true, false);
					dragon.playSound(SoundEvents.ENDER_DRAGON_GROWL, 3.0F, dragon.getVoicePitch());
					if (entity.hatcher != null)
					{
						dragon.setTame(true);
						dragon.setOwnerUUID(entity.hatcher);
						if (((ServerLevel)level).getEntity(entity.hatcher) instanceof ServerPlayer player)
							CriteriaTriggers.TAME_ANIMAL.trigger(player, dragon);
						dragon.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Tamed health modifier", 16.0D, AttributeModifier.Operation.ADDITION));
					}
					serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 10, 0.5F, 0.5F, 0.5F, 0.05D);
					level.playSound(null, pos, EnderTrigonSoundEvents.BABY_DRAGON_EGG_BREAKS.get(), SoundSource.BLOCKS, 5.0F, 1.0F);
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
					level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
				}
			}
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.putInt("TimeTillSpawn", this.timeTillSpawn);
		if (this.hatcher != null)
			tag.putUUID("Hatcher", this.hatcher);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		this.timeTillSpawn = tag.getInt("TimeTillSpawn");
		if (tag.hasUUID("Hatcher"))
			this.hatcher = tag.getUUID("Hatcher");
	}
	
	public void setTimeTillSpawn(int time)
	{
		this.timeTillSpawn = time;
	}
	
	public void setHatcher(@Nullable UUID uuid)
	{
		this.hatcher = uuid;
	}
}
