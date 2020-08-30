package theking530.staticpower.tileentities.digistorenetwork.severrack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerDigistoreServerRack extends StaticPowerTileEntityContainer<TileEntityDigistoreServerRack> {

	public ContainerDigistoreServerRack(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreServerRack) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreServerRack(int windowId, PlayerInventory playerInventory, TileEntityDigistoreServerRack owner) {
		super(ModContainerTypes.DIGISTORE_SERVER_RACK, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 0, 65, 20));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 1, 65, 40));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 2, 65, 60));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 3, 65, 80));

		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 4, 93, 20));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 5, 93, 40));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 6, 93, 60));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 7, 93, 80));

		addPlayerHotbar(getPlayerInventory(), 8, 161);
		addPlayerInventory(getPlayerInventory(), 8, 103);
	}
}
