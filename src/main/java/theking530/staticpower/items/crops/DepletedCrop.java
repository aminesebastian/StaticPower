package theking530.staticpower.items.crops;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.items.StaticPowerItem;

public class DepletedCrop extends StaticPowerItem {

	public DepletedCrop() {
		super();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent("Strangly Fertilizing..."));
	}

	/**
	 * Attempts to use this as bonemeal on the targeted crop.
	 */
	public InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (!player.mayUseItemAt(pos.relative(face), face, item)) {
			return InteractionResult.FAIL;
		} else {
			if (applyBonemeal(item, world, pos, player)) {
				if (!world.isClientSide) {
					world.levelEvent(2005, pos, 0);
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}
	}

	/**
	 * Attempts to apply bonemeal.
	 * 
	 * @param stack
	 * @param worldIn
	 * @param target
	 * @param player
	 * @return
	 */
	private boolean applyBonemeal(ItemStack stack, Level worldIn, BlockPos target, Player player) {
		BlockState BlockState = worldIn.getBlockState(target);

		int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, worldIn, target, BlockState, stack);
		if (hook != 0)
			return hook > 0;

		if (BlockState.getBlock() instanceof BonemealableBlock) {
			BonemealableBlock igrowable = (BonemealableBlock) BlockState.getBlock();

			if (igrowable.isValidBonemealTarget(worldIn, target, BlockState, worldIn.isClientSide)) {
				if (!worldIn.isClientSide) {
					if (igrowable.isBonemealSuccess(worldIn, worldIn.random, target, BlockState)) {
						igrowable.performBonemeal((ServerLevel) worldIn, worldIn.random, target, BlockState);
					}

					stack.shrink(1);
				}

				return true;
			}
		}

		return false;
	}
}
