package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.cokeoven.CokeOvenRecipe;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class CokeOvenRecipeGenerator extends SCRecipeProvider<AlloyFurnaceRecipe> {

	public CokeOvenRecipeGenerator(DataGenerator dataGenerator) {
		super("coke_oven", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("coal_coke", StaticPowerIngredient.of(Items.COAL),
				StaticPowerOutputItem.of(ModMaterials.COAL_COKE.get(MaterialTypes.RAW_MATERIAL).get()),
				new FluidStack(ModFluids.CreosoteOil.getSource().get(), 250), 1, 60 * 20);

		addRecipe("coal_coke_block", StaticPowerIngredient.of(Items.COAL_BLOCK),
				StaticPowerOutputItem.of(ModMaterials.COAL_COKE.get(MaterialTypes.RAW_STOARGE_BLOCK).get()),
				new FluidStack(ModFluids.CreosoteOil.getSource().get(), 250 * 9), 9, 60 * 20 * 9);

		addRecipe("charcoal_from_logs", StaticPowerIngredient.of(ItemTags.LOGS_THAT_BURN),
				StaticPowerOutputItem.of(Items.CHARCOAL), new FluidStack(ModFluids.CreosoteOil.getSource().get(), 100),
				1, 20 * 20);

		addRecipe("charcoal_from_dust", StaticPowerIngredient.of(ModItemTags.WOOD_DUST, 9),
				StaticPowerOutputItem.of(Items.CHARCOAL), new FluidStack(ModFluids.CreosoteOil.getSource().get(), 100),
				1, 20 * 20);

		addRecipe("charcoal_from_planks", StaticPowerIngredient.of(ItemTags.PLANKS, 4),
				StaticPowerOutputItem.of(Items.CHARCOAL), new FluidStack(ModFluids.CreosoteOil.getSource().get(), 100),
				1, 20 * 20);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output,
			FluidStack outputFluid, float experience, int processingTime) {
		addRecipe(nameOverride, input, output, outputFluid, experience,
				MachineRecipeProcessingSection.hardcoded(processingTime, 0, 0, 0));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output,
			FluidStack outputFluid, float experience, MachineRecipeProcessingSection processing) {
		CokeOvenRecipe recipe = new CokeOvenRecipe(null, input, output, outputFluid, experience, processing);
		addRecipe(nameOverride, SCRecipeBuilder.create(recipe));
	}
}
