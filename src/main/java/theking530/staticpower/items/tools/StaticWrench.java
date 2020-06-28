package theking530.staticpower.items.tools;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import theking530.common.wrench.IWrenchTool;
import theking530.common.wrench.IWrenchable;
import theking530.common.wrench.RegularWrenchMode;
import theking530.common.wrench.SneakWrenchMode;
import theking530.staticpower.items.StaticPowerItem;

public class StaticWrench extends StaticPowerItem implements IWrenchTool {

	public StaticWrench(String name) {
		super(name, new Item.Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemStack = playerIn.getHeldItem(hand);
		if (!worldIn.isRemote) {
			if (!playerIn.isSneaking()) {
				if (getWrenchMode(itemStack).ordinal() + 1 <= RegularWrenchMode.values().length - 1) {
					setWrenchMode(itemStack, RegularWrenchMode.values()[getWrenchMode(itemStack).ordinal() + 1]);
					playerIn.sendMessage(new StringTextComponent("Wrench Mode: " + getWrenchMode(itemStack).toString()));
				} else {
					setWrenchMode(itemStack, RegularWrenchMode.values()[0]);
					playerIn.sendMessage(new StringTextComponent("Wrench Mode: " + getWrenchMode(itemStack).toString()));
				}
			}
		}
		return ActionResult.resultPass(itemStack);
	}

	@Override
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		if (item != null) {
			if (world.getBlockState(pos).getBlock() instanceof IWrenchable) {
				IWrenchable block = (IWrenchable) world.getBlockState(pos).getBlock();
				player.swingArm(Hand.MAIN_HAND);
				if (!player.isSneaking()) {
					ActionResultType result = block.wrenchBlock(player, getWrenchMode(item), item, world, pos, face, true);
					if (result == ActionResultType.SUCCESS) {
						playWrenchSound(world, pos);
						return result;
					}
				} else {
					if (world.getBlockState(pos).getBlock() instanceof IWrenchable) {
						ActionResultType result = block.sneakWrenchBlock(player, getSneakWrenchMode(item), item, world, pos, face, true);
						if (result == ActionResultType.SUCCESS) {
							playWrenchSound(world, pos);
							return result;
						}
					}
				}
			} else if (world.getBlockState(pos).getProperties().contains(BlockStateProperties.AXIS) && getWrenchMode(item) == RegularWrenchMode.ROTATE) {
				// If on the server, rotate the block to face the axis that was rightclicked by
				// the wrench.
				BlockState rotatedState = world.getBlockState(pos);
				if (rotatedState.get(BlockStateProperties.AXIS) != face.getAxis()) {
					if (!world.isRemote) {
						rotatedState = rotatedState.with(BlockStateProperties.AXIS, face.getAxis());
						world.setBlockState(pos, rotatedState, 1 | 2);
					}
				}
				world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 0.5F, (float) (1.0F + Math.random() * 0.5));
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}

	public void playWrenchSound(World world, BlockPos pos) {
		if(world.isRemote) {
			world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
		}
	}

	@Override
	public RegularWrenchMode getWrenchMode(ItemStack stack) {
		if (stack.hasTag()) {
			return RegularWrenchMode.values()[stack.getTag().getInt("REGULAR")];
		}
		return RegularWrenchMode.TOGGLE_SIDES;
	}

	@Override
	public SneakWrenchMode getSneakWrenchMode(ItemStack stack) {
		return SneakWrenchMode.DISMANTE;
	}

	public void setWrenchMode(ItemStack stack, RegularWrenchMode mode) {
		if (stack.hasTag()) {
			stack.getTag().putInt("REGULAR", mode.ordinal());
		} else {
			CompoundNBT nbt = new CompoundNBT();
			stack.setTag(nbt);
			stack.getTag().putInt("REGULAR", mode.ordinal());
		}
	}
}
