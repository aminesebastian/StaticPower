package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.init.ModContainerTypes;

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
		addSlot(new FluidContainerSlot(getTileEntity().inputFluidContainerComponent, Items.WATER_BUCKET, 0, 30, 31));
		addSlot(new FluidContainerSlot(getTileEntity().inputFluidContainerComponent, Items.BUCKET, 1, 30, 63));

		// FluidContainerFillSlots
		addSlot(new FluidContainerSlot(getTileEntity().outputFluidContainerComponent, Items.BUCKET, 0, 130, 31));
		addSlot(new FluidContainerSlot(getTileEntity().outputFluidContainerComponent, Items.WATER_BUCKET, 1, 130, 63));

		addPlayerInventory(getPlayerInventory(), 8, 103);
		addPlayerHotbar(getPlayerInventory(), 8, 161);
	}
}
