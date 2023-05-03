package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import theking530.staticcore.block.BlockStateIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.FreezingBehaviour;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.OverheatingBehaviour;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.init.ModFluids;

public class ThermalConductivityRecipeGenerator extends SCRecipeProvider<ThermalConductivityRecipe> {

	public ThermalConductivityRecipeGenerator(DataGenerator dataGenerator) {
		super("thermal_conductivity", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("lava", FluidIngredient.of(1, Fluids.LAVA), true, 1500, 1);
		addRecipe("lava_flowing", FluidIngredient.of(1, Fluids.FLOWING_LAVA), true, 1500, 5);

		addRecipe("coolant", FluidIngredient.of(1, ModFluids.Coolant.getSource().get()), 100);
		addRecipe("coolant_flowing", FluidIngredient.of(1, ModFluids.Coolant.getFlowing().get()), 500);

		addRecipe("water", FluidIngredient.of(1, Fluids.WATER), 10,
				new OverheatingBehaviour(100, ModFluids.Steam.getBlock().get().defaultBlockState(),
						StaticPowerOutputItem.EMPTY),
				new FreezingBehaviour(0, Blocks.ICE.defaultBlockState(), StaticPowerOutputItem.EMPTY));
		addRecipe("water_flowing", FluidIngredient.of(1, Fluids.FLOWING_WATER), 400);

		addRecipe("campfire", BlockStateIngredient.of(Blocks.CAMPFIRE), true, 1000, 3);
		addRecipe("fire", BlockStateIngredient.of(Blocks.FIRE), true, 750, 5);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, float conductivity) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, false, 0, conductivity, null, null);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, boolean hasActiveTemperature, int temperature,
			float conductivity) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, hasActiveTemperature, temperature, conductivity, null, null);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, boolean hasActiveTemperature, int temperature,
			float conductivity) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, hasActiveTemperature, temperature, conductivity, null,
				null);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, boolean hasActiveTemperature, int temperature,
			float conductivity, OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, hasActiveTemperature, temperature, conductivity,
				overheatingBehaviour, freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, false, 0, conductivity, overheatingBehaviour,
				freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, boolean hasActiveTemperature, int temperature,
			float conductivity, OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, hasActiveTemperature, temperature, conductivity,
				overheatingBehaviour, freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, false, 0, conductivity, overheatingBehaviour,
				freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, FluidIngredient fluids,
			boolean hasActiveTemperature, int temperature, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new ThermalConductivityRecipe(null, blocks, fluids,
				hasActiveTemperature, temperature, conductivity, overheatingBehaviour, freezingBehaviour)));
	}
}
