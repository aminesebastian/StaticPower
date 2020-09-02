package theking530.api;

import theking530.staticpower.data.StaticPowerTier;

public interface IUpgradeItem {
	public enum UpgradeType {
		SPEED, POWER, TANK, RANGE, OUTPUT_MULTIPLIER, CENTRIFUGE, SPECIAL, DIGISTORE_ATTACHMENT
	};

	public boolean isTiered();

	public StaticPowerTier getTier();

	public boolean isOfType(UpgradeType type);
}
