package theking530.api;

import net.minecraft.resources.ResourceLocation;
import theking530.api.upgrades.UpgradeType;

public interface IUpgradeItem {

	public boolean isTiered();

	public ResourceLocation getTier();

	public boolean isOfType(UpgradeType type);
}
