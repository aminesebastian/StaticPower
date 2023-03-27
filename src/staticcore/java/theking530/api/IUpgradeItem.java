package theking530.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.api.upgrades.UpgradeType;

public interface IUpgradeItem {

	public boolean isTiered();

	public ResourceLocation getTier();

	public boolean isOfType(ItemStack upgradeStack, UpgradeType<?> type);

	public <T> T getUpgradeValue(ItemStack upgradeStack, UpgradeType<T> type);
}
