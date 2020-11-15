package theking530.staticpower.tileentities.components.items;

import net.minecraft.item.ItemStack;
import theking530.api.IUpgradeItem;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.utilities.ItemUtilities;

public class UpgradeInventoryComponent extends InventoryComponent {
	public UpgradeInventoryComponent(String name, int size) {
		super(name, size, MachineSideMode.Never);
		setShiftClickEnabled(true);
		setShiftClickPriority(-10);
		setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof IUpgradeItem;
			}
		});
	}

	public UpgradeItemWrapper getMaxTierItemForUpgradeType(UpgradeType type) {
		// Allocate the max tier upgrade stack and tier.
		StaticPowerTier maxTier = null;
		ItemStack maxTierUpgradeStack = ItemStack.EMPTY;
		int count = 0;

		// Check for all items in the stacks array.
		for (ItemStack upgradeStack : stacks) {
			// Skip empty stacks.
			if (upgradeStack.isEmpty()) {
				continue;
			}
			// Get the upgrade item.
			BaseUpgrade upgradeItem = upgradeStack.getItem() instanceof BaseUpgrade ? (BaseUpgrade) upgradeStack.getItem() : null;

			// If it is a valid upgrade item, and it is of the requested type, check to see
			// if the tier is higher than the current tier.
			if (upgradeItem != null && upgradeItem.isOfType(type)) {
				if (maxTier == null || upgradeItem.getTier().upgradeOrdinal.get() > maxTier.upgradeOrdinal.get()) {
					maxTier = upgradeItem.getTier();
					maxTierUpgradeStack = upgradeStack;
					count += upgradeStack.getCount();
				} else if (ItemUtilities.areItemStacksStackable(maxTierUpgradeStack, upgradeStack)) {
					count += upgradeStack.getCount();
				}
			}
		}

		// Return the max tiered item.
		ItemStack maxStack = maxTierUpgradeStack.copy();
		maxStack.setCount(Math.min(maxStack.getMaxStackSize(), count));
		return new UpgradeItemWrapper(maxStack, maxTier);
	}

	/**
	 * Determines if this upgrade inventory has an upgrade of the provided class.
	 * 
	 * @param upgradeClass The class to check for.
	 * @return True if an upgrade of the provided type was found, false otherwise.
	 */
	public <T extends IUpgradeItem> boolean hasUpgradeOfClass(Class<T> upgradeClass) {
		for (ItemStack stack : stacks) {
			if (upgradeClass.isInstance(stack.getItem())) {
				return true;
			}
		}
		return false;
	}

	public class UpgradeItemWrapper {
		private final ItemStack stack;
		private final StaticPowerTier tier;
		private final boolean isEmpty;

		public UpgradeItemWrapper(ItemStack stack, StaticPowerTier tier) {
			super();
			this.stack = stack;
			this.tier = tier;
			this.isEmpty = stack.isEmpty() || tier == null;
		}

		public boolean isEmpty() {
			return isEmpty;
		}

		public ItemStack getStack() {
			return stack;
		}

		public StaticPowerTier getTier() {
			return tier;
		}

		public float getUpgradeWeight() {
			return (float) stack.getCount() / (float) stack.getMaxStackSize();
		}

	}
}
