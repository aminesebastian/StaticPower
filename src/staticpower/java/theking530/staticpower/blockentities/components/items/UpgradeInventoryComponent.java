package theking530.staticpower.blockentities.components.items;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.api.IUpgradeItem;
import theking530.api.upgrades.UpgradeType;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.items.upgrades.BaseUpgrade;
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
			BaseUpgrade upgradeItem = upgradeStack.getItem() instanceof BaseUpgrade ? (BaseUpgrade) upgradeStack.getItem() : null;

			// If it is a valid upgrade item, and it is of the requested type, check to see
			// if the tier is higher than the current tier.
			if (upgradeItem != null && upgradeItem.isOfType(type)) {
				if (maxTier == null || StaticPowerConfig.getTier(upgradeItem.getTier()).upgradeConfiguration.upgradeOrdinal
						.get() > StaticPowerConfig.getTier(maxTier).upgradeConfiguration.upgradeOrdinal.get()) {
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
		return new UpgradeItemWrapper(maxStack, maxTier);
	}

	public List<UpgradeItemWrapper> getAllUpgradesOfType(UpgradeType type) {
		List<UpgradeItemWrapper> output = new LinkedList<UpgradeItemWrapper>();

		// Check for all items in the stacks array.
		for (ItemStack upgradeStack : stacks) {
			// Skip empty stacks.
			if (upgradeStack.isEmpty()) {
				continue;
			}
			// Get the upgrade item.
			BaseUpgrade upgradeItem = upgradeStack.getItem() instanceof BaseUpgrade ? (BaseUpgrade) upgradeStack.getItem() : null;

			// If it is a valid upgrade item, and it is of the requested type, add it.
			if (upgradeItem != null && upgradeItem.isOfType(type)) {
				boolean foundExisting = false;
				for (UpgradeItemWrapper wrapper : output) {
					if (ItemUtilities.areItemStacksStackable(wrapper.getStack(), upgradeStack)) {
						wrapper.stack.grow(upgradeStack.getCount());
						foundExisting = true;
					}
				}

				if (!foundExisting) {
					output.add(new UpgradeItemWrapper(upgradeStack.copy(), upgradeItem.getTier()));
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
		private final ResourceLocation tier;
		private final boolean isEmpty;

		public UpgradeItemWrapper(ItemStack stack, ResourceLocation tier) {
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

		public ResourceLocation getTierId() {
			return tier;
		}

		public StaticPowerTier getTier() {
			return StaticPowerConfig.getTier(tier);
		}

		public float getUpgradeWeight() {
			return (float) stack.getCount() / (float) stack.getMaxStackSize();
		}

	}
}
