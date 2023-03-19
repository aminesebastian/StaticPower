package theking530.staticcore.data;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class StaticCoreTierUpgradeConfiguration {
	public final ConfigValue<Integer> upgradeOrdinal;

	public StaticCoreTierUpgradeConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		upgradeOrdinal = builder.comment(
				"The upgrade ordinal of this tier. Higher value will take precedence. For example, if a machine has both Basic and Energized power upgrades installed, the Energized upgrades will be used when calculating power values because it has the higher upgrade ordinal. In the case of a tie, it comes down to which one appears in later in the upgrade slots.")
				.translation(modId + ".config." + "upgradeOrdinal").define("UpgradeOrdinal", this.getUpgradeOrdinal());
	}

	protected abstract int getUpgradeOrdinal();
}
