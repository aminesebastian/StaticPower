package theking530.api;

import theking530.api.upgrades.UpgradeType;
import theking530.staticpower.data.StaticPowerTier;

public interface IUpgradeItem {

	public boolean isTiered();

	public StaticPowerTier getTier();

	public boolean isOfType(UpgradeType type);
}
