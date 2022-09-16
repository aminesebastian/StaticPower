package theking530.staticpower.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import theking530.staticpower.blockentities.power.wireconnector.BlockEntityWireConnector;
import theking530.staticpower.blockentities.power.wireconnector.BlockWireConnector;

public class WireCoil extends StaticPowerItem {
	private static final String INITIAL_LOCATOIN_TAG_NAME = "initial_connecting_point";

	@Override
	public Component getName(ItemStack item) {
		Component superCall = super.getName(item);
		if (isPendingSecondLocation(item)) {
			BlockPos initialLocation = getFirstLocation(item);
			return new TranslatableComponent(superCall.getString()).append(": ")
					.append(new TranslatableComponent("gui.staticpower.wire_coil_connecting", initialLocation.toShortString()));
		} else {
			return superCall;
		}
	}

	@Override
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (!world.isClientSide()) {
			if (player.isCrouching()) {
				item.removeTagKey(INITIAL_LOCATOIN_TAG_NAME);
				return InteractionResult.SUCCESS;
			} else {
				if (isPendingSecondLocation(item)) {
					if (world.getBlockState(pos).getBlock() instanceof BlockWireConnector) {
						BlockPos initialLocation = getFirstLocation(item);
						if (!(world.getBlockState(initialLocation).getBlock() instanceof BlockWireConnector)) {
							clearPendingLocation(item);
							MutableComponent message = new TextComponent(
									String.format("Wire connector no longer exists at initial position %1$s .", initialLocation.toShortString()));
							player.sendMessage(message, player.getUUID());
							return InteractionResult.FAIL;
						}

						BlockEntityWireConnector connector = (BlockEntityWireConnector) world.getBlockEntity(pos);
						if (connector.addConnectedConnector(initialLocation, getLinkDataTag(context, world, pos, face, player, item)) != null) {
							if (!player.isCreative()) {
								item.shrink(1);
							}

							MutableComponent message = new TextComponent(
									String.format("Linked wire connector at location %1$s to position %2$s.", pos.toShortString(), initialLocation.toShortString()));
							player.sendMessage(message, player.getUUID());
							clearPendingLocation(item);
							return InteractionResult.SUCCESS;
						}
					}
				} else {
					if (world.getBlockState(pos).getBlock() instanceof BlockWireConnector) {
						setFirstSampleLocation(item, pos);
						MutableComponent message = new TextComponent(String.format("First connection position set to %1$s .", pos.toShortString()));
						player.sendMessage(message, player.getUUID());
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		return InteractionResult.PASS;
	}

	protected CompoundTag getLinkDataTag(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		CompoundTag output = new CompoundTag();

		ItemStack wire = item.copy();
		wire.setCount(1);

		// Make sure we clear this first before we upload the item to connector data
		// tag.
		wire.removeTagKey(INITIAL_LOCATOIN_TAG_NAME);

		output.put("wire", wire.serializeNBT());
		return output;
	}

	public void setFirstSampleLocation(ItemStack stack, BlockPos pos) {
		stack.getOrCreateTag().putLong(INITIAL_LOCATOIN_TAG_NAME, pos.asLong());
	}

	public BlockPos getFirstLocation(ItemStack stack) {
		return BlockPos.of(stack.getTag().getLong(INITIAL_LOCATOIN_TAG_NAME));
	}

	public boolean isPendingSecondLocation(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(INITIAL_LOCATOIN_TAG_NAME);
	}

	public void clearPendingLocation(ItemStack stack) {
		stack.removeTagKey(INITIAL_LOCATOIN_TAG_NAME);
	}
}
