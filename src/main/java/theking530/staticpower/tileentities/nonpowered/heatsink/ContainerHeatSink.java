package theking530.staticpower.tileentities.nonpowered.heatsink;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerHeatSink extends StaticPowerTileEntityContainer<TileEntityHeatSink> {

	public ContainerHeatSink(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityHeatSink) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerHeatSink(int windowId, PlayerInventory playerInventory, TileEntityHeatSink owner) {
		super(ModContainerTypes.HEAT_SINK_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addPlayerInventory(getPlayerInventory(), 8, 64);
		this.addPlayerHotbar(getPlayerInventory(), 8, 122);
	}
}
