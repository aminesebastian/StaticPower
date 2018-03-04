package theking530.staticpower.tileentity.solderingtable;

import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class SlotSolderingTableInput extends StaticPowerContainerSlot{

	TileEntitySolderingTable TABLE;
	ContainerSolderingTable CONTAINER;
	
	public SlotSolderingTableInput(ContainerSolderingTable container,  IItemHandler teTable, int i, int j, int k) {
		super(teTable, i, j, k);
		CONTAINER = container;
	}
    public void onSlotChanged() {
        super.onSlotChanged();
        CONTAINER.onSolderingAreaChanged();
    }
}
