package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;

public class CrucibleRecipeGenerator extends SPRecipeProvider<CrucibleRecipe> {

	public CrucibleRecipeGenerator(DataGenerator dataGenerator) {
		super("crucible", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (!material.has(MaterialTypes.MOLTEN_FLUID)) {
				continue;
			}

			if (material.has(MaterialTypes.NUGGET)) {
				addRecipe("/" + material.getName() + "_nugget",
						create(StaticPowerIngredient.of(material.get(MaterialTypes.NUGGET).getItemTag()), StaticPowerOutputItem.EMPTY,
								new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 16), CastingRecipe.DEFAULT_PROCESSING_TIME / 9));
			}
			if (material.has(MaterialTypes.CHUNKS)) {
				addRecipe("/" + material.getName() + "_chunks",
						create(StaticPowerIngredient.of(material.get(MaterialTypes.CHUNKS).getItemTag()), StaticPowerOutputItem.of(ModItems.Slag.get(), 1),
								new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 72), CastingRecipe.DEFAULT_PROCESSING_TIME));
			}
			if (material.has(MaterialTypes.INGOT)) {
				addRecipe("/" + material.getName() + "_ingot",
						create(StaticPowerIngredient.of(material.get(MaterialTypes.INGOT).getItemTag()), StaticPowerOutputItem.of(ModItems.Slag.get(), 2),
								new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 144), CastingRecipe.DEFAULT_PROCESSING_TIME));
			}
			if (material.has(MaterialTypes.DUST)) {
				addRecipe("/" + material.getName() + "_dust",
						create(StaticPowerIngredient.of(material.get(MaterialTypes.DUST).getItemTag()), StaticPowerOutputItem.of(ModItems.Slag.get(), 2),
								new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 144), CastingRecipe.DEFAULT_PROCESSING_TIME));
			}
			if (material.has(MaterialTypes.RAW_MATERIAL)) {
				addRecipe("/" + material.getName() + "_raw_material",
						create(StaticPowerIngredient.of(material.get(MaterialTypes.RAW_MATERIAL).getItemTag()), StaticPowerOutputItem.of(ModItems.Slag.get(), 5),
								new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 1296), CastingRecipe.DEFAULT_PROCESSING_TIME));
			}
			if (material.has(MaterialTypes.STORAGE_BLOCK)) {
				addRecipe("/" + material.getName() + "_block",
						create(StaticPowerIngredient.of(material.get(MaterialTypes.STORAGE_BLOCK).getItemTag()), StaticPowerOutputItem.of(ModItems.Slag.get(), 3),
								new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 1296), CastingRecipe.DEFAULT_PROCESSING_TIME * 9));
			}
			if (material.has(MaterialTypes.RAW_STOARGE_BLOCK)) {
				addRecipe("/" + material.getName() + "_raw_material_block",
						create(StaticPowerIngredient.of(material.get(MaterialTypes.RAW_STOARGE_BLOCK).getItemTag()), StaticPowerOutputItem.of(ModItems.Slag.get(), 3),
								new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 1296), CastingRecipe.DEFAULT_PROCESSING_TIME * 9));
			}
			if (material.hasOre()) {
				addRecipe("/" + material.getName() + "_ore", create(StaticPowerIngredient.of(material.getOreItemTag()), StaticPowerOutputItem.of(ModItems.Slag.get(), 9),
						new FluidStack(material.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 1296), CastingRecipe.DEFAULT_PROCESSING_TIME * 9));
			}
		}
	}

	protected SPRecipeBuilder<CrucibleRecipe> create(StaticPowerIngredient mold, StaticPowerOutputItem slag, FluidStack outputFluid) {
		return create(mold, slag, outputFluid, null);
	}

	protected SPRecipeBuilder<CrucibleRecipe> create(StaticPowerIngredient mold, StaticPowerOutputItem slag, FluidStack outputFluid, int processingTime) {
		return create(mold, slag, outputFluid, MachineRecipeProcessingSection.hardcoded(processingTime, CastingRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected SPRecipeBuilder<CrucibleRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem slag, FluidStack outputFluid, MachineRecipeProcessingSection processing) {
		return SPRecipeBuilder.create(new CrucibleRecipe(null, input, slag, outputFluid, processing));
	}
}
