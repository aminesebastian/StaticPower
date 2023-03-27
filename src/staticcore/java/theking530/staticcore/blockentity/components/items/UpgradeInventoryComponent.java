package theking530.staticcore.blockentity.components.items;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.api.IUpgradeItem;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.utilities.item.ItemUtilities;

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

	public <T> UpgradeItemWrapper<T> getMaxTierItemForUpgradeType(UpgradeType<T> type) {
		// Allocate the max tier upgrade stack and tier.
		ResourceLocation maxTier = null;
		ItemStack maxTierUpgradeStack = ItemStack.EMPTY;
		int count = 0;

		// Check for all items in the stacks array.
		for (ItemStack upgradeStack : stacks) {
			// Skip empty stacks.
			if (upgradeStack.isEmpty()) {
				continue;
			}
			// Get the upgrade item.
			IUpgradeItem upgradeItem = upgradeStack.getItem() instanceof IUpgradeItem ? (IUpgradeItem) upgradeStack.getItem() : null;

			// If it is a valid upgrade item, and it is of the requested type, check to see
			// if the tier is higher than the current tier.
			if (upgradeItem != null && upgradeItem.isOfType(upgradeStack, type)) {
				if (maxTier == null || StaticCoreConfig.getTier(upgradeItem.getTier()).upgradeConfiguration.upgradeOrdinal
						.get() > StaticCoreConfig.getTier(maxTier).upgradeConfiguration.upgradeOrdinal.get()) {
					maxTier = upgradeItem.getTier();
					maxTierUpgradeStack = upgradeStack;
					count = upgradeStack.getCount();
				} else if (ItemUtilities.areItemStacksStackable(maxTierUpgradeStack, upgradeStack)) {
					count += upgradeStack.getCount();
				}
			}
		}

		// Return the max tiered item.
		ItemStack maxStack = maxTierUpgradeStack.copy();
		maxStack.setCount(Math.min(maxStack.getMaxStackSize(), count));
		return new UpgradeItemWrapper<T>(type, maxStack);
	}

	public <T> List<UpgradeItemWrapper<T>> getAllUpgradesOfType(UpgradeType<T> type) {
		List<UpgradeItemWrapper<T>> output = new LinkedList<UpgradeItemWrapper<T>>();

		// Check for all items in the stacks array.
		for (ItemStack upgradeStack : stacks) {
			// Skip empty stacks.
			if (upgradeStack.isEmpty()) {
				continue;
			}
			// Get the upgrade item.
			IUpgradeItem upgradeItem = upgradeStack.getItem() instanceof IUpgradeItem ? (IUpgradeItem) upgradeStack.getItem() : null;

			// If it is a valid upgrade item, and it is of the requested type, add it.
			if (upgradeItem != null && upgradeItem.isOfType(upgradeStack, type)) {
				boolean foundExisting = false;
				for (UpgradeItemWrapper<T> wrapper : output) {
					if (ItemUtilities.areItemStacksStackable(wrapper.getStack(), upgradeStack)) {
						wrapper.stack.grow(upgradeStack.getCount());
						foundExisting = true;
					}
				}

				if (!foundExisting) {
					output.add(new UpgradeItemWrapper<T>(type, upgradeStack.copy()));
				}
			}
		}
		return output;
	}

	/**
	 * Determines if this upgrade inventory has an upgrade of the provided class.
	 * 
	 * @param upgradeClass The class to check for.
	 * @return True if an upgrade of the provided type was found, false otherwise.
	 */
	public boolean hasUpgradeOfType(UpgradeType<?> type) {
		for (ItemStack stack : stacks) {
			if (stack.getItem() instanceof IUpgradeItem) {
				IUpgradeItem upgradeItem = (IUpgradeItem) stack.getItem();
				return upgradeItem.isOfType(stack, type);
			}
		}
		return false;
	}

	public class UpgradeItemWrapper<T> {
		private final ItemStack stack;
		private final UpgradeType<T> type;
		private final boolean isEmpty;
		private final IUpgradeItem upgradeItem;

		public UpgradeItemWrapper(UpgradeType<T> type, ItemStack stack) {
			this.type = type;
			this.stack = stack;
			this.upgradeItem = (IUpgradeItem) stack.getItem();
			this.isEmpty = stack.isEmpty();
		}

		public IUpgradeItem getUpgradeItem() {
			return upgradeItem;
		}

		public <K> boolean upgradeIsAlsoOfType(UpgradeType<K> type) {
			return getUpgradeItem().isOfType(stack, type);
		}

		public T getUpgradeValue() {
			return upgradeItem.getUpgradeValue(stack, type);
		}

		public boolean isEmpty() {
			return isEmpty;
		}

		public ItemStack getStack() {
			return stack;
		}

		public float getUpgradeWeight() {
			return (float) stack.getCount() / (float) stack.getMaxStackSize();
		}

	}
}
