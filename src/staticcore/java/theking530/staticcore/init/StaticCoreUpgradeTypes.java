package theking530.staticcore.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;

public class StaticCoreUpgradeTypes {
	private static final DeferredRegister<UpgradeType<?>> UPGRADE_TYPE_REGISTRY = DeferredRegister
			.create(StaticCoreRegistries.UPGRADE_TYPES_REGISTRY_KEY, StaticCore.MOD_ID);

	public static final RegistryObject<UpgradeType<SpeedMultiplierUpgradeValue>> SPEED = UPGRADE_TYPE_REGISTRY.register(
			"speed", () -> new UpgradeType<SpeedMultiplierUpgradeValue>(new SpeedMultiplierUpgradeValue(1.0, 1.0)));

	public static final RegistryObject<UpgradeType<Double>> POWER_USAGE = UPGRADE_TYPE_REGISTRY.register("power_usage",
			() -> new UpgradeType<Double>(1.0));
	public static final RegistryObject<UpgradeType<Double>> POWER_TRANSFER = UPGRADE_TYPE_REGISTRY
			.register("power_transfer", () -> new UpgradeType<Double>(1.0));
	public static final RegistryObject<UpgradeType<Double>> POWER_CAPACITY = UPGRADE_TYPE_REGISTRY
			.register("power_capacity", () -> new UpgradeType<Double>(1.0));
	public static final RegistryObject<UpgradeType<StaticPowerVoltage>> POWER_TRANSFORMER = UPGRADE_TYPE_REGISTRY
			.register("power_transformer", () -> new UpgradeType<StaticPowerVoltage>(StaticPowerVoltage.ZERO));
	public static final RegistryObject<UpgradeType<CombinedPowerUpgradeValue>> POWER_COMBINED_UPGRADE = UPGRADE_TYPE_REGISTRY
			.register("power_combined_upgrade",
					() -> new UpgradeType<CombinedPowerUpgradeValue>(new CombinedPowerUpgradeValue(1.0, 1.0)));

	public static final RegistryObject<UpgradeType<Double>> TANK_CAPACITY = UPGRADE_TYPE_REGISTRY
			.register("tank_capacity", () -> new UpgradeType<Double>(1.0));

	public static final RegistryObject<UpgradeType<Double>> RANGE = UPGRADE_TYPE_REGISTRY.register("range",
			() -> new UpgradeType<Double>(1.0));
	public static final RegistryObject<UpgradeType<OutputMultiplierUpgradeValue>> OUTPUT_MULTIPLIER = UPGRADE_TYPE_REGISTRY
			.register("output_multiplier",
					() -> new UpgradeType<OutputMultiplierUpgradeValue>(new OutputMultiplierUpgradeValue(1.0, 1.0)));

	public static final RegistryObject<UpgradeType<Double>> HEAT_CAPACITY = UPGRADE_TYPE_REGISTRY
			.register("heat_capacity", () -> new UpgradeType<Double>(1.0));
	public static final RegistryObject<UpgradeType<Double>> HEAT_TRANSFER = UPGRADE_TYPE_REGISTRY
			.register("heat_transfer", () -> new UpgradeType<Double>(1.0));
	public static final RegistryObject<UpgradeType<CombinedHeatUpgradeValue>> HEAT_COMBINED = UPGRADE_TYPE_REGISTRY
			.register("heat_combined",
					() -> new UpgradeType<CombinedHeatUpgradeValue>(new CombinedHeatUpgradeValue(1.0, 1.0)));

	public static final RegistryObject<UpgradeType<Boolean>> VOID = UPGRADE_TYPE_REGISTRY.register("void",
			() -> new UpgradeType<Boolean>(false));

	public static void init(IEventBus eventBus) {
		UPGRADE_TYPE_REGISTRY.register(eventBus);
	}

	public record OutputMultiplierUpgradeValue(double outputMultiplierIncrease, double powerUsageIncrease) {
	}

	public record SpeedMultiplierUpgradeValue(double speedIncrease, double powerUsageIncrease) {
	}

	public record CombinedPowerUpgradeValue(double powerTransfer, double powerCapacity) {
	}

	public record CombinedHeatUpgradeValue(double capacity, double conductivity) {
	}
}
