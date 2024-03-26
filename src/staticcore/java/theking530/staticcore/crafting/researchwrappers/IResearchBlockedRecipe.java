package theking530.staticcore.crafting.researchwrappers;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public interface IResearchBlockedRecipe {
	Set<ResourceLocation> getRequiredResearch();
}
