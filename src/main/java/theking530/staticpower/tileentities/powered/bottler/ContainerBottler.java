package theking530.staticpower.tileentities.powered.bottler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerBottler extends StaticPowerTileEntityContainer<TileEntityBottler> {

	public ContainerBottler(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityBottler) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerBottler(int windowId, PlayerInventory playerInventory, TileEntityBottler owner) {
		super(ModContainerTypes.BOTTLER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 108, 28));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 109, 58));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
