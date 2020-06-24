package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.machines.tileentitycomponents.slots.FluidContainerSlot;

public class ContainerTank extends StaticPowerTileEntityContainer<TileEntityTank> {

	public ContainerTank(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityTank) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTank(int windowId, PlayerInventory playerInventory, TileEntityTank owner) {
		super(ModContainerTypes.TANK_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// FluidContainerDrainSlots
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.WATER_BUCKET, 0, 30, 31));
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 1, 30, 63));

		// FluidContainerFillSlots
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 2, 130, 31));
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.WATER_BUCKET, 3, 130, 63));

		addPlayerInventory(getPlayerInventory(), 8, 103);
		addPlayerHotbar(getPlayerInventory(), 8, 161);
	}
}
