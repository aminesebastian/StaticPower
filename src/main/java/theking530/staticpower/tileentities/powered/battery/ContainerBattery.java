package theking530.staticpower.tileentities.powered.battery;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerBattery extends StaticPowerTileEntityContainer<TileEntityBattery> {

	public ContainerBattery(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityBattery) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerBattery(int windowId, PlayerInventory playerInventory, TileEntityBattery owner) {
		super(ModContainerTypes.BATTERY_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
