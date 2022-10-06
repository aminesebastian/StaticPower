package theking530.staticpower.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.productivity.entry.ItemProductionEntry;
import theking530.staticcore.productivity.product.ItemStackProductType;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;

public class ModProducts {
	private static final DeferredRegister<ProductType<?, ?>> PRODUCT_REGISTRY = DeferredRegister.create(StaticPowerRegistries.PRODUCT_REGISTRY, StaticPower.MOD_ID);

	public static final RegistryObject<ProductType<ItemStack, ItemProductionEntry>> Item = PRODUCT_REGISTRY.register("digistore", () -> new ItemStackProductType());

	public static void init(IEventBus eventBus) {
		PRODUCT_REGISTRY.register(eventBus);
	}
}
