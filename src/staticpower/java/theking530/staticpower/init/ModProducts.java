package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPower;

public class ModProducts {
	private static final DeferredRegister<ProductType<?>> PRODUCT_REGISTRY = DeferredRegister
			.create(StaticCoreRegistries.PRODUCT_TYPE_REGISTRY_KEY, StaticPower.MOD_ID);

	public static void init(IEventBus eventBus) {
		PRODUCT_REGISTRY.register(eventBus);
	}
}
