package theking530.staticpower.cables.network.modules;

import java.util.HashSet;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.network.CableNetwork;

public class CableNetworkModuleTypes {
	public static final ResourceLocation POWER_WIRE_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_power_wire_network");
	public static final ResourceLocation POWER_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_power_network");
	
	public static final ResourceLocation ITEM_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_item_network");
	public static final ResourceLocation FLUID_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_fluid_network");
	public static final ResourceLocation DIGISTORE_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_digistore_network");
	public static final ResourceLocation HEAT_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_heat_network");
	public static final ResourceLocation SCAFFOLD_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_scaffold_network");

	public static final ResourceLocation REFINERY_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_refiney_network");

	public static final ResourceLocation BUNDLED_REDSTONE_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_bundled_redstone_network");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network");

	public static final ResourceLocation REDSTONE_NETWORK_MODULE_DARK_RED = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_dark_red");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_RED = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_red");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_GOLD = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_gold");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_YELLOW = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_yellow");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_DARK_GREEN = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_dark_green");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_GREEN = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_green");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_AQUA = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_aqua");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_DARK_AQUA = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_dark_aqua");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_DARK_BLUE = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_dark_blue");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_BLUE = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_blue");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_LIGHT_PURPLE = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_light_purple");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_DARK_PURPLE = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_dark_purple");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_WHITE = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_white");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_GRAY = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_gray");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_DARK_GRAY = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_dark_gray");
	public static final ResourceLocation REDSTONE_NETWORK_MODULE_BLACK = new ResourceLocation(StaticPower.MOD_ID, "module_redstone_network_black");

	public static final HashSet<ResourceLocation> REDSTONE_MODULES = new HashSet<ResourceLocation>();
	static {
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_DARK_RED);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_RED);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_GOLD);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_YELLOW);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_DARK_GREEN);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_GREEN);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_AQUA);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_DARK_AQUA);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_DARK_BLUE);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_BLUE);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_LIGHT_PURPLE);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_DARK_PURPLE);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_WHITE);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_GRAY);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_DARK_GRAY);
		REDSTONE_MODULES.add(REDSTONE_NETWORK_MODULE_BLACK);
	}

	public static boolean doesNetworkSupportRedstoneAnyRedstoneModule(CableNetwork network) {
		for (ResourceLocation type : REDSTONE_MODULES) {
			if (network != null && network.hasModule(type)) {
				return true;
			}
		}
		return false;
	}
}
