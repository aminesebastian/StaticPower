package theking530.api.item.compound.capability;

import java.util.Collection;

import net.minecraft.world.item.ItemStack;

public interface ICompoundItem {
	public ItemStack getBaseItem();

	public ItemStack getPartInSlot(int slotIndex);

	public CompoundItemSlotState getSlot(int slotIndex);

	public Collection<CompoundItemSlotState> getSlots();

	public boolean canApplyPartToSlot(int slotIndex, ItemStack part);

	public boolean tryApplyPartToSlot(int slotIndex, ItemStack part);

	public boolean canRemovePartFromSlot(int slotIndex);

	public ItemStack tryRemovePartFromSlot(int slotIndex);

	public boolean isSlotPopulated(int slotIndex);

	public boolean isComplete();

	public int getDamage();

	public int getMaxDamage();
}
