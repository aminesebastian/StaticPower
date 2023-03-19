package theking530.staticcore.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class StaticCoreTier {
	/****************
	 * Tier Settings
	 ****************/
	public final ConfigValue<String> tierId;
	public final ConfigValue<String> unlocalizedTierName;

	private final StaticCoreTierUpgradeConfiguration upgradeConfiguration;

	public StaticCoreTier(ForgeConfigSpec.Builder builder, String modId) {
		// Establish field for the tier Id.
		tierId = builder.comment("The unique id of the tier in the format of 'MOD_ID:TIER_NAME'.").translation(modId + ".config." + "tierId").define("TierId", getTierId().toString());

		// Establish a field for the unlocalized name.
		unlocalizedTierName = builder.comment("The unlocalized name of the tier.").translation(modId + ".config." + "unlocalizedTierName").define("UnlocalizedTierName", getUnlocalizedName());

		builder.push("Upgrades");
		upgradeConfiguration = createUpgradeConfiguration(builder, modId);
		builder.pop();
	}

	@SuppressWarnings("unchecked")
	public <T extends StaticCoreTierUpgradeConfiguration> T getUpgradeConfiguration() {
		return (T) upgradeConfiguration;
	}

	public boolean hasUpgradeConfiguration() {
		return upgradeConfiguration != null;
	}

	protected abstract ResourceLocation getTierId();

	protected abstract String getUnlocalizedName();

	protected StaticCoreTierUpgradeConfiguration createUpgradeConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}
}
