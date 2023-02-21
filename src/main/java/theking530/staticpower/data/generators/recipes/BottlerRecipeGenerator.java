package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags.Fluids;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.tags.ModItemTags;

public class BottlerRecipeGenerator extends SPRecipeProvider<BottleRecipe> {

	public BottlerRecipeGenerator(DataGenerator dataGenerator) {
		super("bottling", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("apple_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.AppleJuiceBottle.get()),
				FluidIngredient.of(1000, ModFluids.AppleJuice.getTag())));
		addRecipe("beetroot_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.BeetJuiceBottle.get()),
				FluidIngredient.of(1000, ModFluids.BeetJuice.getTag())));
		addRecipe("berry_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.BerryJuiceBottle.get()),
				FluidIngredient.of(1000, ModFluids.BerryJuice.getTag())));
		addRecipe("carrot_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.CarrotJuiceBottle.get()),
				FluidIngredient.of(1000, ModFluids.CarrotJuice.getTag())));
		addRecipe("honey_bottle",
				create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(Items.HONEY_BOTTLE), FluidIngredient.of(1000, ModFluids.Honey.getTag())));
		addRecipe("melon_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.MelonJuiceBottle.get()),
				FluidIngredient.of(1000, ModFluids.WatermelonJuice.getTag())));
		addRecipe("milk", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.MilkBottle.get()), FluidIngredient.of(1000, Fluids.MILK)));
		addRecipe("pumpkin_juice", create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(ModItems.PumpkinJuiceBottle.get()),
				FluidIngredient.of(1000, ModFluids.PumpkinJuice.getTag())));
		addRecipe("water_bottle",
				create(StaticPowerIngredient.of(ModItemTags.GLASS_BOTTLES), StaticPowerOutputItem.of(Items.GLASS_BOTTLE), FluidIngredient.of(1000, FluidTags.WATER)));
	}

	protected SPRecipeBuilder<BottleRecipe> create(StaticPowerIngredient emptyBottle, StaticPowerOutputItem filledBottle, FluidIngredient fluid) {
		return SPRecipeBuilder.create(new BottleRecipe(null, emptyBottle, filledBottle, fluid));
	}
}
