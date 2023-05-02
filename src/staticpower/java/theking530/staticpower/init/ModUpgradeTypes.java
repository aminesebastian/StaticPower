package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticpower.StaticPower;

public class ModUpgradeTypes {
	private static final DeferredRegister<UpgradeType<?>> UPGRADE_TYPE_REGISTRY = DeferredRegister
			.create(StaticCoreRegistries.UPGRADE_TYPES_REGISTRY_KEY, StaticPower.MOD_ID);

	public static final RegistryObject<UpgradeType<Boolean>> EXPERIENCE_VACUUM = UPGRADE_TYPE_REGISTRY
			.register("experience_vacuum", () -> new UpgradeType<Boolean>());
	public static final RegistryObject<UpgradeType<Boolean>> TELEPORT = UPGRADE_TYPE_REGISTRY.register("teleport",
			() -> new UpgradeType<Boolean>());
	public static final RegistryObject<UpgradeType<CentrifugeUpgradeValue>> CENTRIFUGE = UPGRADE_TYPE_REGISTRY
			.register("centrifuge", () -> new UpgradeType<CentrifugeUpgradeValue>());

	public static final RegistryObject<UpgradeType<Boolean>> DIGISTORE_CRAFTING = UPGRADE_TYPE_REGISTRY
			.register("digistore_crafting", () -> new UpgradeType<Boolean>());
	public static final RegistryObject<UpgradeType<Boolean>> DIGISTORE_STACK = UPGRADE_TYPE_REGISTRY
			.register("digistore_stack", () -> new UpgradeType<Boolean>());
	public static final RegistryObject<UpgradeType<Double>> DIGISTORE_ACCELERATION = UPGRADE_TYPE_REGISTRY
			.register("digistore_acceleration", () -> new UpgradeType<Double>());

	public static void init(IEventBus eventBus) {
		UPGRADE_TYPE_REGISTRY.register(eventBus);
	}

	public record CentrifugeUpgradeValue(int maxRPM, double powerUsageIncrease) {
	}
}
