package theking530.staticpower.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
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
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (!world.isClientSide()) {
			if (isPendingSecondLocation(item)) {
				if (world.getBlockState(pos).getBlock() instanceof BlockWireConnector) {
					BlockPos initialLocation = getFirstLocation(item);
					if (!(world.getBlockState(initialLocation).getBlock() instanceof BlockWireConnector)) {
						clearPendingLocation(item);
						MutableComponent message = new TextComponent(String.format("Wire connector no longer exists at initial position %1$s .", initialLocation.toShortString()));
						player.sendMessage(message, player.getUUID());
						return InteractionResult.FAIL;
					}
					BlockEntityWireConnector connector = (BlockEntityWireConnector) world.getBlockEntity(pos);
					connector.addConnectedConnector(initialLocation);

					MutableComponent message = new TextComponent(
							String.format("Linked wire connector at location %1$s to position %2$s.", pos.toShortString(), initialLocation.toShortString()));
					player.sendMessage(message, player.getUUID());
					clearPendingLocation(item);
					return InteractionResult.SUCCESS;
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

		return InteractionResult.PASS;
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
