package theking530.staticpower.init.cables;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.digistore.DigistoreNetworkModuleFactory;
import theking530.staticpower.cables.fluid.FluidNetworkModuleFactory;
import theking530.staticpower.cables.heat.HeatNetworkModuleFactory;
import theking530.staticpower.cables.item.ItemNetworkModuleFactory;
import theking530.staticpower.cables.power.PowerNetworkModuleFactory;
import theking530.staticpower.cables.redstone.basic.RedstoneNetworkModuleFactory;
import theking530.staticpower.cables.redstone.bundled.BundledRedstoneNetworkModuleFactory;
import theking530.staticpower.cables.scaffold.ScaffoldNetworkModuleFactory;

public class ModCableModules {
	private static final DeferredRegister<CableNetworkModuleType> MODULES = DeferredRegister.create(StaticCoreRegistries.CABLE_MODULE_TYPE, StaticPower.MOD_ID);

	public static final RegistryObject<CableNetworkModuleType> Power = MODULES.register("power", () -> new PowerNetworkModuleFactory());
	public static final RegistryObject<CableNetworkModuleType> Item = MODULES.register("item", () -> new ItemNetworkModuleFactory());
	public static final RegistryObject<CableNetworkModuleType> Fluid = MODULES.register("fluid", () -> new FluidNetworkModuleFactory());
	public static final RegistryObject<CableNetworkModuleType> Digistore = MODULES.register("digistore", () -> new DigistoreNetworkModuleFactory());
	public static final RegistryObject<CableNetworkModuleType> Heat = MODULES.register("heat", () -> new HeatNetworkModuleFactory());
	public static final RegistryObject<CableNetworkModuleType> Scaffold = MODULES.register("scaffold", () -> new ScaffoldNetworkModuleFactory());

	public static final RegistryObject<CableNetworkModuleType> BundledRedstone = MODULES.register("bundled_redstone", () -> new BundledRedstoneNetworkModuleFactory());
	public static final RegistryObject<CableNetworkModuleType> NakedRedstone = MODULES.register("naked_redstone", () -> new RedstoneNetworkModuleFactory());

	public static final Map<MinecraftColor, RegistryObject<CableNetworkModuleType>> RedstoneModules = new HashMap<>();
	static {
		for (MinecraftColor color : MinecraftColor.values()) {
			RedstoneModules.put(color, MODULES.register(color.getName() + "_redstone", () -> new RedstoneNetworkModuleFactory()));
		}
	}

	public static void init(IEventBus eventBus) {
		MODULES.register(eventBus);
	}

	public static boolean doesNetworkSupportRedstoneAnyRedstoneModule(CableNetwork network) {
		if (network == null) {
			return false;
		}

		for (CableNetworkModuleType type : StaticCoreRegistries.CABLE_MODULE_TYPE.getValues()) {
			if (type instanceof RedstoneNetworkModuleFactory) {
				if (network.hasModule(type)) {
					return true;
				}
			}
		}
		return false;
	}
}
