package theking530.staticpower.tileentities.utilities.interfaces;

import net.minecraft.item.ItemStack;

public interface ItemStackHandlerFilter {

    public boolean canInsertItem(int slot, ItemStack stack);
    public boolean canExtractItem(int slot, int amount);
    
}
