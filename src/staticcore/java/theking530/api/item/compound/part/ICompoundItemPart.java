package theking530.api.item.compound.part;

import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import theking530.api.item.compound.slot.CompoundItemSlot;

public interface ICompoundItemPart {
	public boolean canApplyToItem(ItemStack item, ItemStack part, CompoundItemSlot slot);

	public Collection<Supplier<CompoundItemSlot>> getFullfilledSlots(ItemStack part);
}
