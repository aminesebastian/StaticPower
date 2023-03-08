package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.generators.RecipeItem;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.generators.helpers.SolderingRecipeBuilder;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class SolderingRecipeGenerator extends SPRecipeProvider<SolderingRecipe> {

	public SolderingRecipeGenerator(DataGenerator dataGenerator) {
		super("soldering", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addCardRecipe("basic", ModItems.BasicCard.get(), RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()), RecipeItem.of(ModItems.BasicProcessor.get()),
				RecipeItem.of(Tags.Items.INGOTS_COPPER), RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		addCardRecipe("advanced", ModItems.AdvancedCard.get(), RecipeItem.of(ModMaterials.GOLD.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()), RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()), RecipeItem.of(Tags.Items.GEMS_DIAMOND));
		addCardRecipe("static", ModItems.StaticCard.get(), RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticProcessor.get()), RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.CrystalStatic.get()));
		addCardRecipe("energized", ModItems.EnergizedCard.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()), RecipeItem.of(ModMaterials.GOLD.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.CrystalEnergized.get()));
		addCardRecipe("lumum", ModItems.LumumCard.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumProcessor.get()), RecipeItem.of(ModMaterials.PLATINUM.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.CrystalLumum.get()));

		addProcessorRecipe("basic", ModItems.BasicProcessor.get(), RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItemTags.SILICON));
		addProcessorRecipe("advanced", ModItems.AdvancedProcessor.get(), RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItemTags.SILICON));
		addProcessorRecipe("static", ModItems.StaticProcessor.get(), RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItems.StaticDopedSilicon.get()));
		addProcessorRecipe("energized", ModItems.EnergizedProcessor.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.GOLD.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItems.EnergizedDopedSilicon.get()));
		addProcessorRecipe("lumum", ModItems.LumumProcessor.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.PLATINUM.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItems.LumumDopedSilicon.get()));

		// @formatter:off
		addRecipe("servo_from_ingots", SolderingRecipeBuilder.shaped(ModItems.Servo.get(), 3)
			.define('t', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.define('c', ModMaterials.COPPER.get(MaterialTypes.WIRE_COIL).getItemTag())
			.pattern(" t ")
			.pattern("tct")
			.pattern(" t "));
		// @formatter:on

		// @formatter:off
		addRecipe("servo_from_plates", SolderingRecipeBuilder.shaped(ModItems.Servo.get(), 3)
			.define('t', ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag())
			.define('c', ModMaterials.COPPER.get(MaterialTypes.WIRE_COIL).getItemTag())
			.pattern(" t ")
			.pattern("tct")
			.pattern(" t "));
		// @formatter:on

		// @formatter:off
		addRecipe("transistor", SolderingRecipeBuilder.shaped(ModItems.Transistor.get(), 2)
			.define('p', ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag())
			.define('s', ModItemTags.SILICON)
			.define('w', ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.pattern(" p ")
			.pattern("psp")
			.pattern("www"));
		// @formatter:on

		// @formatter:off
		addRecipe("diode", SolderingRecipeBuilder.shaped(ModItems.Diode.get(), 2)
			.define('w', ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.define('s', ModItemTags.SILICON)
			.pattern("   ")
			.pattern("wsw")
			.pattern("   "));
		// @formatter:on

		// @formatter:off
		addRecipe("memory_chip", SolderingRecipeBuilder.shaped(ModItems.MemoryChip.get(), 2)
			.define('p', ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag())
			.define('s', ModItemTags.SILICON)
			.define('w', ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.pattern("www")
			.pattern("psp")
			.pattern("www"));
		// @formatter:on

		// @formatter:off
		addRecipe("internal_clock", SolderingRecipeBuilder.shaped(ModItems.MemoryChip.get(), 2)
			.define('p', ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag())
			.define('c', Items.CLOCK)
			.define('w', ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.pattern("ppp")
			.pattern("pcp")
			.pattern("w w"));
		// @formatter:on
	}

	protected void addCardRecipe(String name, ItemLike output, RecipeItem plate, RecipeItem processor, RecipeItem contactMetal, RecipeItem sides) {
		// @formatter:off
		addRecipe("cards/"+name, SolderingRecipeBuilder.shaped(output)
			.define('l', plate)
			.define('p', processor)
			.define('w', contactMetal)
			.define('i', Tags.Items.INGOTS_IRON)
			.define('g', sides)
			.pattern("lwl")
			.pattern("gpg")
			.pattern("iii"));
		// @formatter:on
	}

	protected void addProcessorRecipe(String name, ItemLike output, RecipeItem plate, RecipeItem nugget, RecipeItem silicon) {
		// @formatter:off
		addRecipe("processor/"+name, SolderingRecipeBuilder.shaped(output)
			.define('p', plate)
			.define('n', nugget)
			.define('s', silicon)
			.pattern("pnp")
			.pattern("nsn")
			.pattern("pnp"));
		// @formatter:on
	}

	protected void addRecipe(String nameOverride, SolderingRecipeBuilder builder) {
		addRecipe(nameOverride, SPRecipeBuilder.create(builder.build(nameOverride), (recipe) -> recipe.toJson()));
	}
}
