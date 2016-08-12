package theking530.staticpower.machines.solderingtable;

import net.minecraft.inventory.Slot;

public class SlotSolderingTableInput extends Slot{

	TileEntitySolderingTable TABLE;
	ContainerSolderingTable CONTAINER;
	
	public SlotSolderingTableInput(ContainerSolderingTable container,  TileEntitySolderingTable teTable, int i, int j, int k) {
		super(teTable, i, j, k);
		CONTAINER = container;
	}
    public void onSlotChanged() {
        super.onSlotChanged();
        CONTAINER.onSolderingAreaChanged();
    }
}
