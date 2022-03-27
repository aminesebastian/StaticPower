package theking530.staticpower.init;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;

public class ModResearch {
	public static final ResourceLocation BASIC_RESEARCH = makeResearchName("research_tiers/basic_research");
	public static final ResourceLocation RUBBER_WOOD_STRIPPING = makeResearchName("rubber/rubber_wood_stripping");

	public static ResourceLocation makeResearchName(String name) {
		return new ResourceLocation(StaticPower.MOD_ID, "research/" + name);
	}
}
