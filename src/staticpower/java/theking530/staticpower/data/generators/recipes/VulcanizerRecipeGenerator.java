package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class VulcanizerRecipeGenerator extends SCRecipeProvider<VulcanizerRecipe> {

	public VulcanizerRecipeGenerator(DataGenerator dataGenerator) {
		super("vulcanization", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("rubber_bar", StaticPowerIngredient.of(ModItemTags.COALS_DUST), FluidIngredient.of(ModFluids.Latex.getTag(), 100),
				StaticPowerOutputItem.of(ModItems.RubberBar.get()));

		addRecipe("wire/insulated_copper_wire", StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 125), StaticPowerOutputItem.of(ModMaterials.COPPER.get(MaterialTypes.INSULATED_WIRE).get()));
		addRecipe("wire/insulated_brass_wire", StaticPowerIngredient.of(ModMaterials.BRASS.get(MaterialTypes.WIRE).getItemTag()), FluidIngredient.of(ModFluids.Latex.getTag(), 125),
				StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.INSULATED_WIRE).get()));
		addRecipe("wire/insulated_static_wire", StaticPowerIngredient.of(ModMaterials.STATIC_METAL.get(MaterialTypes.WIRE).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 125), StaticPowerOutputItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INSULATED_WIRE).get()));
		addRecipe("wire/insulated_energized_wire", StaticPowerIngredient.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.WIRE).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 125), StaticPowerOutputItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INSULATED_WIRE).get()));
		addRecipe("wire/insulated_lumum_wire", StaticPowerIngredient.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.WIRE).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 125), StaticPowerOutputItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INSULATED_WIRE).get()));

		addRecipe("wire/insulated_copper_wire_coil", StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.WIRE_COIL).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 1000), StaticPowerOutputItem.of(ModMaterials.COPPER.get(MaterialTypes.INSULATED_WIRE_COIL).get()));
		addRecipe("wire/insulated_brass_wire_coil", StaticPowerIngredient.of(ModMaterials.BRASS.get(MaterialTypes.WIRE_COIL).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 1000), StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.INSULATED_WIRE_COIL).get()));
		addRecipe("wire/insulated_static_wire_coil", StaticPowerIngredient.of(ModMaterials.STATIC_METAL.get(MaterialTypes.WIRE_COIL).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 1000), StaticPowerOutputItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INSULATED_WIRE_COIL).get()));
		addRecipe("wire/insulated_energized_wire_coil", StaticPowerIngredient.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.WIRE_COIL).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 1000), StaticPowerOutputItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INSULATED_WIRE_COIL).get()));
		addRecipe("wire/insulated_lumum_wire_coil", StaticPowerIngredient.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.WIRE_COIL).getItemTag()),
				FluidIngredient.of(ModFluids.Latex.getTag(), 1000), StaticPowerOutputItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INSULATED_WIRE_COIL).get()));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient inputItem, FluidIngredient inputFluid, StaticPowerOutputItem output) {
		addRecipe(nameOverride, inputItem, inputFluid, output, null);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid, StaticPowerOutputItem output) {
		addRecipe(nameOverride, inputFluid, output, null);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid, StaticPowerOutputItem output, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new VulcanizerRecipe(null, StaticPowerIngredient.EMPTY, inputFluid, output, processing)));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient inputItem, FluidIngredient inputFluid, StaticPowerOutputItem output,
			MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new VulcanizerRecipe(null, inputItem, inputFluid, output, processing)));
	}
}
