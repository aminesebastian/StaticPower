package theking530.staticpower.items.crops;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.items.StaticPowerItem;

public class DepletedCrop extends StaticPowerItem {

	public DepletedCrop(String name) {
		super(name);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent("Strangly Fertilizing..."));
	}

	/**
	 * Attempts to use this as bonemeal on the targeted crop.
	 */
	public ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		if (!player.canPlayerEdit(pos.offset(face), face, item)) {
			return ActionResultType.FAIL;
		} else {
			if (applyBonemeal(item, world, pos, player)) {
				if (!world.isRemote) {
					world.playEvent(2005, pos, 0);
				}
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.PASS;
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
	private boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target, PlayerEntity player) {
		BlockState BlockState = worldIn.getBlockState(target);

		int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, worldIn, target, BlockState, stack);
		if (hook != 0)
			return hook > 0;

		if (BlockState.getBlock() instanceof IGrowable) {
			IGrowable igrowable = (IGrowable) BlockState.getBlock();

			if (igrowable.canGrow(worldIn, target, BlockState, worldIn.isRemote)) {
				if (!worldIn.isRemote) {
					if (igrowable.canUseBonemeal(worldIn, worldIn.rand, target, BlockState)) {
						igrowable.grow((ServerWorld) worldIn, worldIn.rand, target, BlockState);
					}

					stack.shrink(1);
				}

				return true;
			}
		}

		return false;
	}
}
