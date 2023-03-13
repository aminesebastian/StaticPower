package theking530.staticpower.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import theking530.staticpower.integration.TOP.PluginTOP;

public class ModIntegrations {
	private static final Map<ResourceLocation, ICompatibilityPlugin> PLUGINS = new HashMap<>();

	public static final PluginTOP TOP_PLUGIN = register(new PluginTOP());

	public static final <T extends ICompatibilityPlugin> T register(T plugin) {
		if (plugin.shouldRegister()) {
			PLUGINS.put(plugin.getPluginName(), plugin);
		}
		return plugin;
	}

	public static void init(IEventBus eventBus) {
		for (Entry<ResourceLocation, ICompatibilityPlugin> entry : PLUGINS.entrySet()) {
			entry.getValue().register();
		}
	}

	public static void preInit(IEventBus eventBus) {
		for (Entry<ResourceLocation, ICompatibilityPlugin> entry : PLUGINS.entrySet()) {
			entry.getValue().preInit();
		}
	}

	public static void postInit(IEventBus eventBus) {
		for (Entry<ResourceLocation, ICompatibilityPlugin> entry : PLUGINS.entrySet()) {
			entry.getValue().postInit();
		}
	}
}
