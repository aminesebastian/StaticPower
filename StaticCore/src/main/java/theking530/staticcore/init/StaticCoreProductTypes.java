package theking530.staticcore.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.fluid.FluidStackProductType;
import theking530.staticcore.productivity.product.item.ItemStackProductType;
import theking530.staticcore.productivity.product.power.PowerProductType;
import theking530.staticcore.productivity.product.power.PowerProductionStack;

public class StaticCoreProductTypes {
	private static final DeferredRegister<ProductType<?>> PRODUCT_REGISTRY = DeferredRegister.create(StaticCoreRegistries.PRODUCT_TYPE, StaticCore.MOD_ID);

	public static final RegistryObject<ProductType<ItemStack>> Item = PRODUCT_REGISTRY.register("item", () -> new ItemStackProductType());
	public static final RegistryObject<ProductType<FluidStack>> Fluid = PRODUCT_REGISTRY.register("fluid", () -> new FluidStackProductType());
	public static final RegistryObject<ProductType<PowerProductionStack>> Power = PRODUCT_REGISTRY.register("power", () -> new PowerProductType());

	public static void init(IEventBus eventBus) {
		PRODUCT_REGISTRY.register(eventBus);
	}
}
