package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.block.BlockStateIngredient;
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
		addRecipe("lava", ThermalRecipeBuilder.of(FluidIngredient.of(Fluids.LAVA), 0.5f, 0.8f).withTemperature(1500)
				.freezes(new FreezingBehaviour(700, Blocks.OBSIDIAN)));
		addRecipe("lava_flowing",
				ThermalRecipeBuilder.of(FluidIngredient.of(Fluids.FLOWING_LAVA), 1.0f, 0.8f).withTemperature(1500));

		// Coolant and ethanol should be the same, except ethanol catches fire at 200.
		addRecipe("coolant",
				ThermalRecipeBuilder.of(FluidIngredient.of(ModFluids.Coolant.getSource().get()), 0.25f, 2.5f));
		addRecipe("coolant_flowing",
				ThermalRecipeBuilder.of(FluidIngredient.of(ModFluids.Coolant.getFlowing().get()), 0.5f, 2.5f));

		addRecipe("ethanol",
				ThermalRecipeBuilder.of(FluidIngredient.of(ModFluids.Ethanol.getSource().get()), 0.25f, 2.5f)
						.overheats(new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState())));
		addRecipe("ethanol_flowing",
				ThermalRecipeBuilder.of(FluidIngredient.of(ModFluids.Ethanol.getFlowing().get()), 0.5f, 2.5f)
						.overheats(new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState())));

		addRecipe("oil", ThermalRecipeBuilder.of(FluidIngredient.of(ModFluids.CrudeOil.getSource().get()), 0.25f, 2000f)
				.overheats(new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState())));
		addRecipe("oil_flowing",
				ThermalRecipeBuilder.of(FluidIngredient.of(ModFluids.CrudeOil.getFlowing().get()), 0.5f, 2000f)
						.overheats(new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState())));

		addRecipe("water", ThermalRecipeBuilder
				.of(FluidIngredient.of(Fluids.WATER), IHeatStorage.WATER_CONDUCIVITY, IHeatStorage.WATER_SPECIFIC_HEAT)
				.overheats(new OverheatingBehaviour(100, ModFluids.Steam.getSource().get()))
				.freezes(new FreezingBehaviour(0, Blocks.ICE)));
		addRecipe("water_flowing",
				ThermalRecipeBuilder.of(FluidIngredient.of(Fluids.FLOWING_WATER), IHeatStorage.WATER_CONDUCIVITY * 2,
						IHeatStorage.WATER_SPECIFIC_HEAT).overheats(new OverheatingBehaviour(100)));

		addRecipe("campfire",
				ThermalRecipeBuilder.of(BlockStateIngredient.of(Blocks.CAMPFIRE), 1, 1200f).withTemperature(1000));
		addRecipe("fire", ThermalRecipeBuilder.of(BlockStateIngredient.of(Blocks.FIRE), 1, 1200f).withTemperature(750));
		addRecipe("torch",
				ThermalRecipeBuilder.of(BlockStateIngredient.of(Blocks.TORCH), 1, 1000f).withTemperature(300));

		addRecipe("stone", ThermalRecipeBuilder.of(BlockStateIngredient.of(Tags.Blocks.STONE), 2, 1000)
				.overheats(new OverheatingBehaviour(1000, Fluids.FLOWING_LAVA)));
		addRecipe("cobblestone", ThermalRecipeBuilder.of(BlockStateIngredient.of(Tags.Blocks.COBBLESTONE), 1, 1000)
				.overheats(new OverheatingBehaviour(1000, Fluids.LAVA)));
		addRecipe("obsidian", ThermalRecipeBuilder.of(BlockStateIngredient.of(Tags.Blocks.OBSIDIAN), 2, 1000)
				.overheats(new OverheatingBehaviour(1000, Fluids.LAVA)));

		addRecipe("dirt", ThermalRecipeBuilder.of(BlockStateIngredient.of(BlockTags.DIRT), 0.5f, 800f));
		addRecipe("planks", ThermalRecipeBuilder.of(BlockStateIngredient.of(BlockTags.PLANKS), 0, 1.75f)
				.overheats(new OverheatingBehaviour(200, Blocks.FIRE.defaultBlockState())));
		addRecipe("logs", ThermalRecipeBuilder.of(BlockStateIngredient.of(BlockTags.LOGS_THAT_BURN), 0.0f, 1500f)
				.overheats(new OverheatingBehaviour(300, ModBlocks.BurntLog.get().defaultBlockState())));

		addRecipe("ice", ThermalRecipeBuilder.of(BlockStateIngredient.of(BlockTags.ICE), 5, 2000).withTemperature(0)
				.overheats(new OverheatingBehaviour(100, Fluids.WATER)));

		addRecipe("aluminum_block",
				ThermalRecipeBuilder
						.of(BlockStateIngredient.of(ModMaterials.ALUMINUM.getBlockTag(MaterialTypes.STORAGE_BLOCK)),
								250, 900)
						.overheats(new OverheatingBehaviour(660,
								ModMaterials.ALUMINUM.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("copper_block",
				ThermalRecipeBuilder
						.of(BlockStateIngredient.of(ModMaterials.COPPER.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 400,
								400)
						.overheats(new OverheatingBehaviour(1000,
								ModMaterials.COPPER.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("tin_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.TIN.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 230, 220)
				.overheats(new OverheatingBehaviour(100, ModMaterials.TIN.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("zinc_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.ZINC.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 125, 380)
				.overheats(
						new OverheatingBehaviour(420, ModMaterials.ZINC.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("magnesium_block",
				ThermalRecipeBuilder
						.of(BlockStateIngredient.of(ModMaterials.MAGNESIUM.getBlockTag(MaterialTypes.STORAGE_BLOCK)),
								160, 1050)
						.overheats(new OverheatingBehaviour(650,
								ModMaterials.MAGNESIUM.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("lead_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.LEAD.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 40, 130)
				.overheats(
						new OverheatingBehaviour(320, ModMaterials.LEAD.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("platinum_block",
				ThermalRecipeBuilder
						.of(BlockStateIngredient.of(ModMaterials.PLATINUM.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 70,
								130)
						.overheats(new OverheatingBehaviour(1770,
								ModMaterials.PLATINUM.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("silver_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.SILVER.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 400, 235)
				.overheats(
						new OverheatingBehaviour(960, ModMaterials.SILVER.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("gold_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.GOLD.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 320, 130)
				.overheats(
						new OverheatingBehaviour(1000, ModMaterials.GOLD.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("iron_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.IRON.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 100, 450)
				.overheats(
						new OverheatingBehaviour(1100, ModMaterials.IRON.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("tungsten_block",
				ThermalRecipeBuilder
						.of(BlockStateIngredient.of(ModMaterials.TUNGSTEN.getBlockTag(MaterialTypes.STORAGE_BLOCK)),
								200, 130)
						.overheats(new OverheatingBehaviour(3400,
								ModMaterials.TUNGSTEN.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("bronze_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.BRONZE.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 200, 370)
				.overheats(
						new OverheatingBehaviour(900, ModMaterials.BRONZE.getFluidSource(MaterialTypes.MOLTEN_FLUID))));
		addRecipe("brass_block", ThermalRecipeBuilder
				.of(BlockStateIngredient.of(ModMaterials.BRASS.getBlockTag(MaterialTypes.STORAGE_BLOCK)), 150, 375)
				.overheats(
						new OverheatingBehaviour(900, ModMaterials.BRASS.getFluidSource(MaterialTypes.MOLTEN_FLUID))));

		addRecipe("wool", ThermalRecipeBuilder.of(BlockStateIngredient.of(BlockTags.WOOL), 0.04f, 3)
				.overheats(new OverheatingBehaviour(600, Blocks.FIRE.defaultBlockState())));
	}

	protected void addRecipe(String nameOverride, ThermalRecipeBuilder builder) {
		addRecipe(nameOverride, SCRecipeBuilder.create(builder.recipe()));
	}

	protected static class ThermalRecipeBuilder {
		private final BlockStateIngredient blocks;
		private final FluidIngredient fluids;

		private final float mass;
		private final float conductivity;
		private final float specificHeat;

		private boolean hasTemperature;
		private float temperature;
		private OverheatingBehaviour overheatingBehaviour;
		private FreezingBehaviour freezingBehaviour;

		public ThermalRecipeBuilder(BlockStateIngredient blocks, FluidIngredient fluids, float mass, float conductivity,
				float specificHeat) {
			this.blocks = blocks;
			this.fluids = fluids;
			this.mass = mass;
			this.conductivity = conductivity;
			this.specificHeat = specificHeat;
		}

		public static ThermalRecipeBuilder of(FluidIngredient fluids, float mass, float conductivity,
				float specificHeat) {
			return new ThermalRecipeBuilder(BlockStateIngredient.EMPTY, fluids, mass, conductivity, specificHeat);
		}

		public static ThermalRecipeBuilder of(BlockStateIngredient blocks, float mass, float conductivity,
				float specificHeat) {
			return new ThermalRecipeBuilder(blocks, FluidIngredient.EMPTY, mass, conductivity, specificHeat);
		}

		public static ThermalRecipeBuilder of(FluidIngredient fluids, float conductivity, float specificHeat) {
			return new ThermalRecipeBuilder(BlockStateIngredient.EMPTY, fluids, IHeatStorage.DEFAULT_BLOCK_MASS,
					conductivity, specificHeat);
		}

		public static ThermalRecipeBuilder of(BlockStateIngredient blocks, float conductivity, float specificHeat) {
			return new ThermalRecipeBuilder(blocks, FluidIngredient.EMPTY, IHeatStorage.DEFAULT_BLOCK_MASS,
					conductivity, specificHeat);
		}

		public ThermalRecipeBuilder withTemperature(float temperature) {
			this.hasTemperature = true;
			this.temperature = temperature;
			return this;
		}

		public ThermalRecipeBuilder overheats(OverheatingBehaviour overheatingBehaviour) {
			this.overheatingBehaviour = overheatingBehaviour;
			return this;
		}

		public ThermalRecipeBuilder freezes(FreezingBehaviour freezingBehaviour) {
			this.freezingBehaviour = freezingBehaviour;
			return this;
		}

		public ThermalConductivityRecipe recipe() {
			return new ThermalConductivityRecipe(null, blocks, fluids, hasTemperature, temperature, mass, specificHeat,
					conductivity, overheatingBehaviour, freezingBehaviour);
		}
	}
}
