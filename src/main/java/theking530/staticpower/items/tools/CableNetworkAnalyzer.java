package theking530.staticpower.items.tools;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.items.StaticPowerItem;

public class CableNetworkAnalyzer extends StaticPowerItem {

	public CableNetworkAnalyzer(String name) {
		super(name, new Item.Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos,
			Direction face, PlayerEntity player, ItemStack item) {
		// If on the server.
		if (!world.isRemote) {
			// If we right clicked on a cable.
			if (CableNetworkManager.get(world).isTrackingCable(pos)) {
				// Get the cable.
				ServerCable cable = CableNetworkManager.get(world).getCable(pos);
				// Get all the messages that should be written to the output and write them.
				for (ITextComponent text : cable.getNetwork().getReaderOutput()) {
					player.sendMessage(text, player.getUniqueID());
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
}
