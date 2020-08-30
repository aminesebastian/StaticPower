package theking530.staticpower.tileentities.powered.squeezer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerSqueezer extends StaticPowerTileEntityContainer<TileEntitySqueezer> {

	public ContainerSqueezer(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntitySqueezer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSqueezer(int windowId, PlayerInventory playerInventory, TileEntitySqueezer owner) {
		super(ModContainerTypes.SQUEEZER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 52, 28));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 52));

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
