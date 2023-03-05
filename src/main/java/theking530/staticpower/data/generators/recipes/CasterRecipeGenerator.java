package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Blocks;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.NewModMaterials;

public class CasterRecipeGenerator extends SPRecipeProvider<CastingRecipe> {

	public CasterRecipeGenerator(DataGenerator dataGenerator) {
		super("casting", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle material : NewModMaterials.MATERIALS.values()) {
			if (!material.has(MaterialTypes.MOLTEN_FLUID)) {
				continue;
			}

			if (material.has(MaterialTypes.NUGGET)) {
				addRecipe("nuggets/" + material.getName(),
						create(StaticPowerIngredient.of(ModItems.MoldNugget.get()), StaticPowerOutputItem.of(material.get(MaterialTypes.NUGGET).get()),
								FluidIngredient.of(16, material.get(MaterialTypes.MOLTEN_FLUID).get().getTag()), CastingRecipe.DEFAULT_PROCESSING_TIME / 9));
			}
			if (material.has(MaterialTypes.INGOT)) {
				addRecipe("ingots/" + material.getName(),
						create(StaticPowerIngredient.of(ModItems.MoldIngot.get()), StaticPowerOutputItem.of(material.get(MaterialTypes.INGOT).get()),
								FluidIngredient.of(144, material.get(MaterialTypes.MOLTEN_FLUID).get().getTag()), CastingRecipe.DEFAULT_PROCESSING_TIME));
			}
			if (material.has(MaterialTypes.STORAGE_BLOCK)) {
				addRecipe("blocks/" + material.getName(),
						create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(material.get(MaterialTypes.STORAGE_BLOCK).get()),
								FluidIngredient.of(1296, material.get(MaterialTypes.MOLTEN_FLUID).get().getTag()), CastingRecipe.DEFAULT_PROCESSING_TIME * 9));
			}
		}

		addRecipe("concrete/white", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.WHITE_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.WHITE).getTag())));
		addRecipe("concrete/orange", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.ORANGE_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.ORANGE).getTag())));
		addRecipe("concrete/magenta", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.MAGENTA_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.MAGENTA).getTag())));
		addRecipe("concrete/light_blue", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.LIGHT_BLUE_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.LIGHT_BLUE).getTag())));
		addRecipe("concrete/yellow", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.YELLOW_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.YELLOW).getTag())));
		addRecipe("concrete/lime", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.LIME_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.LIME).getTag())));
		addRecipe("concrete/pink", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.PINK_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.PINK).getTag())));
		addRecipe("concrete/gray", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.GRAY_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.GRAY).getTag())));
		addRecipe("concrete/light_gray", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.LIGHT_GRAY_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.LIGHT_GRAY).getTag())));
		addRecipe("concrete/cyan", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.CYAN_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.CYAN).getTag())));
		addRecipe("concrete/purple", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.PURPLE_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.PURPLE).getTag())));
		addRecipe("concrete/blue", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.BLUE_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.BLUE).getTag())));
		addRecipe("concrete/brown", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.BROWN_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.BROWN).getTag())));
		addRecipe("concrete/green", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.GREEN_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.GREEN).getTag())));
		addRecipe("concrete/red", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.RED_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.RED).getTag())));
		addRecipe("concrete/black", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(Blocks.BLACK_CONCRETE),
				FluidIngredient.of(1000, ModFluids.CONCRETE.get(MinecraftColor.BLACK).getTag())));

		addRecipe("latex", create(StaticPowerIngredient.of(ModItems.MoldBlock.get()), StaticPowerOutputItem.of(ModBlocks.BlockLatex.get()),
				FluidIngredient.of(1000, ModFluids.Latex.getTag())));
	}

	protected SPRecipeBuilder<CastingRecipe> create(StaticPowerIngredient mold, StaticPowerOutputItem output, FluidIngredient inputFluid) {
		return create(mold, output, inputFluid, null);
	}

	protected SPRecipeBuilder<CastingRecipe> create(StaticPowerIngredient mold, StaticPowerOutputItem output, FluidIngredient inputFluid, int processingTime) {
		return create(mold, output, inputFluid, MachineRecipeProcessingSection.hardcoded(processingTime, CastingRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected SPRecipeBuilder<CastingRecipe> create(StaticPowerIngredient mold, StaticPowerOutputItem output, FluidIngredient inputFluid,
			MachineRecipeProcessingSection processing) {
		return SPRecipeBuilder.create(new CastingRecipe(null, mold, output, inputFluid, processing));
	}
}
