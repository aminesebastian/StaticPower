package theking530.staticcore.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCore;
import theking530.staticcore.crafting.StaticPowerRecipeType;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.research.Research;

public class StaticCoreRecipeTypes {
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,
			StaticCore.MOD_ID);

	public static final RegistryObject<RecipeType<Research>> RESEARCH_RECIPE_TYPE = TYPES.register(Research.ID,
			() -> new StaticPowerRecipeType<Research>());
	public static final RegistryObject<RecipeType<ThermalConductivityRecipe>> THERMAL_CONDUCTIVITY_RECIPE_TYPE = TYPES
			.register(ThermalConductivityRecipe.ID, () -> new StaticPowerRecipeType<ThermalConductivityRecipe>());

	public static void init(IEventBus eventBus) {
		TYPES.register(eventBus);
	}
}
