package theking530.staticcore.container.slots;

import java.util.Optional;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import theking530.api.item.compound.capability.ICompoundItem;
import theking530.api.item.compound.slot.CompoundItemSlot;

public class CompoundItemPartSlot extends Slot {
	private static Container emptyInventory = new SimpleContainer(0);
	private final ICompoundItem compoundItem;
	private final int partSlotIndex;
	private final CompoundItemSlot itemSlot;

	public CompoundItemPartSlot(ICompoundItem compoundItem, int partSlotIndex, int slot, int x, int y) {
		super(emptyInventory, slot, x, y);
		this.compoundItem = compoundItem;
		this.partSlotIndex = partSlotIndex;
		this.itemSlot = compoundItem.getSlot(partSlotIndex).getSlot();
	}

	public void onQuickCraft(ItemStack stack, ItemStack p_40236_) {

	}

	protected void onQuickCraft(ItemStack stack, int p_40233_) {
	}

	protected void onSwapCraft(int p_40237_) {
	}

	protected void checkTakeAchievements(ItemStack stack) {
	}

	public void onTake(Player p_150645_, ItemStack stack) {
		this.setChanged();
	}

	public boolean mayPlace(ItemStack stack) {
		return true;
	}

	public ItemStack getItem() {
		return compoundItem.getPartInSlot(partSlotIndex);
	}

	public int getPartSlotIndex() {
		return partSlotIndex;
	}

	public CompoundItemSlot getItemSlot() {
		return itemSlot;
	}

	public boolean hasItem() {
		return !this.getItem().isEmpty();
	}

	public void set(ItemStack stack) {
		if (!compoundItem.isSlotPopulated(partSlotIndex) && compoundItem.canApplyPartToSlot(partSlotIndex, stack)) {
			compoundItem.tryApplyPartToSlot(partSlotIndex, stack);
			this.setChanged();
		}
	}

	public void initialize(ItemStack stack) {
		compoundItem.tryApplyPartToSlot(this.partSlotIndex, stack);
		this.setChanged();
	}

	public void setChanged() {

	}

	public int getMaxStackSize() {
		return 1;
	}

	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}

	public ItemStack remove(int amount) {
		return compoundItem.tryRemovePartFromSlot(partSlotIndex);
	}

	public boolean mayPickup(Player player) {
		return true;
	}

	public boolean isActive() {
		return true;
	}

	/**
	 * Checks if the other slot is in the same inventory, by comparing the inventory
	 * reference.
	 * 
	 * @param other
	 * @return true if the other slot is in the same inventory
	 */
	public boolean isSameInventory(Slot other) {
		if (other instanceof CompoundItemPartSlot) {
			return this.compoundItem == ((CompoundItemPartSlot) other).compoundItem;
		}
		return false;
	}

	public Optional<ItemStack> tryRemove(int amount, int maxRemove, Player player) {
		if (!mayPickup(player)) {
			return Optional.empty();
		} else if (!allowModification(player) && maxRemove < getItem().getCount()) {
			return Optional.empty();
		} else {
			ItemStack removed = compoundItem.tryRemovePartFromSlot(partSlotIndex);
			return Optional.of(removed);
		}
	}

	public ItemStack safeTake(int p_150648_, int p_150649_, Player player) {
		Optional<ItemStack> optional = tryRemove(p_150648_, p_150649_, player);
		optional.ifPresent((p_150655_) -> {
			onTake(player, p_150655_);
		});
		return optional.orElse(ItemStack.EMPTY);
	}

	public ItemStack safeInsert(ItemStack stack) {
		return safeInsert(stack, stack.getCount());
	}

	public ItemStack safeInsert(ItemStack stack, int amount) {
		if (!stack.isEmpty() && mayPlace(stack)) {
			boolean applied = compoundItem.tryApplyPartToSlot(partSlotIndex, stack.copy());
			if (applied) {
				stack.shrink(1);
			}
			return stack;
		} else {
			return stack;
		}
	}

	public boolean allowModification(Player player) {
		return this.mayPickup(player) && this.mayPlace(this.getItem());
	}
}
