package theking530.api.upgrades;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface IUpgradeSupplier<T> {
	public T apply(UpgradeType<T> type, ItemStack upgradeItem);
}
