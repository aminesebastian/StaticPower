package theking530.staticpower.items.upgrades;

import theking530.staticpower.data.StaticPowerTier;

public interface IUpgradeItem {
	public enum UpgradeType {
		SPEED, POWER, TANK, RANGE, OUTPUT_MULTIPLIER, SPECIAL
	};

	public StaticPowerTier getTier();

	public boolean isOfType(UpgradeType type);
}
