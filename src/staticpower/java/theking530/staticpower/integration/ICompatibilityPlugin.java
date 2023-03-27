package theking530.staticpower.integration;

import net.minecraft.resources.ResourceLocation;

public interface ICompatibilityPlugin {

	public ResourceLocation getPluginName();

	public void register();

	default public void preInit() {
	}

	default public void postInit() {
	}

	default public boolean shouldRegister() {
		return true;
	}
}
