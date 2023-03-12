package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.carpenter.CarpenterRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.generators.helpers.ShapedRecipePatternBuilder;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;

public class CarpenterRecipeGenerator extends SPRecipeProvider<CarpenterRecipe> {

	public CarpenterRecipeGenerator(DataGenerator dataGenerator) {
		super("carpenting", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		// @formatter:off
		addRecipe("barrel", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.define('s', ItemTags.WOODEN_SLABS)
			.pattern("wsw")
			.pattern("w w")
			.pattern("wsw"),
			StaticPowerOutputItem.of(Blocks.BARREL),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 12),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 30));
		// @formatter:on

		// @formatter:off
		addRecipe("beehive", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.define('h', Items.HONEYCOMB)
			.pattern("www")
			.pattern("hhh")
			.pattern("www"),
			StaticPowerOutputItem.of(Blocks.BEEHIVE),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 12),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 30));
		// @formatter:on

		// @formatter:off
		addRecipe("bowl", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.pattern("   ")
			.pattern("w w")
			.pattern(" w "),
			StaticPowerOutputItem.of(Items.BOWL, 4),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 6),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 15));
		// @formatter:on

		// @formatter:off
		addRecipe("chest", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.pattern("www")
			.pattern("w w")
			.pattern("www"),
			StaticPowerOutputItem.of(Blocks.CHEST),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 16),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 40));
		// @formatter:on

		// @formatter:off
		addRecipe("stick", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.pattern(" w ")
			.pattern(" w ")
			.pattern("   "),
			StaticPowerOutputItem.of(Items.STICK),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 4),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 10));
		// @formatter:on

		// @formatter:off
		addRecipe("shield", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.define('i', Tags.Items.INGOTS_IRON)
			.pattern("wiw")
			.pattern("www")
			.pattern(" w "),
			StaticPowerOutputItem.of(Items.SHIELD),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 12),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 30));
		// @formatter:on

		// @formatter:off
		addRecipe("bed", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.define('l', ItemTags.WOOL)
			.pattern("   ")
			.pattern("lll")
			.pattern("w w"),
			StaticPowerOutputItem.of(Blocks.RED_BED),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 4),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 10));
		// @formatter:on

		// @formatter:off
		addRecipe("piston", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('i', Tags.Items.INGOTS_IRON)
			.define('s', Tags.Items.COBBLESTONE)
			.pattern("www")
			.pattern("wiw")
			.pattern("srs"),
			StaticPowerOutputItem.of(Blocks.PISTON),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 10),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 20));
		// @formatter:on

		// @formatter:off
		addRecipe("note_block", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.pattern("www")
			.pattern("wrw")
			.pattern("www"),
			StaticPowerOutputItem.of(Blocks.NOTE_BLOCK),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 16),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 40));
		// @formatter:on

		// @formatter:off
		addRecipe("crafting_table", ShapedRecipePatternBuilder.create()
			.define('w', ItemTags.PLANKS)
			.pattern("ww ")
			.pattern("ww ")
			.pattern("   "),
			StaticPowerOutputItem.of(Blocks.CRAFTING_TABLE),
			StaticPowerOutputItem.of(ModItems.DustWood.get(), 8),
			new FluidStack(ModFluids.TreeOil.getSource().get(), 20));
		// @formatter:on
	}

	protected void addRecipe(String nameOverride, ShapedRecipePatternBuilder patternBuilder, StaticPowerOutputItem primaryOutput, FluidStack outputFluid) {
		addRecipe(nameOverride, patternBuilder, primaryOutput, StaticPowerOutputItem.EMPTY, outputFluid, null);
	}

	protected void addRecipe(String nameOverride, ShapedRecipePatternBuilder patternBuilder, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput) {
		addRecipe(nameOverride, patternBuilder, primaryOutput, secondaryOutput, FluidStack.EMPTY, null);
	}

	protected void addRecipe(String nameOverride, ShapedRecipePatternBuilder patternBuilder, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput,
			FluidStack outputFluid) {
		addRecipe(nameOverride, patternBuilder, primaryOutput, secondaryOutput, outputFluid, null);
	}

	protected void addRecipe(String nameOverride, ShapedRecipePatternBuilder patternBuilder, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput,
			FluidStack outputFluid, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SPRecipeBuilder.create(new CarpenterRecipe(null, patternBuilder.build(), primaryOutput, secondaryOutput, outputFluid, processing)));
	}
}
