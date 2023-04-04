package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags.Fluids;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.tags.ModItemTags;

public class BottlerRecipeGenerator extends SCRecipeProvider<BottleRecipe> {

	public BottlerRecipeGenerator(DataGenerator dataGenerator) {
		super("bottling", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("apple_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.AppleJuiceBottle.get()),
				FluidIngredient.of(ModFluids.AppleJuice.getTag(), 1000)));
		addRecipe("beetroot_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.BeetJuiceBottle.get()),
				FluidIngredient.of(ModFluids.BeetJuice.getTag(), 1000)));
		addRecipe("berry_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.BerryJuiceBottle.get()),
				FluidIngredient.of(ModFluids.BerryJuice.getTag(), 1000)));
		addRecipe("carrot_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.CarrotJuiceBottle.get()),
				FluidIngredient.of(ModFluids.CarrotJuice.getTag(), 1000)));
		addRecipe("honey_bottle",
				create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(Items.HONEY_BOTTLE), FluidIngredient.of(ModFluids.Honey.getTag(), 1000)));
		addRecipe("melon_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.MelonJuiceBottle.get()),
				FluidIngredient.of(ModFluids.WatermelonJuice.getTag(), 1000)));
		addRecipe("milk", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.MilkBottle.get()), FluidIngredient.of(Fluids.MILK, 1000)));
		addRecipe("pumpkin_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.PumpkinJuiceBottle.get()),
				FluidIngredient.of(ModFluids.PumpkinJuice.getTag(), 1000)));
		addRecipe("water_bottle",
				create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(Items.GLASS_BOTTLE), FluidIngredient.of(FluidTags.WATER, 1000)));
	}

	protected SCRecipeBuilder<BottleRecipe> create(StaticPowerIngredient emptyBottle, StaticPowerOutputItem filledBottle, FluidIngredient fluid) {
		return SCRecipeBuilder.create(new BottleRecipe(null, emptyBottle, filledBottle, fluid, null));
	}
}
