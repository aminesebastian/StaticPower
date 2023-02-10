package theking530.staticpower.cables;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.StaticPowerTiers;

public class CableTiers {

	public record CableTier(ResourceLocation location, String name) {
	}

	private static final CableTier[] CABLE_TIERS = new CableTier[] { new CableTier(StaticPowerTiers.BASIC, "basic"), new CableTier(StaticPowerTiers.ADVANCED, "advanced"),
			new CableTier(StaticPowerTiers.STATIC, "static"), new CableTier(StaticPowerTiers.ENERGIZED, "energized"), new CableTier(StaticPowerTiers.LUMUM, "lumum"),
			new CableTier(StaticPowerTiers.CREATIVE, "creative") };

	public static CableTier[] get() {
		return CABLE_TIERS;
	}
}
