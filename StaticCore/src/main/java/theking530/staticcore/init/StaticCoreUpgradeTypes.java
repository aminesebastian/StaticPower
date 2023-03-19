package theking530.staticcore.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;

public class StaticCoreUpgradeTypes {
	private static final DeferredRegister<UpgradeType<?>> UPGRADE_TYPE_REGISTRY = DeferredRegister.create(StaticCoreRegistries.UPGRADE_TYPES, StaticCore.MOD_ID);

	public static final RegistryObject<UpgradeType<Float>> SPEED = UPGRADE_TYPE_REGISTRY.register("speed", () -> new UpgradeType<Float>());
	public static final RegistryObject<UpgradeType<Float>> POWER_USAGE = UPGRADE_TYPE_REGISTRY.register("power_usage", () -> new UpgradeType<Float>());

	public static final RegistryObject<UpgradeType<Float>> POWER_TRANSFER = UPGRADE_TYPE_REGISTRY.register("power_transfer", () -> new UpgradeType<Float>());
	public static final RegistryObject<UpgradeType<Float>> POWER_CAPACITY = UPGRADE_TYPE_REGISTRY.register("power_capacity", () -> new UpgradeType<Float>());
	public static final RegistryObject<UpgradeType<StaticPowerVoltage>> POWER_TRANSFORMER = UPGRADE_TYPE_REGISTRY.register("power_transformer", () -> new UpgradeType<StaticPowerVoltage>());

	public static final RegistryObject<UpgradeType<Float>> TANK_CAPACITY = UPGRADE_TYPE_REGISTRY.register("tank_capacity", () -> new UpgradeType<Float>());

	public static final RegistryObject<UpgradeType<Float>> RANGE = UPGRADE_TYPE_REGISTRY.register("range", () -> new UpgradeType<Float>());
	public static final RegistryObject<UpgradeType<Float>> OUTPUT_MULTIPLIER = UPGRADE_TYPE_REGISTRY.register("output_multiplier", () -> new UpgradeType<Float>());

	public static final RegistryObject<UpgradeType<Float>> HEAT_CAPACITY = UPGRADE_TYPE_REGISTRY.register("heat_capacity", () -> new UpgradeType<Float>());
	public static final RegistryObject<UpgradeType<Float>> HEAT_TRANSFER = UPGRADE_TYPE_REGISTRY.register("heat_transfer", () -> new UpgradeType<Float>());

	public static final RegistryObject<UpgradeType<Boolean>> VOID = UPGRADE_TYPE_REGISTRY.register("void", () -> new UpgradeType<Boolean>());

	public static void init(IEventBus eventBus) {
		UPGRADE_TYPE_REGISTRY.register(eventBus);
	}
}
