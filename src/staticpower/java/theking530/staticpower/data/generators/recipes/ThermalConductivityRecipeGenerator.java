package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.block.BlockStateIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.FreezingBehaviour;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.OverheatingBehaviour;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModMaterials;

public class ThermalConductivityRecipeGenerator extends SCRecipeProvider<ThermalConductivityRecipe> {

	public ThermalConductivityRecipeGenerator(DataGenerator dataGenerator) {
		super("thermal_conductivity", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("lava", FluidIngredient.of(1, Fluids.LAVA), true, 1500, 2);
		addRecipe("lava_flowing", FluidIngredient.of(1, Fluids.FLOWING_LAVA), true, 1500, 5);

		addRecipe("coolant", FluidIngredient.of(1, ModFluids.Coolant.getSource().get()), 10);
		addRecipe("coolant_flowing", FluidIngredient.of(1, ModFluids.Coolant.getFlowing().get()), 20);

		addRecipe("ethanol", FluidIngredient.of(1, ModFluids.Ethanol.getSource().get()), 5,
				new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState()));
		addRecipe("ethanol_flowing", FluidIngredient.of(1, ModFluids.Ethanol.getFlowing().get()), 50,
				new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState()));

		addRecipe("oil", FluidIngredient.of(1, ModFluids.CrudeOil.getSource().get()), 1,
				new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState()));
		addRecipe("oil_flowing", FluidIngredient.of(1, ModFluids.CrudeOil.getFlowing().get()), 2,
				new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState()));

		addRecipe("water", FluidIngredient.of(1, Fluids.WATER), 1,
				new OverheatingBehaviour(100, ModFluids.Steam.getBlock().get().defaultBlockState(),
						StaticPowerOutputItem.EMPTY),
				new FreezingBehaviour(0, Blocks.ICE.defaultBlockState(), StaticPowerOutputItem.EMPTY));
		addRecipe("water_flowing", FluidIngredient.of(1, Fluids.FLOWING_WATER), 5, new OverheatingBehaviour(100),
				new FreezingBehaviour(0, Blocks.POWDER_SNOW.defaultBlockState(), StaticPowerOutputItem.EMPTY));

		addRecipe("campfire", BlockStateIngredient.of(Blocks.CAMPFIRE), true, 1000, 1);
		addRecipe("fire", BlockStateIngredient.of(Blocks.FIRE), true, 750, 1);
		addRecipe("torch", BlockStateIngredient.of(Blocks.TORCH), true, 300, 1);

		addRecipe("stone", BlockStateIngredient.of(Tags.Blocks.STONE), 2,
				new OverheatingBehaviour(1000, Fluids.FLOWING_LAVA));
		addRecipe("cobblestone", BlockStateIngredient.of(Tags.Blocks.COBBLESTONE), 1f,
				new OverheatingBehaviour(1000, Fluids.LAVA));
		addRecipe("obsidian", BlockStateIngredient.of(Tags.Blocks.OBSIDIAN), 2,
				new OverheatingBehaviour(1000, Fluids.LAVA));

		addRecipe("dirt", BlockStateIngredient.of(BlockTags.DIRT), 0.5f);
		addRecipe("planks", BlockStateIngredient.of(BlockTags.PLANKS), 0.0f,
				new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState()));
		addRecipe("logs", BlockStateIngredient.of(BlockTags.LOGS_THAT_BURN), 0.0f,
				new OverheatingBehaviour(300, ModBlocks.BurntLog.get().defaultBlockState()));

		addRecipe("ice", BlockStateIngredient.of(BlockTags.ICE), 5, new OverheatingBehaviour(100, Fluids.WATER));

		addRecipe("aluminum_block",
				BlockStateIngredient.of(ModMaterials.ALUMINUM.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 250,
				new OverheatingBehaviour(660,
						ModMaterials.ALUMINUM.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("copper_block",
				BlockStateIngredient.of(ModMaterials.COPPER.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 400,
				new OverheatingBehaviour(1000,
						ModMaterials.COPPER.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("tin_block", BlockStateIngredient.of(ModMaterials.TIN.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()),
				230, new OverheatingBehaviour(100,
						ModMaterials.TIN.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("zinc_block",
				BlockStateIngredient.of(ModMaterials.ZINC.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 125,
				new OverheatingBehaviour(420,
						ModMaterials.ZINC.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("magnesium_block",
				BlockStateIngredient.of(ModMaterials.MAGNESIUM.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 160,
				new OverheatingBehaviour(650,
						ModMaterials.MAGNESIUM.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("lead_block",
				BlockStateIngredient.of(ModMaterials.LEAD.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 40,
				new OverheatingBehaviour(320,
						ModMaterials.LEAD.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("platinum_block",
				BlockStateIngredient.of(ModMaterials.PLATINUM.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 70,
				new OverheatingBehaviour(1770,
						ModMaterials.PLATINUM.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("silver_block",
				BlockStateIngredient.of(ModMaterials.SILVER.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 400,
				new OverheatingBehaviour(960,
						ModMaterials.SILVER.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("gold_block",
				BlockStateIngredient.of(ModMaterials.GOLD.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 320,
				new OverheatingBehaviour(1000,
						ModMaterials.GOLD.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("iron_block",
				BlockStateIngredient.of(ModMaterials.IRON.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 100,
				new OverheatingBehaviour(1100,
						ModMaterials.IRON.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("tungsten_block",
				BlockStateIngredient.of(ModMaterials.TUNGSTEN.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 200,
				new OverheatingBehaviour(3400,
						ModMaterials.TUNGSTEN.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("bronze_block",
				BlockStateIngredient.of(ModMaterials.BRONZE.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 200,
				new OverheatingBehaviour(900,
						ModMaterials.BRONZE.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
		addRecipe("brass_block",
				BlockStateIngredient.of(ModMaterials.BRASS.get(MaterialTypes.STORAGE_BLOCK).getBlockTag()), 150,
				new OverheatingBehaviour(900,
						ModMaterials.BRASS.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get()));
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, float conductivity) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, false, 0, IHeatStorage.DEFAULT_BLOCK_MASS,
				conductivity, null, null);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, boolean hasActiveTemperature, int temperature,
			float conductivity) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, hasActiveTemperature, temperature,
				IHeatStorage.DEFAULT_BLOCK_MASS, conductivity, null, null);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, float conductivity,
			OverheatingBehaviour overheatingBehaviour) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, false, 0, IHeatStorage.DEFAULT_BLOCK_MASS,
				conductivity, overheatingBehaviour, null);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, float conductivity,
			OverheatingBehaviour overheatingBehaviour) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, false, 0, IHeatStorage.DEFAULT_BLOCK_MASS, conductivity,
				overheatingBehaviour, null);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, float conductivity) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, false, 0, IHeatStorage.DEFAULT_BLOCK_MASS, conductivity,
				null, null);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, boolean hasActiveTemperature,
			int temperature, float conductivity) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, hasActiveTemperature, temperature,
				IHeatStorage.DEFAULT_BLOCK_MASS, conductivity, null, null);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, boolean hasActiveTemperature,
			int temperature, float conductivity, OverheatingBehaviour overheatingBehaviour,
			FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, hasActiveTemperature, temperature,
				IHeatStorage.DEFAULT_BLOCK_MASS, conductivity, overheatingBehaviour, freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, blocks, FluidIngredient.EMPTY, false, 0, IHeatStorage.DEFAULT_BLOCK_MASS, conductivity,
				overheatingBehaviour, freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, boolean hasActiveTemperature, int temperature,
			float conductivity, OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, hasActiveTemperature, temperature,
				IHeatStorage.DEFAULT_BLOCK_MASS, conductivity, overheatingBehaviour, freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluids, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride, BlockStateIngredient.EMPTY, fluids, false, 0, IHeatStorage.DEFAULT_BLOCK_MASS,
				conductivity, overheatingBehaviour, freezingBehaviour);
	}

	protected void addRecipe(String nameOverride, BlockStateIngredient blocks, FluidIngredient fluids,
			boolean hasActiveTemperature, int temperature, float thermalMass, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		addRecipe(nameOverride,
				SCRecipeBuilder.create(new ThermalConductivityRecipe(null, blocks, fluids, hasActiveTemperature,
						temperature, thermalMass, conductivity, overheatingBehaviour, freezingBehaviour)));
	}
}
