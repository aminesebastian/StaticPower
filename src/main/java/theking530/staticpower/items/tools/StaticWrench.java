package theking530.staticpower.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import theking530.api.wrench.IWrenchTool;
import theking530.api.wrench.IWrenchable;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticpower.items.StaticPowerItem;

public class StaticWrench extends StaticPowerItem implements IWrenchTool {

	public StaticWrench() {
		super(new Item.Properties().stacksTo(1).setNoRepair());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
		ItemStack itemStack = playerIn.getItemInHand(hand);
		if (!worldIn.isClientSide) {
			if (playerIn.isShiftKeyDown()) {
				if (getWrenchMode(itemStack).ordinal() + 1 <= RegularWrenchMode.values().length - 1) {
					setWrenchMode(itemStack, RegularWrenchMode.values()[getWrenchMode(itemStack).ordinal() + 1]);
					playerIn.sendMessage(new TextComponent("Wrench Mode: " + getWrenchMode(itemStack).toString()), playerIn.getUUID());
				} else {
					setWrenchMode(itemStack, RegularWrenchMode.values()[0]);
					playerIn.sendMessage(new TextComponent("Wrench Mode: " + getWrenchMode(itemStack).toString()), playerIn.getUUID());
				}
			}
		}
		return InteractionResultHolder.pass(itemStack);
	}

	@Override
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (item != null) {
			// We will check two places for a block to wrench.
			// First, we check the block we hit. If that is not wrenchable, then,
			// we check the block normal to the surface that we hit.
			IWrenchable wrenchable = null;
			BlockPos wrenchablePos = pos;
			Direction wrenchableFace = face;
			if (world.getBlockState(pos).getBlock() instanceof IWrenchable) {
				wrenchable = (IWrenchable) world.getBlockState(pos).getBlock();
			} else if (world.getBlockState(pos.relative(face)).getBlock() instanceof IWrenchable) {
				wrenchablePos = pos.relative(face);
				wrenchableFace = face.getOpposite();
				wrenchable = (IWrenchable) world.getBlockState(wrenchablePos).getBlock();
			}

			// If we found a wrenchable using the above logic, do the work.
			if (wrenchable != null) {
				player.swing(InteractionHand.MAIN_HAND);
				if (!player.isShiftKeyDown()) {
					InteractionResult result = wrenchable.wrenchBlock(player, getWrenchMode(item), item, world, wrenchablePos, wrenchableFace, true);
					if (result == InteractionResult.SUCCESS) {
						playWrenchSound(world, wrenchablePos);
					}
					return result;
				} else {
					// Make sure its only the block we're actually hitting that is wrenchable. Up
					// top we also check for the relative face.
					if (world.getBlockState(pos).getBlock() instanceof IWrenchable) {
						InteractionResult result = wrenchable.sneakWrenchBlock(player, getSneakWrenchMode(item), item, world, wrenchablePos, wrenchableFace, true);
						if (result == InteractionResult.SUCCESS) {
							playWrenchSound(world, wrenchablePos);
						}
						return result;
					}
				}
			} else if (world.getBlockState(pos).getProperties().contains(BlockStateProperties.AXIS) && getWrenchMode(item) == RegularWrenchMode.ROTATE) {
				// If on the server, rotate the block to face the axis that was rightclicked by
				// the wrench.
				BlockState rotatedState = world.getBlockState(pos);
				if (rotatedState.getValue(BlockStateProperties.AXIS) != face.getAxis()) {
					if (!world.isClientSide) {
						rotatedState = rotatedState.setValue(BlockStateProperties.AXIS, face.getAxis());
						world.setBlock(pos, rotatedState, 1 | 2);
					}
				}
				world.playSound(null, pos, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 0.5F, (float) (1.0F + Math.random() * 0.5));
				return InteractionResult.SUCCESS;
			}
		}

		// If we made it here, we pass control back to the game to perform regular
		// right-click behaviour.
		return InteractionResult.PASS;
	}

	public void playWrenchSound(Level world, BlockPos pos) {
		world.playSound(null, pos, SoundEvents.LANTERN_HIT, SoundSource.BLOCKS, 0.35F, (float) (0.5F + Math.random() * 0.5));
		world.playSound(null, pos, SoundEvents.SPYGLASS_USE, SoundSource.BLOCKS, 1.5f, 1f);
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
			CompoundTag nbt = new CompoundTag();
			stack.setTag(nbt);
			stack.getTag().putInt("REGULAR", mode.ordinal());
		}
	}
}
