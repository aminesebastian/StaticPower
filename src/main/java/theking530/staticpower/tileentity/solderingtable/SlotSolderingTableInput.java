package theking530.staticpower.tileentity.solderingtable;

import net.minecraft.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotSolderingTableInput extends SlotItemHandler{

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
