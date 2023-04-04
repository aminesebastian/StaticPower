package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;

public class FermenterRecipeGenerator extends SCRecipeProvider<FermenterRecipe> {

	public FermenterRecipeGenerator(DataGenerator dataGenerator) {
		super("fermenting", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("apple", create(StaticPowerIngredient.of(Items.APPLE), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()),
				new FluidStack(ModFluids.Mash.getSource().get(), 150), FermenterRecipe.DEFAULT_PROCESSING_TIME * 2));
		addRecipe("wheat", create(StaticPowerIngredient.of(Tags.Items.CROPS_WHEAT), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()),
				new FluidStack(ModFluids.Mash.getSource().get(), 50)));
		addRecipe("beetroot", create(StaticPowerIngredient.of(Tags.Items.CROPS_BEETROOT), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()),
				new FluidStack(ModFluids.Mash.getSource().get(), 100), FermenterRecipe.DEFAULT_PROCESSING_TIME * 2));
		addRecipe("carrot", create(StaticPowerIngredient.of(Tags.Items.CROPS_CARROT), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()),
				new FluidStack(ModFluids.Mash.getSource().get(), 65)));
		addRecipe("potato", create(StaticPowerIngredient.of(Tags.Items.CROPS_POTATO), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()),
				new FluidStack(ModFluids.Mash.getSource().get(), 75)));
		addRecipe("sapling", create(StaticPowerIngredient.of(ItemTags.SAPLINGS), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()),
				new FluidStack(ModFluids.Mash.getSource().get(), 100), FermenterRecipe.DEFAULT_PROCESSING_TIME * 2));
		addRecipe("seeds", create(StaticPowerIngredient.of(Tags.Items.SEEDS), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()),
				new FluidStack(ModFluids.Mash.getSource().get(), 10), FermenterRecipe.DEFAULT_PROCESSING_TIME / 2));
		addRecipe("sugar_cane",
				create(StaticPowerIngredient.of(Items.SUGAR_CANE), StaticPowerOutputItem.of(ModItems.DistilleryGrain.get()), new FluidStack(ModFluids.Mash.getSource().get(), 50)));
	}

	protected SCRecipeBuilder<FermenterRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack fluid) {
		return create(input, output, fluid, null);
	}

	protected SCRecipeBuilder<FermenterRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack fluid, int processingTime) {
		return create(input, output, fluid, MachineRecipeProcessingSection.hardcoded(processingTime, CastingRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected SCRecipeBuilder<FermenterRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack fluid, MachineRecipeProcessingSection processing) {
		return SCRecipeBuilder.create(new FermenterRecipe(null, input, output, fluid, processing));
	}
}
