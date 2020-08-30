package theking530.staticpower.tileentities.powered.fermenter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerFermenter extends StaticPowerTileEntityContainer<TileEntityFermenter> {

	public ContainerFermenter(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityFermenter) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFermenter(int windowId, PlayerInventory playerInventory, TileEntityFermenter owner) {
		super(ModContainerTypes.FERMENTER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, j + i * 3, 40 + j * 18, 21 + i * 18));
			}
		}
		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 115, 55));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
