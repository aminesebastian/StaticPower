package theking530.staticpower.tileentities.powered.pump;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerPump extends StaticPowerTileEntityContainer<TileEntityPump> {

	public ContainerPump(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityPump) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPump(int windowId, PlayerInventory playerInventory, TileEntityPump owner) {
		super(ModContainerTypes.PUMP_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// FluidContainerDrainSlots
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 0, 133, 20));
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.WATER_BUCKET, 1, 133, 56));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 58));

		addPlayerInventory(getPlayerInventory(), 8, 78);
		addPlayerHotbar(getPlayerInventory(), 8, 136);
	}
}
