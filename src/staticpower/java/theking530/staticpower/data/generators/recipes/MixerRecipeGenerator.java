package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModFluidTags;

public class MixerRecipeGenerator extends SCRecipeProvider<MixerRecipe> {

	public MixerRecipeGenerator(DataGenerator dataGenerator) {
		super("mixing", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("metal/brass", FluidIngredient.of(ModMaterials.COPPER.get(MaterialTypes.MOLTEN_FLUID).get().getTag(), 10),
				FluidIngredient.of(ModMaterials.ZINC.get(MaterialTypes.MOLTEN_FLUID).get().getTag(), 10),
				new FluidStack(ModMaterials.BRASS.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 20));

		addRecipe("metal/bronze", FluidIngredient.of(ModMaterials.COPPER.get(MaterialTypes.MOLTEN_FLUID).get().getTag(), 15),
				FluidIngredient.of(ModMaterials.TIN.get(MaterialTypes.MOLTEN_FLUID).get().getTag(), 5),
				new FluidStack(ModMaterials.BRONZE.get(MaterialTypes.MOLTEN_FLUID).get().getSource().get(), 20));

		addRecipe("misc/coolant", StaticPowerIngredient.of(Items.SNOWBALL), StaticPowerIngredient.EMPTY, FluidIngredient.of(FluidTags.WATER, 15),
				FluidIngredient.of(ModFluids.Ethanol.getTag(), 5), new FluidStack(ModFluids.Coolant.getSource().get(), 20));

		for (MinecraftColor color : MinecraftColor.values()) {
			addRecipe("concrete/" + color.getName(), FluidIngredient.of(ModFluidTags.CONCRETE, 100), FluidIngredient.of(ModFluids.DYES.get(color).getTag(), 10),
					new FluidStack(ModFluids.CONCRETE.get(color).getSource().get(), 100));
		}

		addRecipe("concrete/from_slag", StaticPowerIngredient.of(ItemTags.SAND), StaticPowerIngredient.of(ModItems.Slag.get()), FluidIngredient.of(FluidTags.WATER, 100),
				FluidIngredient.EMPTY, new FluidStack(ModFluids.CONCRETE.get(MinecraftColor.GRAY).getSource().get(), 200));
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2, FluidStack output) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new MixerRecipe(null, StaticPowerIngredient.EMPTY, StaticPowerIngredient.EMPTY, inputFluid1, inputFluid2, output, null)));
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2, FluidStack output, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride,
				SCRecipeBuilder.create(new MixerRecipe(null, StaticPowerIngredient.EMPTY, StaticPowerIngredient.EMPTY, inputFluid1, inputFluid2, output, processing)));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			FluidStack output) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new MixerRecipe(null, input1, input2, inputFluid1, inputFluid2, output, null)));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			FluidStack output, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new MixerRecipe(null, input1, input2, inputFluid1, inputFluid2, output, processing)));
	}
}
