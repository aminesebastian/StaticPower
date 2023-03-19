package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticpower.StaticPower;

public class ModUpgradeTypes {
	private static final DeferredRegister<UpgradeType<?>> UPGRADE_TYPE_REGISTRY = DeferredRegister.create(StaticCoreRegistries.UPGRADE_TYPES, StaticPower.MOD_ID);

	public static final RegistryObject<UpgradeType> CENTRIFUGE = UPGRADE_TYPE_REGISTRY.register("centrifuge", () -> new UpgradeType());
	public static final RegistryObject<UpgradeType> DIGISTORE_CRAFTING = UPGRADE_TYPE_REGISTRY.register("digistore_crafting", () -> new UpgradeType());
	public static final RegistryObject<UpgradeType> DIGISTORE_STACK = UPGRADE_TYPE_REGISTRY.register("digistore_stack", () -> new UpgradeType());
	public static final RegistryObject<UpgradeType> DIGISTORE_ACCELERATION = UPGRADE_TYPE_REGISTRY.register("digistore_acceleration", () -> new UpgradeType());

	public static void init(IEventBus eventBus) {
		UPGRADE_TYPE_REGISTRY.register(eventBus);
	}
}
