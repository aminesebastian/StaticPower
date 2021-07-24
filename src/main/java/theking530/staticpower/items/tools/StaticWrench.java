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
import theking530.api.wrench.IWrenchTool;
import theking530.api.wrench.IWrenchable;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticpower.items.StaticPowerItem;

public class StaticWrench extends StaticPowerItem implements IWrenchTool {

	public StaticWrench(String name) {
		super(name, new Item.Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemStack = playerIn.getHeldItem(hand);
		if (!worldIn.isRemote) {
			if (playerIn.isSneaking()) {
				if (getWrenchMode(itemStack).ordinal() + 1 <= RegularWrenchMode.values().length - 1) {
					setWrenchMode(itemStack, RegularWrenchMode.values()[getWrenchMode(itemStack).ordinal() + 1]);
					playerIn.sendMessage(new StringTextComponent("Wrench Mode: " + getWrenchMode(itemStack).toString()), playerIn.getUniqueID());
				} else {
					setWrenchMode(itemStack, RegularWrenchMode.values()[0]);
					playerIn.sendMessage(new StringTextComponent("Wrench Mode: " + getWrenchMode(itemStack).toString()), playerIn.getUniqueID());
				}
			}
		}
		return ActionResult.resultPass(itemStack);
	}

	@Override
	protected ActionResultType onPreStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		if (item != null) {
			// We will check two places for a block to wrench.
			// First, we check the block we hit. If that is not wrenchable, then,
			// we check the block normal to the surface that we hit.
			IWrenchable wrenchable = null;
			BlockPos wrenchablePos = pos;
			Direction wrenchableFace = face;
			if (world.getBlockState(pos).getBlock() instanceof IWrenchable) {
				wrenchable = (IWrenchable) world.getBlockState(pos).getBlock();
			} else if (world.getBlockState(pos.offset(face)).getBlock() instanceof IWrenchable) {
				wrenchablePos = pos.offset(face);
				wrenchableFace = face.getOpposite();
				wrenchable = (IWrenchable) world.getBlockState(wrenchablePos).getBlock();
			}

			// If we found a wrenchable using the above logic, do the work.
			if (wrenchable != null) {
				player.swingArm(Hand.MAIN_HAND);
				if (!player.isSneaking()) {
					ActionResultType result = wrenchable.wrenchBlock(player, getWrenchMode(item), item, world, wrenchablePos, wrenchableFace, true);
					if (result == ActionResultType.SUCCESS) {
						playWrenchSound(world, wrenchablePos);
						return result;
					}
				} else {
					if (world.getBlockState(pos).getBlock() instanceof IWrenchable) {
						ActionResultType result = wrenchable.sneakWrenchBlock(player, getSneakWrenchMode(item), item, world, wrenchablePos, wrenchableFace, true);
						if (result == ActionResultType.SUCCESS) {
							playWrenchSound(world, wrenchablePos);
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

		// If we made it here, we pass control back to the game to perform regular
		// right-click behaviour.
		return ActionResultType.PASS;
	}

	public void playWrenchSound(World world, BlockPos pos) {
		world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
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
