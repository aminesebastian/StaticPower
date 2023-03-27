package theking530.staticcore.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCore;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipeSerializer;
import theking530.staticcore.research.Research;
import theking530.staticcore.research.ResearchSerializer;

public class StaticCoreRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, StaticCore.MOD_ID);

	public static final RegistryObject<ResearchSerializer> RESEARCH_SERIALIZER = SERIALIZERS.register(Research.ID,
			() -> new ResearchSerializer());
	public static final RegistryObject<ThermalConductivityRecipeSerializer> THERMAL_CONDUCTIVITY_SERIALIZER = SERIALIZERS
			.register(ThermalConductivityRecipe.ID, () -> new ThermalConductivityRecipeSerializer());

	public static void init(IEventBus eventBus) {
		SERIALIZERS.register(eventBus);
	}
}
