package nonamecrackers2.endertrigon.common.entity.goal;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import nonamecrackers2.endertrigon.common.entity.BabyEnderDragon;

public class BabyEnderDragonFollowOwnerGoal extends Goal
{
	private static final int UPDATE_INTERVAL = 80;
	private final BabyEnderDragon dragon;
	private @Nullable LivingEntity owner;
	private int nextUpdate = UPDATE_INTERVAL;
	
	public BabyEnderDragonFollowOwnerGoal(BabyEnderDragon dragon)
	{
		this.dragon = dragon;
	}
	
	@Override
	public boolean canUse()
	{
		if (this.nextUpdate > 0)
			this.nextUpdate--;
		this.owner = this.dragon.getOwner();
		return this.dragon.isTame() && this.owner != null && EntitySelector.NO_SPECTATORS.test(this.owner) && (this.nextUpdate == 0 || this.dragon.distanceTo(this.owner) > 32.0D) && this.dragon.getPhase() != BabyEnderDragon.Phase.LAND;
	}
	
	@Override
	public void start()
	{
		this.nextUpdate = UPDATE_INTERVAL;
	}
	
	@Override
	public void tick()
	{
		BlockPos pos = this.dragon.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.owner.blockPosition());
		int heightDiff = pos.getY() - this.owner.getBlockY();
		if (heightDiff >= 0 && heightDiff <= 16)
		{
			this.dragon.setAnchor(pos.above(10));
		}
		else
		{
			int ceiling = getCeilingStartingAt(this.dragon.level(), this.owner.getBlockY(), this.owner.getBlockX(), this.owner.getBlockZ());
			int ceilingDiff = ceiling - this.owner.getBlockY();
			ceiling = Mth.clamp(ceilingDiff, 0, 10);
			this.dragon.setAnchor(this.owner.blockPosition().above(ceiling));
		}
	}
	
	public static int getCeilingStartingAt(Level level, int height, int x, int z)
	{
		BlockPos pos = new BlockPos(x, height, z);
		while (pos.getY() < level.getMaxBuildHeight() && level.getBlockState(pos).isAir())
			pos = pos.above();
		return pos.getY();
	}
}
