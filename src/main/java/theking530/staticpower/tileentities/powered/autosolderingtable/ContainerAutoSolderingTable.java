package theking530.staticpower.tileentities.powered.autosolderingtable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.tileentities.nonpowered.solderingtable.AbstractContainerSolderingTable;

public class ContainerAutoSolderingTable extends AbstractContainerSolderingTable<TileEntityAutoSolderingTable> {

	public ContainerAutoSolderingTable(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityAutoSolderingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutoSolderingTable(int windowId, PlayerInventory playerInventory, TileEntityAutoSolderingTable owner) {
		super(ModContainerTypes.AUTO_SOLDERING_TABLE_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		enableSolderingIronSlot = false;
		craftingGridXOffset = -18;
		super.initializeContainer();

		// Add the soldering iron slot.
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 57));
	}

	@Override
	protected void addOutputSlot() {
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 129, 38));
	}
}
