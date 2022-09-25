package theking530.staticpower.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticpower.items.StaticPowerItem;

public class CableNetworkAnalyzer extends StaticPowerItem {

	public CableNetworkAnalyzer() {
		super(new Item.Properties().stacksTo(1).setNoRepair());
	}

	@Override
	protected InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		// If on the server.
		if (!world.isClientSide) {
			// If we right clicked on a cable.
			if (CableNetworkManager.get(world).isTrackingCable(pos)) {
				// Get the cable.
				ServerCable cable = CableNetworkManager.get(world).getCable(pos);
				// Get all the messages that should be written to the output and write them.
				for (Component text : cable.getNetwork().getReaderOutput(pos)) {
					player.sendMessage(text, player.getUUID());
				}

				player.sendMessage(new TextComponent("Cable Location: ").append(pos.toString()), player.getUUID());
				player.sendMessage(new TextComponent("Cable Sparse Links: ").append("" + cable.getSparseLinks().size()), player.getUUID());

				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
}
