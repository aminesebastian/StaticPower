package theking530.staticpower.tileentities.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerDigistoreManager extends StaticPowerTileEntityContainer<TileEntityDigistoreManager> {

	public ContainerDigistoreManager(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreManager) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreManager(int windowId, PlayerInventory playerInventory, TileEntityDigistoreManager owner) {
		super(ModContainerTypes.DIGISTORE_MANAGER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() { // Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
