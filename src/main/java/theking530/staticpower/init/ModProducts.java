package theking530.staticpower.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.fluid.FluidStackProductType;
import theking530.staticcore.productivity.product.item.ItemStackProductType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;

public class ModProducts {
	private static final DeferredRegister<ProductType<?>> PRODUCT_REGISTRY = DeferredRegister.create(StaticPowerRegistries.PRODUCT_REGISTRY, StaticPower.MOD_ID);

	public static final RegistryObject<ProductType<ItemStack>> Item = PRODUCT_REGISTRY.register("item", () -> new ItemStackProductType());
	public static final RegistryObject<ProductType<FluidStack>> Fluid = PRODUCT_REGISTRY.register("fluid", () -> new FluidStackProductType());

	public static void init(IEventBus eventBus) {
		PRODUCT_REGISTRY.register(eventBus);
	}
}
