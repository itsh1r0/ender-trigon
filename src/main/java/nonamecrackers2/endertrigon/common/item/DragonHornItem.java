package nonamecrackers2.endertrigon.common.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nonamecrackers2.endertrigon.common.entity.BabyEnderDragon;

public class DragonHornItem extends InstrumentItem
{
	public DragonHornItem(Item.Properties properties, TagKey<Instrument> instruments)
	{
		super(properties, instruments);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var result = super.use(level, player, hand);
		if (!level.isClientSide() && result.getResult() == InteractionResult.CONSUME)
		{
			AABB box = player.getBoundingBox().inflate(32.0F);
			var dragons = player.level().getEntitiesOfClass(BabyEnderDragon.class, box, d -> d.distanceTo(player) <= 32.0F);
			int total = dragons.size();
			int totalSitting = 0;
			for (BabyEnderDragon dragon : dragons)
			{
				if (dragon.isOrderedToSit())
					totalSitting++;
			}
			for (BabyEnderDragon dragon : dragons)
			{
				if (player.getUUID().equals(dragon.getOwnerUUID()))
				{
					if ((float)totalSitting / (float)total < 0.5F)
					{
						for (int i = 0; i < 10; i++)
						{
							int x = dragon.getRandom().nextIntBetweenInclusive(-3, 3);
							int y = dragon.getRandom().nextIntBetweenInclusive(-1, 1);
							int z = dragon.getRandom().nextIntBetweenInclusive(-3, 3);
							BlockPos pos = player.blockPosition().offset(x, y, z);
							BlockPathTypes pathType = WalkNodeEvaluator.getBlockPathTypeStatic(level, pos.mutable());
							if (pathType == BlockPathTypes.WALKABLE)
							{
								if (level.noCollision(dragon, dragon.getLocalBoundsForPose(Pose.SITTING).move(pos)))
								{
									dragon.setPhase(BabyEnderDragon.Phase.LAND);
									dragon.setMoveTarget(Vec3.atBottomCenterOf(pos.above(1)));
								}
							}
						}
					}
					else
					{
						dragon.setPhase(BabyEnderDragon.Phase.CIRCLE);
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> text, TooltipFlag flag)
	{
		text.add(Component.translatable("block.endertrigon.dragon_horn.use").withStyle(ChatFormatting.GRAY));
	}
}
