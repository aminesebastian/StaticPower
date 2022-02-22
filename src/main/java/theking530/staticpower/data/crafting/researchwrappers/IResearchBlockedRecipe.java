package theking530.staticpower.data.crafting.researchwrappers;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public interface IResearchBlockedRecipe {
	Set<ResourceLocation> getRequiredResearch();
}
