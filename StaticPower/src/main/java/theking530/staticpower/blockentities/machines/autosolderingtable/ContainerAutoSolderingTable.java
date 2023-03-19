package theking530.staticpower.blockentities.machines.autosolderingtable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.blockentities.nonpowered.solderingtable.AbstractContainerSolderingTable;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerAutoSolderingTable extends AbstractContainerSolderingTable<BlockEntityAutoSolderingTable> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerAutoSolderingTable, GuiAutoSolderingTable> TYPE = new ContainerTypeAllocator<>("machine_industrial_soldering_table",
			ContainerAutoSolderingTable::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiAutoSolderingTable::new);
		}
	}

	public ContainerAutoSolderingTable(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityAutoSolderingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutoSolderingTable(int windowId, Inventory playerInventory, BlockEntityAutoSolderingTable owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		enableSolderingIronSlot = false;
		super.initializeContainer();

		// Add the soldering iron slot.
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 57));
	}

	@Override
	protected void addOutputSlot() {
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 129, 38));
	}
}
