package theking530.staticpower.tileentities.components;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public class UpgradeProcessingComponent extends AbstractTileEntityComponent {
	private BiConsumer<UpgradeProcessingComponent, ItemStack> upgradeCallback;
	private Predicate<ItemStack> upgradePredicate;
	private InventoryComponent upgradeInventory;

	public UpgradeProcessingComponent(String name, @Nonnull InventoryComponent upgradeInventory, @Nonnull BiConsumer<UpgradeProcessingComponent, ItemStack> upgradeCallback,
			Predicate<ItemStack> upgradePredicate) {
		super(name);
		this.upgradeCallback = upgradeCallback;
		this.upgradePredicate = upgradePredicate;
		this.upgradeInventory = upgradeInventory;
	}

	/**
	 * Raise the callback for any upgrades.
	 */
	@Override
	public void preProcessUpdate() {
		for (int i = 0; i < upgradeInventory.getSlots(); i++) {
			ItemStack stack = upgradeInventory.getStackInSlot(i);
			upgradeCallback.accept(this, stack);
		}
	}

	/**
	 * Checks to see if the provided upgrade is valid.
	 * 
	 * @param upgrade The upgrade {@link ItemStack}.
	 * @return True if the upgrade is valid, false otherwise.
	 */
	public boolean isValidUpgrade(ItemStack upgrade) {
		return upgradePredicate.test(upgrade);
	}
}
