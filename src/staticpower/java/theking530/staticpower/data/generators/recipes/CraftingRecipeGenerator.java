package theking530.staticpower.data.generators.recipes;

import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.crafting.RecipeItem;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.data.generators.helpers.SCShapedRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCShapelessRecipeBuilder;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.Tiers;
import theking530.staticpower.data.Tiers.RedstoneCableTier;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class CraftingRecipeGenerator extends SCRecipeProvider<CraftingRecipe> {

	public CraftingRecipeGenerator(DataGenerator generator) {
		super("crafting", generator);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildRecipes() {

		beginShapelessRecipe(ModBlocks.StaticPlanks.get(), 4, "wood/static_planks").requires(ModBlocks.StaticLog.get())
				.unlockedBy("has_log", hasItems(ModBlocks.StaticLog.get()));
		beginShapelessRecipe(ModBlocks.EnergizedPlanks.get(), 4, "wood/energized_planks")
				.requires(ModBlocks.EnergizedLog.get()).unlockedBy("has_log", hasItems(ModBlocks.EnergizedLog.get()));
		beginShapelessRecipe(ModBlocks.LumumPlanks.get(), 4, "wood/lumum_planks").requires(ModBlocks.LumumLog.get())
				.unlockedBy("has_log", hasItems(ModBlocks.LumumLog.get()));
		beginShapelessRecipe(ModBlocks.RubberTreePlanks.get(), 4, "wood/rubber_planks")
				.requires(ModItemTags.RUBBER_WOOD_LOGS).unlockedBy("has_log", hasItems(ModItemTags.RUBBER_WOOD_LOGS));

		// @formatter:off
		beginShapedRecipe(ModBlocks.RubberTreeStrippedWood.get(), "wood/rubber_wood_stripped/")
			.define('w',ModBlocks.RubberTreeStrippedLog.get())
			.pattern("ww ")
			.pattern("ww ")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(ModBlocks.RubberTreeStrippedLog.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.BlockRubber.get(), "rubber_block_from_bars")
			.define('i', ModItems.RubberBar.get())
			.pattern("iii")
			.pattern("iii")
			.pattern("iii")
			.unlockedBy("has_ingot", hasItems(ModItems.RubberBar.get()));
		// @formatter:on

		beginShapelessRecipe(ModItems.RubberBar.get(), 9, "rubber_bars_from_block")
				.requires(ModBlocks.BlockRubber.get()).unlockedBy("has__block", hasItems(ModBlocks.BlockRubber.get()));

		// @formatter:off
		beginShapedRecipe(ModBlocks.BlockLatex.get(), "latex_block_from_chunks")
			.define('i', ModItems.LatexChunk.get())
			.pattern("iii")
			.pattern("iii")
			.pattern("iii")
			.unlockedBy("has_ingot", hasItems(ModItems.LatexChunk.get()));
		// @formatter:on

		beginShapelessRecipe(ModItems.LatexChunk.get(), 9, "latex_chunks_from_block")
				.requires(ModBlocks.BlockLatex.get()).unlockedBy("has__block", hasItems(ModBlocks.BlockLatex.get()));

		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (material.hasGeneratedMaterial(MaterialTypes.STORAGE_BLOCK)
					&& material.hasGeneratedMaterial(MaterialTypes.INGOT)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.STORAGE_BLOCK).get(), "storage_blocks/from_ingots/" + material.getName())
					.define('i', material.get(MaterialTypes.INGOT).getItemTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_ingot", hasItems(material.get(MaterialTypes.INGOT).getItemTag()));
				// @formatter:on

				beginShapelessRecipe(material.get(MaterialTypes.INGOT).get(), 9,
						"ingots/from_block/" + material.getName())
						.requires(material.get(MaterialTypes.STORAGE_BLOCK).getItemTag())
						.unlockedBy("has__block", hasItems(material.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));
			}

			if (material.hasGeneratedMaterial(MaterialTypes.RAW_STOARGE_BLOCK)
					&& material.hasGeneratedMaterial(MaterialTypes.RAW_MATERIAL)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.RAW_STOARGE_BLOCK).get(), "storage_blocks/from_raw_materials/" + material.getName())
					.define('i', material.get(MaterialTypes.RAW_MATERIAL).getItemTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_raw_material", hasItems(material.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
				// @formatter:on

				beginShapelessRecipe(material.get(MaterialTypes.RAW_MATERIAL).get(), 9,
						"raw_materials/from_block/" + material.getName())
						.requires(material.get(MaterialTypes.RAW_STOARGE_BLOCK).getItemTag()).unlockedBy(
								"has_raw_block", hasItems(material.get(MaterialTypes.RAW_STOARGE_BLOCK).getItemTag()));
			}

			if (material.has(MaterialTypes.INGOT) && material.hasGeneratedMaterial(MaterialTypes.NUGGET)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.INGOT).get(), "ingots/from_nuggets/" + material.getName())
					.define('i', material.get(MaterialTypes.NUGGET).getItemTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_nugget", hasItems(material.get(MaterialTypes.NUGGET).getItemTag()));
				// @formatter:on

				beginShapelessRecipe(material.get(MaterialTypes.NUGGET).get(), 9,
						"nuggets/from_ingot/" + material.getName())
						.requires(material.get(MaterialTypes.INGOT).getItemTag())
						.unlockedBy("has_ingot", hasItems(material.get(MaterialTypes.INGOT).getItemTag()));
			}

			if (material.has(MaterialTypes.GEAR)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.GEAR).get(), "gear/from_ingots/" + material.getName())
					.define('i', material.get(MaterialTypes.INGOT).getItemTag())
					.pattern(" i ")
					.pattern("i i")
					.pattern(" i ")
					.unlockedBy("has_" + material.getName() + "_ingot", hasItems(material.get(MaterialTypes.INGOT).getItemTag()));
				// @formatter:on

				if (material.has(MaterialTypes.GEAR_BOX)) {
					// @formatter:off
					beginShapedRecipe(material.get(MaterialTypes.GEAR_BOX).get(), "gear_box/" + material.getName())
						.define('i', material.get(MaterialTypes.INGOT).getItemTag())
						.define('g', material.get(MaterialTypes.GEAR).getItemTag())
						.pattern("g  ")
						.pattern(" ig")
						.pattern("g  ")
						.unlockedBy("has_items", hasItems(material.get(MaterialTypes.GEAR).getItemTag()));
					// @formatter:on
				}
			}

			if (material.has(MaterialTypes.WIRE)) {
				beginShapelessRecipe(material.get(MaterialTypes.WIRE).get(), 3, "wire/from_ingot/" + material.getName())
						.requires(material.get(MaterialTypes.INGOT).getItemTag()).requires(ModItemTags.WIRE_CUTTER)
						.unlockedBy("has_ingot", hasItems(material.get(MaterialTypes.INGOT).getItemTag()))
						.unlockedBy("has_wire_cutter", hasWireCutter());

				if (material.has(MaterialTypes.PLATE)) {
					beginShapelessRecipe(material.get(MaterialTypes.WIRE).get(), 3,
							"wire/from_plate/" + material.getName())
							.requires(material.get(MaterialTypes.PLATE).getItemTag()).requires(ModItemTags.WIRE_CUTTER)
							.unlockedBy("has_plate", hasItems(material.get(MaterialTypes.INGOT).getItemTag()))
							.unlockedBy("has_wire_cutter", hasWireCutter());
				}
			}

			if (material.has(MaterialTypes.INSULATED_WIRE)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.INSULATED_WIRE).get(), 3, "wire/insulated/" + material.getName())
					.define('r', ModItemTags.RUBBER)
					.define('w', material.get(MaterialTypes.WIRE).getItemTag())
					.pattern("rwr")
					.pattern("rwr")
					.pattern("rwr")
					.unlockedBy("has_" + material.getName() + "_wire", hasItems(material.get(MaterialTypes.WIRE).getItemTag()))
					.unlockedBy("has_rubber", hasItems(ModItemTags.RUBBER));
				// @formatter:on
			}

			if (material.has(MaterialTypes.WIRE_COIL)) {
				wireCoil(material.getName(), material.get(MaterialTypes.WIRE_COIL).get(),
						RecipeItem.of(material.get(MaterialTypes.WIRE).getItemTag()));
			}

			if (material.has(MaterialTypes.INSULATED_WIRE_COIL)) {
				wireCoil("insulted_" + material.getName(), material.get(MaterialTypes.INSULATED_WIRE_COIL).get(),
						RecipeItem.of(material.get(MaterialTypes.INSULATED_WIRE).getItemTag()));
			}

			if (material.has(MaterialTypes.CUT_STORAGE_BLOCK) && material.has(MaterialTypes.STORAGE_BLOCK)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.CUT_STORAGE_BLOCK).get().asItem(), 4, "cut_metal/" + material.getName())
					.define('b', material.get(MaterialTypes.STORAGE_BLOCK).getItemTag())
					.pattern("bb ")
					.pattern("bb ")
					.pattern("   ")
					.unlockedBy("has_metal", hasItems(material.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));
				// @formatter:on
			}
		}

		// @formatter:off
		beginShapedRecipe(ModItems.WireCoilDigistore.get(), 4, "wire/coil_digistore")
			.define('i', ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.define('s', Tags.Items.RODS_WOODEN)
			.define('w', ItemTags.WOOL)
			.pattern("wiw")
			.pattern("isi")
			.pattern("wiw")
			.unlockedBy("has_items", hasItems(Tags.Items.INGOTS_COPPER));
		// @formatter:on

		beginShapelessRecipe(ModMaterials.COPPER.get(MaterialTypes.DUST).get(), 2, "dusts/brass_from_dusts")
				.requires(ModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag())
				.requires(ModMaterials.ZINC.get(MaterialTypes.DUST).getItemTag()).unlockedBy(
						"has_brass_dust_components", hasItems(ModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag(),
								ModMaterials.ZINC.get(MaterialTypes.DUST).getItemTag()));

		beginShapelessRecipe(ModMaterials.COPPER.get(MaterialTypes.DUST).get(), 4, "dusts/bronze_from_dusts")
				.requires(ModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag())
				.requires(ModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag())
				.requires(ModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag())
				.requires(ModMaterials.TIN.get(MaterialTypes.DUST).getItemTag()).unlockedBy("has_brass_dust_components",
						hasItems(ModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag(),
								ModMaterials.TIN.get(MaterialTypes.DUST).getItemTag()));

		beginShapelessRecipe(ModMaterials.INERT_INFUSION.get(MaterialTypes.DUST).get(), 3,
				"dusts/inert_infusion_from_dusts").requires(ModMaterials.IRON.get(MaterialTypes.DUST).getItemTag())
				.requires(ModMaterials.GOLD.get(MaterialTypes.DUST).getItemTag())
				.requires(ModMaterials.PLATINUM.get(MaterialTypes.DUST).getItemTag())
				.unlockedBy("has_inert_infusion_dust_components",
						hasItems(ModMaterials.IRON.get(MaterialTypes.DUST).getItemTag(),
								ModMaterials.GOLD.get(MaterialTypes.DUST).getItemTag(),
								ModMaterials.PLATINUM.get(MaterialTypes.DUST).getItemTag()));

		beginShapelessRecipe(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.DUST).get(), 2,
				"dusts/redstone_alloy_from_dusts").requires(ModMaterials.SILVER.get(MaterialTypes.DUST).getItemTag())
				.requires(Items.REDSTONE).unlockedBy("has_redston_alloy_dust_components",
						hasItems(ModMaterials.SILVER.get(MaterialTypes.DUST).getItemTag(), Tags.Items.DUSTS_REDSTONE));

		portableBattery("basic", ModItems.BasicPortableBattery.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.NUGGET).getItemTag()),
				RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		portableBattery("advanced", ModItems.AdvancedPortableBattery.get(),
				RecipeItem.of(ModMaterials.GOLD.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.NUGGET).getItemTag()),
				RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		portableBattery("static", ModItems.StaticPortableBattery.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.NUGGET).getItemTag()),
				RecipeItem.of(ModItems.CrystalStatic.get()));
		portableBattery("energized", ModItems.EnergizedPortableBattery.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(Tags.Items.NUGGETS_GOLD), RecipeItem.of(ModItems.CrystalEnergized.get()));
		portableBattery("lumum", ModItems.LumumPortableBattery.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.PLATINUM.get(MaterialTypes.NUGGET).getItemTag()),
				RecipeItem.of(ModItems.CrystalLumum.get()));

		batteryPack("basic", ModItems.BasicBatteryPack.get(),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()), RecipeItem.of(ModItems.BasicProcessor.get()));
		batteryPack("advanced", ModItems.AdvancedBatteryPack.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		batteryPack("static", ModItems.StaticBatteryPack.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()), RecipeItem.of(ModItems.StaticProcessor.get()));
		batteryPack("energized", ModItems.EnergizedBatteryPack.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		batteryPack("lumum", ModItems.LumumBatteryPack.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()), RecipeItem.of(ModItems.LumumProcessor.get()));

		heatCable("aluminum", ModBlocks.HeatCables.get(StaticPowerTiers.ALUMINUM).get(),
				RecipeItem.of(ModMaterials.ALUMINUM.get(MaterialTypes.INGOT).getItemTag()));
		heatCable("copper", ModBlocks.HeatCables.get(StaticPowerTiers.COPPER).get(),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.INGOT).getItemTag()));
		heatCable("gold", ModBlocks.HeatCables.get(StaticPowerTiers.GOLD).get(), RecipeItem.of(Tags.Items.INGOTS_GOLD));

		// @formatter:off
		beginShapedRecipe(ModBlocks.ScaffoldCable.get(), 8, "scaffold_cable")
			.define('i', Tags.Items.INGOTS_IRON)
			.define('t', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.pattern(" t ")
			.pattern(" i ")
			.pattern(" t ")
			.unlockedBy("has_items", hasItems(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.DigistoreCable.get(), 8, "digistore_cable")
			.define('i', Tags.Items.INGOTS_COPPER)
			.define('p', ItemTags.WOOL)
			.pattern("ppp")
			.pattern("iii")
			.pattern("ppp")
			.unlockedBy("has_items", hasItems(Tags.Items.INGOTS_COPPER, ItemTags.WOOL));
		// @formatter:on

		itemTube("basic", ModBlocks.ItemCables.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("advanced", ModBlocks.ItemCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("static", ModBlocks.ItemCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("energized", ModBlocks.ItemCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("lumum", ModBlocks.ItemCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		fluidPipe("basic", ModBlocks.FluidCables.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("advanced", ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("static", ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("energized", ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("lumum", ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		capillaryFluidPipe("basic", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("advanced", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("static", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("energized", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("lumum", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		industrialFluidPipe("basic", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));
		industrialFluidPipe("advanced", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get()));
		industrialFluidPipe("static", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get()));
		industrialFluidPipe("energized", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get()));
		industrialFluidPipe("lumum", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));

		powerCable("basic", ModBlocks.PowerCables.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("advanced", ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("static", ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("energized", ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("lumum", ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		insulatedPowerCable("basic", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.BASIC).get()));
		insulatedPowerCable("advanced", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get()));
		insulatedPowerCable("static", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get()));
		insulatedPowerCable("energized", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get()));
		insulatedPowerCable("lumum", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));

		industrialPowerCable("basic", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));
		industrialPowerCable("advanced", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get()));
		industrialPowerCable("static", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get()));
		industrialPowerCable("energized", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get()));
		industrialPowerCable("lumum", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));

		// @formatter:off
		beginShapedRecipe(ModBlocks.BasicRedstoneCableNaked.get(), 6, "redstone_cable/naked")
			.define('i', ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.pattern("   ")
			.pattern("iii")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on

		for (RedstoneCableTier tier : Tiers.getRedstone()) {
			coloredRedstoneCable(tier.color().getName(), ModBlocks.RedstoneCables.get(tier.location()).get(),
					RecipeItem.of(ModItemTags.createForgeTag("wool/" + tier.color().getName())));
		}

		extractorAttachment("basic", ModItems.BasicExtractorAttachment.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("advanced", ModItems.AdvancedExtractorAttachment.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("static", ModItems.StaticExtractorAttachment.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("energized", ModItems.EnergizedExtractorAttachment.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("lumum", ModItems.LumumExtractorAttachment.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		filterAttachment("basic", ModItems.BasicFilterAttachment.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("advanced", ModItems.AdvancedFilterAttachment.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("static", ModItems.StaticFilterAttachment.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("energized", ModItems.EnergizedFilterAttachment.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("lumum", ModItems.LumumFilterAttachment.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		retrieverAttachment("basic", ModItems.BasicRetrieverAttachment.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("advanced", ModItems.AdvancedRetrieverAttachment.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("static", ModItems.StaticRetrieverAttachment.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("energized", ModItems.EnergizedRetrieverAttachment.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("lumum", ModItems.LumumRetrieverAttachment.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		chainsawBlade("iron", ModItems.IronChainsawBlade.get(), RecipeItem.of(ModItems.IronBlade.get()));
		chainsawBlade("bronze", ModItems.BronzeChainsawBlade.get(), RecipeItem.of(ModItems.BronzeBlade.get()));
		chainsawBlade("advanced", ModItems.AdvancedChainsawBlade.get(), RecipeItem.of(ModItems.AdvancedBlade.get()));
		chainsawBlade("static", ModItems.StaticChainsawBlade.get(), RecipeItem.of(ModItems.StaticBlade.get()));
		chainsawBlade("energized", ModItems.EnergizedChainsawBlade.get(), RecipeItem.of(ModItems.EnergizedBlade.get()));
		chainsawBlade("lumum", ModItems.LumumChainsawBlade.get(), RecipeItem.of(ModItems.LumumBlade.get()));
		chainsawBlade("tungsten", ModItems.TungstenChainsawBlade.get(), RecipeItem.of(ModItems.TungstenBlade.get()));

		chest("basic", ModBlocks.BasicChest.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()));
		chest("advanced", ModBlocks.AdvancedChest.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()));
		chest("static", ModBlocks.StaticChest.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()));
		chest("energized", ModBlocks.EnergizedChest.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()));
		chest("lumum", ModBlocks.LumumChest.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()));

		fluidCapsule("iron", ModItems.IronFluidCapsule.get(),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag()), RecipeItem.of(Items.BUCKET));
		fluidCapsule("basic", ModItems.BasicFluidCapsule.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.IronFluidCapsule.get()));
		fluidCapsule("advanced", ModItems.AdvancedFluidCapsule.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicFluidCapsule.get()));
		fluidCapsule("static", ModItems.StaticFluidCapsule.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedFluidCapsule.get()));
		fluidCapsule("energized", ModItems.EnergizedFluidCapsule.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticFluidCapsule.get()));
		fluidCapsule("lumum", ModItems.LumumFluidCapsule.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedFluidCapsule.get()));

		pie("apple", ModItems.ApplePie.get(), RecipeItem.of(Items.APPLE));
		pie("static", ModItems.StaticPie.get(), RecipeItem.of(ModItems.StaticFruit.get()));
		pie("energized", ModItems.EnergizedPie.get(), RecipeItem.of(ModItems.EnergizedFruit.get()));
		pie("lumum", ModItems.LumumPie.get(), RecipeItem.of(ModItems.LumumFruit.get()));

		for (MinecraftColor color : MinecraftColor.values()) {
			lightbulb(color.getName(), ModItems.Lightbulbs.get(color).get(),
					RecipeItem.of(ModItemTags.createForgeTag("glass_panes/" + color.getName())));
			bed(color.getName(), color.getColoredBed().get(), RecipeItem.of(color.getColoredWool().get()));
		}

		poweredMagnet("basic", ModItems.BasicMagnet.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()));
		poweredMagnet("advanced", ModItems.AdvancedMagnet.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()));
		poweredMagnet("static", ModItems.StaticMagnet.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()));
		poweredMagnet("energized", ModItems.EnergizedMagnet.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()));
		poweredMagnet("lumum", ModItems.LumumMagnet.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.WeakMagnet.get(), "tools/magnet/weak")
			.define('t', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.define('i', ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag())
			.pattern(" t ")
			.pattern("t t")
			.pattern("i i")
			.unlockedBy("has_items", hasItems(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag(), ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on

		solarPanel("basic", ModBlocks.SolarPanelBasic.get(), RecipeItem.of(ModItems.BasicProcessor.get()),
				RecipeItem.of(ModItems.Silicon.get()));
		solarPanel("advanced", ModBlocks.SolarPanelAdvanced.get(), RecipeItem.of(ModItems.AdvancedProcessor.get()),
				RecipeItem.of(ModItems.Silicon.get()));
		solarPanel("static", ModBlocks.SolarPanelStatic.get(), RecipeItem.of(ModItems.StaticProcessor.get()),
				RecipeItem.of(ModItems.StaticDopedSilicon.get()));
		solarPanel("energized", ModBlocks.SolarPanelEnergized.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()),
				RecipeItem.of(ModItems.EnergizedDopedSilicon.get()));
		solarPanel("lumum", ModBlocks.SolarPanelLumum.get(), RecipeItem.of(ModItems.LumumProcessor.get()),
				RecipeItem.of(ModItems.LumumDopedSilicon.get()));

		turbineBlade("wooden", ModItems.WoodTurbineBlades.get(), RecipeItem.of(Tags.Items.RODS_WOODEN),
				RecipeItem.of(ItemTags.PLANKS));
		turbineBlade("basic", ModItems.BasicTurbineBlades.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("advanced", ModItems.AdvancedTurbineBlades.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("static", ModItems.StaticTurbineBlades.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("energized", ModItems.EnergizedTurbineBlades.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("lumum", ModItems.LumumTurbineBlades.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR_BOX).get()));

		wireTerminal("low_voltage", ModBlocks.WireConnectorLV.get(), RecipeItem.of(Items.BRICK),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("medium_voltage", ModBlocks.WireConnectorMV.get(), RecipeItem.of(ModItemTags.RUBBER),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("high_voltage", ModBlocks.WireConnectorHV.get(),
				RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("extreme_voltage", ModBlocks.WireConnectorEV.get(),
				RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("bonkers_voltage", ModBlocks.WireConnectorBV.get(),
				RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("digistore", ModBlocks.WireConnectorDigistore.get(), RecipeItem.of(ItemTags.WOOL),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()));

		chainsaw("basic", ModItems.BasicChainsaw.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()), RecipeItem.of(ModItems.BasicProcessor.get()));
		chainsaw("advanced", ModItems.AdvancedChainsaw.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		chainsaw("static", ModItems.StaticChainsaw.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()), RecipeItem.of(ModItems.StaticProcessor.get()));
		chainsaw("energized", ModItems.EnergizedChainsaw.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		chainsaw("lumum", ModItems.LumumChainsaw.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()), RecipeItem.of(ModItems.LumumProcessor.get()));

		miningDrill("basic", ModItems.BasicMiningDrill.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()), RecipeItem.of(ModItems.BasicProcessor.get()));
		miningDrill("advanced", ModItems.AdvancedMiningDrill.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		miningDrill("static", ModItems.StaticMiningDrill.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()), RecipeItem.of(ModItems.StaticProcessor.get()));
		miningDrill("energized", ModItems.EnergizedMiningDrill.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		miningDrill("lumum", ModItems.LumumMiningDrill.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()), RecipeItem.of(ModItems.LumumProcessor.get()));

		hammer("iron", ModItems.IronMetalHammer.get(),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()));
		hammer("zinc", ModItems.ZincMetalHammer.get(),
				RecipeItem.of(ModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()));
		hammer("tin", ModItems.TinMetalHammer.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		hammer("copper", ModItems.CopperMetalHammer.get(),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.INGOT).getItemTag()));
		hammer("bronze", ModItems.BronzeMetalHammer.get(),
				RecipeItem.of(ModMaterials.BRONZE.get(MaterialTypes.INGOT).getItemTag()));
		hammer("tungsten", ModItems.TungstenMetalHammer.get(),
				RecipeItem.of(ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()));

		wireCutter("iron", ModItems.IronWireCutters.get(),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.GEAR).getItemTag()));
		wireCutter("zinc", ModItems.ZincWireCutters.get(),
				RecipeItem.of(ModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.ZINC.get(MaterialTypes.GEAR).getItemTag()));
		wireCutter("bronze", ModItems.BronzeWireCutters.get(),
				RecipeItem.of(ModMaterials.BRONZE.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.BRONZE.get(MaterialTypes.GEAR).getItemTag()));
		wireCutter("tungsten", ModItems.TungstenWireCutters.get(),
				RecipeItem.of(ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.TUNGSTEN.get(MaterialTypes.GEAR).getItemTag()));

		saw("iron", ModItems.IronCoverSaw.get(),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()));
		saw("ruby", ModItems.RubyCoverSaw.get(),
				RecipeItem.of(ModMaterials.RUBY.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		saw("saphhire", ModItems.SapphireCoverSaw.get(),
				RecipeItem.of(ModMaterials.SAPPHIRE.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		saw("diamond", ModItems.DiamondCoverSaw.get(), RecipeItem.of(Tags.Items.GEMS_DIAMOND));
		saw("tungsten", ModItems.TungstenCoverSaw.get(),
				RecipeItem.of(ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()));

		drillBit("iron", ModItems.IronDrillBit.get(),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("advanced", ModItems.AdvancedDrillBit.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("bronze", ModItems.BronzeDrillBit.get(),
				RecipeItem.of(ModMaterials.BRONZE.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.BRONZE.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("static", ModItems.StaticDrillBit.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("energized", ModItems.EnergizedDrillBit.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("lumum", ModItems.LumumDrillBit.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("tungsten", ModItems.TungstenDrillBit.get(),
				RecipeItem.of(ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModMaterials.TUNGSTEN.get(MaterialTypes.PLATE).getItemTag()));

		filter("basic", ModItems.BasicFilter.get(), RecipeItem.of(ModItems.BasicProcessor.get()));
		filter("advanced", ModItems.AdvancedFilter.get(), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		filter("static", ModItems.StaticFilter.get(), RecipeItem.of(ModItems.StaticProcessor.get()));
		filter("energized", ModItems.EnergizedFilter.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		filter("lumum", ModItems.LumumFilter.get(), RecipeItem.of(ModItems.LumumProcessor.get()));

		farmland("static", ModBlocks.StaticFarmland.get(), RecipeItem.of(ModItems.StaticProcessor.get()));
		farmland("energized", ModBlocks.EnergizedFarmland.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		farmland("lumum", ModBlocks.LumumFarmland.get(), RecipeItem.of(ModItems.LumumProcessor.get()));

		digistoreCard("basic", ModItems.BasicDigistoreCard.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicCard.get()), RecipeItem.of(ModItems.DigistoreCore.get()));
		digistoreStackedCard("basic", ModItems.BasicStackedDigistoreCard.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicCard.get()), RecipeItem.of(ModItems.DigistoreCore.get()));
		digistoreMonoCard("basic", ModItems.BasicSingularDigistoreCard.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicCard.get()), RecipeItem.of(ModItems.DigistoreCore.get()));

		digistoreCard("advanced", ModItems.AdvancedDigistoreCard.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedCard.get()), RecipeItem.of(ModItems.BasicDigistoreCard.get()));
		digistoreStackedCard("advanced", ModItems.AdvancedStackedDigistoreCard.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedCard.get()), RecipeItem.of(ModItems.BasicStackedDigistoreCard.get()));
		digistoreMonoCard("advanced", ModItems.AdvancedSingularDigistoreCard.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedCard.get()), RecipeItem.of(ModItems.BasicSingularDigistoreCard.get()));

		digistoreCard("static", ModItems.StaticDigistoreCard.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticCard.get()), RecipeItem.of(ModItems.AdvancedDigistoreCard.get()));
		digistoreStackedCard("static", ModItems.StaticStackedDigistoreCard.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticCard.get()), RecipeItem.of(ModItems.AdvancedStackedDigistoreCard.get()));
		digistoreMonoCard("static", ModItems.StaticSingularDigistoreCard.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticCard.get()), RecipeItem.of(ModItems.AdvancedSingularDigistoreCard.get()));

		digistoreCard("energized", ModItems.EnergizedDigistoreCard.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedCard.get()), RecipeItem.of(ModItems.StaticDigistoreCard.get()));
		digistoreStackedCard("energized", ModItems.EnergizedStackedDigistoreCard.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticStackedDigistoreCard.get()),
				RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));
		digistoreMonoCard("energized", ModItems.EnergizedSingularDigistoreCard.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticSingularDigistoreCard.get()),
				RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));

		digistoreCard("lumum", ModItems.LumumDigistoreCard.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumCard.get()), RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));
		digistoreStackedCard("lumum", ModItems.LumumStackedDigistoreCard.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumCard.get()), RecipeItem.of(ModItems.EnergizedStackedDigistoreCard.get()));
		digistoreMonoCard("lumum", ModItems.LumumSingularDigistoreCard.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumCard.get()), RecipeItem.of(ModItems.EnergizedSingularDigistoreCard.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.PatternCard.get(), "digistore/pattern_card")
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('c', ModItems.DigistoreCore.get())
			.define('p', ModMaterials.MAGNESIUM.get(MaterialTypes.INGOT).getItemTag())
			.pattern("ggg")
			.pattern("rcr")
			.pattern("ppp")
			.unlockedBy("has_digistore_core", hasItems(ModItems.DigistoreCore.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.VoidUpgrade.get(), "upgrades/void")
			.define('o', Tags.Items.OBSIDIAN)
			.define('u', ModItems.BasicUpgradePlate.get())
			.pattern("ooo")
			.pattern("ouo")
			.pattern("ooo")
			.unlockedBy("has_upgrade_plate", hasItems(ModItems.BasicUpgradePlate.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.TeleportUpgrade.get(), "upgrades/teleport")
			.define('o', Tags.Items.OBSIDIAN)
			.define('p', Tags.Items.ENDER_PEARLS)
			.define('u', ModItems.BasicUpgradePlate.get())
			.pattern("ooo")
			.pattern("opo")
			.pattern("ouo")
			.unlockedBy("has_upgrade_plate", hasItems(ModItems.BasicUpgradePlate.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.ExperienceVacuumUpgrade.get(), "upgrades/experience")
			.define('o', Tags.Items.OBSIDIAN)
			.define('p', Tags.Items.GEMS_EMERALD)
			.define('u', ModItems.BasicUpgradePlate.get())
			.pattern("ooo")
			.pattern("opo")
			.pattern("ouo")
			.unlockedBy("has_upgrade_plate", hasItems(ModItems.BasicUpgradePlate.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.AcceleratorUpgrade.get(), "upgrades/digistore/accelerator")
			.define('g', Tags.Items.GLASS)
			.define('r', Tags.Items.DUSTS_GLOWSTONE)
			.define('d', Tags.Items.DUSTS_REDSTONE)
			.define('u', ModItems.AdvancedUpgradePlate.get())
			.pattern("ggg")
			.pattern("drd")
			.pattern("gug")
			.unlockedBy("has_upgrade_plate", hasItems(ModItems.AdvancedUpgradePlate.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.CraftingUpgrade.get(), "upgrades/digistore/crafting")
			.define('g', Tags.Items.GLASS)
			.define('r', Items.CRAFTING_TABLE)
			.define('d', Tags.Items.GEMS_DIAMOND)
			.define('u', ModItems.EnergizedUpgradePlate.get())
			.pattern("ggg")
			.pattern("drd")
			.pattern("gug")
			.unlockedBy("has_upgrade_plate", hasItems(ModItems.EnergizedUpgradePlate.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.StackUpgrade.get(), "upgrades/digistore/stack")
			.define('g', Tags.Items.GLASS)
			.define('r', Tags.Items.STORAGE_BLOCKS_QUARTZ)
			.define('d', Tags.Items.GEMS_EMERALD)
			.define('u', ModItems.StaticUpgradePlate.get())
			.pattern("ggg")
			.pattern("drd")
			.pattern("gug")
			.unlockedBy("has_upgrade_plate", hasItems(ModItems.StaticUpgradePlate.get()));
		// @formatter:on

		upgradePlate("basic", ModItems.BasicUpgradePlate.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicProcessor.get()));
		upgradePlate("advanced", ModItems.AdvancedUpgradePlate.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()));
		upgradePlate("static", ModItems.StaticUpgradePlate.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticProcessor.get()));
		upgradePlate("energized", ModItems.EnergizedUpgradePlate.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		upgradePlate("lumum", ModItems.LumumUpgradePlate.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumProcessor.get()));

		centrifugeUpgrade("basic", ModItems.BasicCentrifugeUpgrade.get(),
				RecipeItem.of(ModItems.BasicUpgradePlate.get()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.GEAR).getItemTag()));
		centrifugeUpgrade("static", ModItems.StaticCentrifugeUpgrade.get(),
				RecipeItem.of(ModItems.StaticUpgradePlate.get()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.GEAR).getItemTag()));
		centrifugeUpgrade("energized", ModItems.EnergizedCentrifugeUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR).getItemTag()));
		centrifugeUpgrade("lumum", ModItems.LumumCentrifugeUpgrade.get(),
				RecipeItem.of(ModItems.LumumUpgradePlate.get()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR).getItemTag()));

		heatUpgrade("basic", ModItems.BasicHeatUpgrade.get(), RecipeItem.of(ModItems.BasicUpgradePlate.get()),
				RecipeItem.of(ModItems.BasicHeatCapacityUpgrade.get()));
		heatUpgrade("static", ModItems.StaticHeatUpgrade.get(), RecipeItem.of(ModItems.StaticUpgradePlate.get()),
				RecipeItem.of(ModItems.StaticHeatCapacityUpgrade.get()));
		heatUpgrade("energized", ModItems.EnergizedHeatUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()),
				RecipeItem.of(ModItems.EnergizedHeatCapacityUpgrade.get()));
		heatUpgrade("lumum", ModItems.LumumHeatUpgrade.get(), RecipeItem.of(ModItems.LumumUpgradePlate.get()),
				RecipeItem.of(ModItems.LumumHeatCapacityUpgrade.get()));

		heatCapacityUpgrade("basic", ModItems.BasicHeatCapacityUpgrade.get(),
				RecipeItem.of(ModItems.BasicUpgradePlate.get()), RecipeItem.of(ModBlocks.AluminumHeatSink.get()));
		heatCapacityUpgrade("static", ModItems.StaticHeatCapacityUpgrade.get(),
				RecipeItem.of(ModItems.StaticUpgradePlate.get()), RecipeItem.of(ModBlocks.AluminumHeatSink.get()));
		heatCapacityUpgrade("energized", ModItems.EnergizedHeatCapacityUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()), RecipeItem.of(ModBlocks.CopperHeatSink.get()));
		heatCapacityUpgrade("lumum", ModItems.LumumHeatCapacityUpgrade.get(),
				RecipeItem.of(ModItems.LumumUpgradePlate.get()), RecipeItem.of(ModBlocks.CopperHeatSink.get()));

		outputUpgrade("basic", ModItems.BasicOutputMultiplierUpgrade.get(),
				RecipeItem.of(ModItems.BasicUpgradePlate.get()), RecipeItem.of(Tags.Items.GEMS_LAPIS));
		outputUpgrade("static", ModItems.StaticOutputMultiplierUpgrade.get(),
				RecipeItem.of(ModItems.StaticUpgradePlate.get()), RecipeItem.of(Tags.Items.STORAGE_BLOCKS_LAPIS));
		outputUpgrade("energized", ModItems.EnergizedOutputMultiplierUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()), RecipeItem.of(Tags.Items.STORAGE_BLOCKS_LAPIS));
		outputUpgrade("lumum", ModItems.LumumOutputMultiplierUpgrade.get(),
				RecipeItem.of(ModItems.LumumUpgradePlate.get()), RecipeItem.of(Tags.Items.STORAGE_BLOCKS_LAPIS));

		powerUpgrade("basic", ModItems.BasicPowerUpgrade.get(), RecipeItem.of(ModItems.BasicUpgradePlate.get()),
				RecipeItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag()));
		powerUpgrade("static", ModItems.StaticPowerUpgrade.get(), RecipeItem.of(ModItems.StaticUpgradePlate.get()),
				RecipeItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag()));
		powerUpgrade("energized", ModItems.EnergizedPowerUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()),
				RecipeItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));
		powerUpgrade("lumum", ModItems.LumumPowerUpgrade.get(), RecipeItem.of(ModItems.LumumUpgradePlate.get()),
				RecipeItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));

		rangeUpgrade("basic", ModItems.BasicRangeUpgrade.get(), RecipeItem.of(ModItems.BasicUpgradePlate.get()),
				RecipeItem.of(ModMaterials.RUBY.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		rangeUpgrade("static", ModItems.StaticRangeUpgrade.get(), RecipeItem.of(ModItems.StaticUpgradePlate.get()),
				RecipeItem.of(ModMaterials.SAPPHIRE.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		rangeUpgrade("energized", ModItems.EnergizedRangeUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()), RecipeItem.of(Tags.Items.GEMS_EMERALD));
		rangeUpgrade("lumum", ModItems.LumumRangeUpgrade.get(), RecipeItem.of(ModItems.LumumUpgradePlate.get()),
				RecipeItem.of(Tags.Items.GEMS_DIAMOND));

		speedUpgrade("basic", ModItems.BasicSpeedUpgrade.get(), RecipeItem.of(ModItems.BasicUpgradePlate.get()));
		speedUpgrade("static", ModItems.StaticSpeedUpgrade.get(), RecipeItem.of(ModItems.StaticUpgradePlate.get()));
		speedUpgrade("energized", ModItems.EnergizedSpeedUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()));
		speedUpgrade("lumum", ModItems.LumumSpeedUpgrade.get(), RecipeItem.of(ModItems.LumumUpgradePlate.get()));

		transformerUpgrade("mv", ModItems.TransformerUpgradeMV.get(), RecipeItem.of(ModItems.BasicUpgradePlate.get()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModBlocks.TransformerBasic.get()));
		transformerUpgrade("hv", ModItems.TransformerUpgradeHV.get(),
				RecipeItem.of(ModItems.AdvancedUpgradePlate.get()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModBlocks.TransformerAdvanced.get()));
		transformerUpgrade("vhv", ModItems.TransformerUpgradeVHV.get(),
				RecipeItem.of(ModItems.StaticUpgradePlate.get()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModBlocks.TransformerStatic.get()));
		transformerUpgrade("ev", ModItems.TransformerUpgradeEV.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModBlocks.TransformerEnergized.get()));

		tankUpgrade("basic", ModItems.BasicTankUpgrade.get(), RecipeItem.of(ModItems.BasicUpgradePlate.get()));
		tankUpgrade("static", ModItems.StaticTankUpgrade.get(), RecipeItem.of(ModItems.StaticUpgradePlate.get()));
		tankUpgrade("energized", ModItems.EnergizedTankUpgrade.get(),
				RecipeItem.of(ModItems.EnergizedUpgradePlate.get()));
		tankUpgrade("lumum", ModItems.LumumTankUpgrade.get(), RecipeItem.of(ModItems.LumumUpgradePlate.get()));

		batteryBlock("basic", ModBlocks.BatteryBasic.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockBasic.get()), RecipeItem.of(ModItems.BasicPortableBattery.get()));
		batteryBlock("advanced", ModBlocks.BatteryAdvanced.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockAdvanced.get()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()));
		batteryBlock("static", ModBlocks.BatteryStatic.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockStatic.get()), RecipeItem.of(ModItems.StaticPortableBattery.get()));
		batteryBlock("energized", ModBlocks.BatteryEnergized.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockEnergized.get()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()));
		batteryBlock("lumum", ModBlocks.BatteryLumum.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockLumum.get()), RecipeItem.of(ModItems.LumumPortableBattery.get()));

		tank("iron", ModBlocks.IronTank.get(), RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.IronFluidCapsule.get()));
		tank("basic", ModBlocks.BasicTank.get(), RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicFluidCapsule.get()));
		tank("advanced", ModBlocks.AdvancedTank.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedFluidCapsule.get()));
		tank("static", ModBlocks.StaticTank.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticFluidCapsule.get()));
		tank("energized", ModBlocks.EnergizedTank.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedFluidCapsule.get()));
		tank("lumum", ModBlocks.LumumTank.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumFluidCapsule.get()));

		tankUpgrade("basic", ModBlocks.BasicTank.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.IronTank.get()));
		tankUpgrade("advanced", ModBlocks.AdvancedTank.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.BasicTank.get()));
		tankUpgrade("static", ModBlocks.StaticTank.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.AdvancedTank.get()));
		tankUpgrade("energized", ModBlocks.EnergizedTank.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.StaticTank.get()));
		tankUpgrade("lumum", ModBlocks.LumumTank.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModBlocks.EnergizedTank.get()));

		heatsink("aluminium", ModBlocks.AluminumHeatSink.get(),
				RecipeItem.of(ModMaterials.ALUMINUM.get(MaterialTypes.WIRE_COIL).getItemTag()));
		heatsink("copper", ModBlocks.CopperHeatSink.get(),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE_COIL).getItemTag()));

		pump("basic", ModBlocks.BasicPump.get(), RecipeItem.of(ModBlocks.MachineBlockBasic.get()),
				RecipeItem.of(ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.BASIC).get()));
		pump("advanced", ModBlocks.AdvancedPump.get(), RecipeItem.of(ModBlocks.MachineBlockAdvanced.get()),
				RecipeItem.of(ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ADVANCED).get()));
		pump("static", ModBlocks.StaticPump.get(), RecipeItem.of(ModBlocks.MachineBlockStatic.get()),
				RecipeItem.of(ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.STATIC).get()));
		pump("energized", ModBlocks.EnergizedPump.get(), RecipeItem.of(ModBlocks.MachineBlockEnergized.get()),
				RecipeItem.of(ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ENERGIZED).get()));
		pump("lumum", ModBlocks.LumumPump.get(), RecipeItem.of(ModBlocks.MachineBlockLumum.get()),
				RecipeItem.of(ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.LUMUM).get()));

		machineBlock("light", ModBlocks.MachineBlockAluminum.get(),
				RecipeItem.of(ModMaterials.ALUMINUM.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ALUMINUM.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		machineBlock("basic", ModBlocks.MachineBlockBasic.get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.BasicProcessor.get()));
		machineBlock("advanced", ModBlocks.MachineBlockAdvanced.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()));
		machineBlock("static", ModBlocks.MachineBlockStatic.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.StaticProcessor.get()));
		machineBlock("energized", ModBlocks.MachineBlockEnergized.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		machineBlock("lumum", ModBlocks.MachineBlockLumum.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.LumumProcessor.get()));
		machineBlock("industrial", ModBlocks.MachineBlockIndustrial.get(),
				RecipeItem.of(ModMaterials.STEEL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STEEL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()));


		resistor("1w", ModBlocks.Resistors.get(1).get().asItem(), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()),
				RecipeItem.of(MinecraftColor.RED.getDyeTag()), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()));
		resistor("5w", ModBlocks.Resistors.get(5).get().asItem(), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()),
				RecipeItem.of(MinecraftColor.PURPLE.getDyeTag()), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()));
		resistor("10w", ModBlocks.Resistors.get(10).get().asItem(), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()),
				RecipeItem.of(MinecraftColor.GRAY.getDyeTag()), RecipeItem.of(MinecraftColor.RED.getDyeTag()));
		resistor("25w", ModBlocks.Resistors.get(25).get().asItem(), RecipeItem.of(MinecraftColor.YELLOW.getDyeTag()),
				RecipeItem.of(MinecraftColor.PURPLE.getDyeTag()), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()));
		resistor("50w", ModBlocks.Resistors.get(50).get().asItem(), RecipeItem.of(MinecraftColor.PURPLE.getDyeTag()),
				RecipeItem.of(MinecraftColor.GRAY.getDyeTag()), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()));
		resistor("100w", ModBlocks.Resistors.get(100).get().asItem(), RecipeItem.of(MinecraftColor.RED.getDyeTag()),
				RecipeItem.of(MinecraftColor.RED.getDyeTag()), RecipeItem.of(MinecraftColor.GRAY.getDyeTag()));
		resistor("250w", ModBlocks.Resistors.get(250).get().asItem(), RecipeItem.of(MinecraftColor.YELLOW.getDyeTag()),
				RecipeItem.of(MinecraftColor.PURPLE.getDyeTag()), RecipeItem.of(MinecraftColor.RED.getDyeTag()));
		resistor("500w", ModBlocks.Resistors.get(500).get().asItem(), RecipeItem.of(MinecraftColor.PURPLE.getDyeTag()),
				RecipeItem.of(MinecraftColor.GRAY.getDyeTag()), RecipeItem.of(MinecraftColor.RED.getDyeTag()));
		resistor("1000w", ModBlocks.Resistors.get(1000).get().asItem(), RecipeItem.of(MinecraftColor.RED.getDyeTag()),
				RecipeItem.of(MinecraftColor.RED.getDyeTag()), RecipeItem.of(MinecraftColor.GREEN.getDyeTag()));

		circuitBreaker("2w", ModBlocks.CircuitBreakers.get(2).get().asItem(),
				RecipeItem.of(ModMaterials.ALUMINUM.get(MaterialTypes.PLATE).getItemTag()));
		circuitBreaker("5w", ModBlocks.CircuitBreakers.get(5).get().asItem(),
				RecipeItem.of(ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag()));
		circuitBreaker("10w", ModBlocks.CircuitBreakers.get(10).get().asItem(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()));
		circuitBreaker("20w", ModBlocks.CircuitBreakers.get(20).get().asItem(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()));
		circuitBreaker("50w", ModBlocks.CircuitBreakers.get(50).get().asItem(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()));
		circuitBreaker("100w", ModBlocks.CircuitBreakers.get(100).get().asItem(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()));

		// @formatter:off
		beginShapedRecipe(ModItems.DigistoreCore.get())
			.define('g', Tags.Items.DUSTS_GLOWSTONE)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('z', ModMaterials.ZINC.plateOrIngotTag())
			.pattern("   ")
			.pattern("rzg")
			.pattern("   ")
			.unlockedBy("has_digistore_core", hasItems(ModItems.DigistoreCore.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.Plug.get())
			.define('t', Tags.Items.INGOTS_GOLD)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.pattern("t t")
			.pattern(" r ")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(Tags.Items.INGOTS_GOLD, Tags.Items.DUSTS_REDSTONE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.IOPort.get())
			.define('t', ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag())
			.define('r', ModItemTags.RUBBER_SHEET)
			.define('c', ModMaterials.COPPER.get(MaterialTypes.PLATE).getItemTag())
			.pattern("   ")
			.pattern("trc")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(Tags.Items.INGOTS_GOLD, Tags.Items.DUSTS_REDSTONE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.PortableSmeltingCore.get())
			.define('t', ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag())
			.define('f', Blocks.SOUL_CAMPFIRE)
			.pattern("tft")
			.pattern("ftf")
			.pattern("tft")
			.unlockedBy("has_items", hasItems(Blocks.SOUL_CAMPFIRE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.PumpTube.get())
			.define('t', ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag())
			.define('r', ModItemTags.RUBBER_SHEET)
			.define('l', Tags.Items.LEATHER)
			.pattern("trt")
			.pattern("l l")
			.pattern("trt")
			.unlockedBy("has_items", hasItems( ModItemTags.RUBBER_SHEET));
		// @formatter:on

		beginShapelessRecipe(ModItems.RubberBar.get(), "rubber_bar_from_latex").requires(ModItems.LatexChunk.get(), 2)
				.requires(ModItemTags.COALS_DUST).unlockedBy("has_latex_chunk", hasItems(ModItems.LatexChunk.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.Motor.get())
			.define('c', ModMaterials.COPPER.get(MaterialTypes.WIRE_COIL).getItemTag())
			.define('i', ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag())
			.define('t', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.define('m', ModItems.Motor.get())
			.pattern("tt ")
			.pattern("mct")
			.pattern("ii ")
			.unlockedBy("has_items", hasItems(ModItems.Motor.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.LightSocket.get())
			.define('w', ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.define('t', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.pattern("   ")
			.pattern("twt")
			.pattern("ttt")
			.unlockedBy("has_items", hasItems(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.Wrench.get(), "tools/wrench")
			.define('i', ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag())
			.define('g', ModMaterials.TIN.get(MaterialTypes.GEAR).getItemTag())
			.pattern(" i ")
			.pattern(" gi")
			.pattern("i  ")
			.unlockedBy("has_items", hasItems(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag(), ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.SolderingIron.get(), "tools/soldering_iron")
			.define('i', ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag())
			.define('l', ModItemTags.RUBBER)
			.pattern("ii ")
			.pattern(" i ")
			.pattern(" l ")
			.unlockedBy("has_items", hasItems( ModItemTags.RUBBER));
		// @formatter:on

		beginShapelessRecipe(ModItems.ElectringSolderingIron.get(), "tools/electric_soldering_iron")
				.requires(ModItems.SolderingIron.get()).requires(ModItems.BasicPortableBattery.get())
				.unlockedBy("has_battery", hasItems(ModItems.BasicPortableBattery.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.Thermometer.get(), "tools/thermometer")
			.define('g', Tags.Items.GLASS)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.pattern(" g ")
			.pattern(" g ")
			.pattern(" r ")
			.unlockedBy("has_items", hasItems(Tags.Items.GLASS, Tags.Items.DUSTS_REDSTONE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.CableNetworkAnalyzer.get(), "tools/cable_network_analyzer")
			.define('i', ModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag())
			.define('t', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.pattern("igi")
			.pattern("ttt")
			.pattern("trt")
			.unlockedBy("has_items", hasItems(Tags.Items.GLASS, Tags.Items.DUSTS_REDSTONE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.DigistoreWirelessTerminal.get(), "tools/digistore_wireless_terminal")
			.define('g', Tags.Items.GLASS_PANES)
			.define('e', Tags.Items.ENDER_PEARLS)
			.define('t', ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag())
			.define('b', ModItems.EnergizedPortableBattery.get())
			.define('p', ModItems.EnergizedProcessor.get())
			.pattern("ete")
			.pattern("tgt")
			.pattern("pbp")
			.unlockedBy("has_items", hasItems(Tags.Items.GLASS, Tags.Items.DUSTS_REDSTONE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModItems.Multimeter.get(), "tools/multimeter")
			.define('t', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.define('d', Tags.Items.DUSTS_REDSTONE)
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', Tags.Items.DYES_RED)
			.define('b', Tags.Items.DYES_BLACK)
			.pattern("ttt")
			.pattern("tgt")
			.pattern("rdb")
			.unlockedBy("has_items", hasItems(Tags.Items.GLASS_PANES, Tags.Items.DUSTS_REDSTONE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.RustyCauldron.get(), "machines/rusty_cauldron")
			.define('r', ModItems.RustyIronScrap.get())
			.pattern("r r")
			.pattern("r r")
			.pattern("rrr")
			.unlockedBy("has_items", hasItems(ModItems.RustyIronScrap.get()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.CleanCauldron.get(), "machines/clean_cauldron")
			.define('s', ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag())
			.define('r', ModBlocks.RustyCauldron.get())
			.pattern("s s")
			.pattern("srs")
			.pattern("sss")
			.unlockedBy("has_items", hasItems(RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag())));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.AlloyFurnace.get(), "machines/alloy_furnace")
		.define('i', Tags.Items.INGOTS_IRON)
		.define('b', Items.BRICK)
		.define('f', Blocks.FURNACE.asItem())
		.pattern("iii")
		.pattern("bfb")
		.pattern("bbb")
		.unlockedBy("has_items", hasItems(Blocks.FURNACE.asItem()));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.ExperienceHopper.get(), "machines/experience_hopper")
		.define('f', Items.IRON_BARS)
		.define('h', Blocks.HOPPER)
		.pattern("   ")
		.pattern(" f ")
		.pattern(" h ")
		.unlockedBy("has_items", hasItems(Blocks.HOPPER));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.DirectDropper.get(), "machines/direct_dropper")
		.define('s', Tags.Items.COBBLESTONE)
		.define('c', Tags.Items.CHESTS)
		.define('d', Blocks.DROPPER)
		.pattern("scs")
		.pattern("sds")
		.pattern("s s")
		.unlockedBy("has_items", hasItems(Blocks.DROPPER));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.AutomaticPlacer.get(), "machines/automatic_placer")
		.define('s', Tags.Items.COBBLESTONE)
		.define('r', Tags.Items.DUSTS_REDSTONE)
		.define('c', Tags.Items.CHESTS)
		.pattern("sss")
		.pattern("rcs")
		.pattern("sss")
		.unlockedBy("has_items", hasItems(Tags.Items.CHESTS, Tags.Items.DUSTS_REDSTONE));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.VacuumChest.get(),  "chests/vacuum" )
			.define('o', Tags.Items.OBSIDIAN)
			.define('e', Tags.Items.ENDER_PEARLS)
			.define('c', Tags.Items.CHESTS_WOODEN)
			.pattern("oeo")
			.pattern("ece")
			.pattern("oeo")
			.unlockedBy("has_items", hasItems(RecipeItem.of(Tags.Items.CHESTS_WOODEN)));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(ModBlocks.SolderingTable.get(),  "machines/soldering_table")
			.define('s', Tags.Items.STONE)
			.define('c', Blocks.CRAFTING_TABLE)
			.pattern("sss")
			.pattern("scs")
			.pattern("s s")
			.unlockedBy("has_items", hasItems(RecipeItem.of(Blocks.CRAFTING_TABLE)));
		// @formatter:on

//		// @formatter:off
//		beginShapedRecipe(ModBlocks.Rectifier.get(),  "machines/rectifier")
//			.define('s', Tags.Items.STONE)
//			.define('c', Blocks.CRAFTING_TABLE)
//			.define('m', ModBlocks.MachineBlockIndustrial.get())
//			.pattern("sss")
//			.pattern("sms")
//			.pattern("s s")
//			.unlockedBy("has_items", hasItems(RecipeItem.of(Blocks.CRAFTING_TABLE)));
//		// @formatter:on

		
		conveyor("basic", ModBlocks.ConveyorsStraight.get(StaticPowerTiers.BASIC).get(),
				ModBlocks.ConveyorsRampUp.get(StaticPowerTiers.BASIC).get(),
				ModBlocks.ConveyorsRampDown.get(StaticPowerTiers.BASIC).get(),
				ModBlocks.ConveyorsHopper.get(StaticPowerTiers.BASIC).get(),
				ModBlocks.ConveyorsFilteredHopper.get(StaticPowerTiers.BASIC).get(),
				ModBlocks.ConveyorsSupplier.get(StaticPowerTiers.BASIC).get(),
				ModBlocks.ConveyorsExtractor.get(StaticPowerTiers.BASIC).get(),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.BasicProcessor.get()));
		conveyor("advanced", ModBlocks.ConveyorsStraight.get(StaticPowerTiers.ADVANCED).get(),
				ModBlocks.ConveyorsRampUp.get(StaticPowerTiers.ADVANCED).get(),
				ModBlocks.ConveyorsRampDown.get(StaticPowerTiers.ADVANCED).get(),
				ModBlocks.ConveyorsHopper.get(StaticPowerTiers.ADVANCED).get(),
				ModBlocks.ConveyorsFilteredHopper.get(StaticPowerTiers.ADVANCED).get(),
				ModBlocks.ConveyorsSupplier.get(StaticPowerTiers.ADVANCED).get(),
				ModBlocks.ConveyorsExtractor.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()));
		conveyor("static", ModBlocks.ConveyorsStraight.get(StaticPowerTiers.STATIC).get(),
				ModBlocks.ConveyorsRampUp.get(StaticPowerTiers.STATIC).get(),
				ModBlocks.ConveyorsRampDown.get(StaticPowerTiers.STATIC).get(),
				ModBlocks.ConveyorsHopper.get(StaticPowerTiers.STATIC).get(),
				ModBlocks.ConveyorsFilteredHopper.get(StaticPowerTiers.STATIC).get(),
				ModBlocks.ConveyorsSupplier.get(StaticPowerTiers.STATIC).get(),
				ModBlocks.ConveyorsExtractor.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.StaticProcessor.get()));
		conveyor("energized", ModBlocks.ConveyorsStraight.get(StaticPowerTiers.ENERGIZED).get(),
				ModBlocks.ConveyorsRampUp.get(StaticPowerTiers.ENERGIZED).get(),
				ModBlocks.ConveyorsRampDown.get(StaticPowerTiers.ENERGIZED).get(),
				ModBlocks.ConveyorsHopper.get(StaticPowerTiers.ENERGIZED).get(),
				ModBlocks.ConveyorsFilteredHopper.get(StaticPowerTiers.ENERGIZED).get(),
				ModBlocks.ConveyorsSupplier.get(StaticPowerTiers.ENERGIZED).get(),
				ModBlocks.ConveyorsExtractor.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		conveyor("lumum", ModBlocks.ConveyorsStraight.get(StaticPowerTiers.LUMUM).get(),
				ModBlocks.ConveyorsRampUp.get(StaticPowerTiers.LUMUM).get(),
				ModBlocks.ConveyorsRampDown.get(StaticPowerTiers.LUMUM).get(),
				ModBlocks.ConveyorsHopper.get(StaticPowerTiers.LUMUM).get(),
				ModBlocks.ConveyorsFilteredHopper.get(StaticPowerTiers.LUMUM).get(),
				ModBlocks.ConveyorsSupplier.get(StaticPowerTiers.LUMUM).get(),
				ModBlocks.ConveyorsExtractor.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR).getItemTag()),
				RecipeItem.of(ModItems.LumumProcessor.get()));

		backpack("basic", ModItems.Backpack.get(), RecipeItem.of(Tags.Items.CHESTS_WOODEN));
		backpack("builder", ModItems.BuildersBackPack.get(), RecipeItem.of(Blocks.STONE_BRICKS));
		backpack("digger", ModItems.DiggersBackPack.get(), RecipeItem.of(Tags.Items.TOOLS_SHOVELS));
		backpack("miner", ModItems.MinersBackpack.get(), RecipeItem.of(Tags.Items.TOOLS_PICKAXES));
		backpack("engineer", ModItems.EngineersBackpack.get(), RecipeItem.of(ModItemTags.WRENCH));
		backpack("farmer", ModItems.FarmersBackpack.get(), RecipeItem.of(Tags.Items.TOOLS_HOES));
		backpack("hunter", ModItems.HuntersPack.get(), RecipeItem.of(Tags.Items.TOOLS_BOWS));
		backpack("lumberjack", ModItems.LumberjacksBackPack.get(), RecipeItem.of(Tags.Items.TOOLS_AXES));
		backpack("tool", ModItems.ToolsBackpack.get(), RecipeItem.of(Blocks.CRAFTING_TABLE));

		transformer("basic", ModBlocks.TransformerBasic.get(),
				RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE_COIL).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockBasic.get()));
		transformer("advanced", ModBlocks.MachineBlockIndustrial.get(),
				RecipeItem.of(ModMaterials.BRASS.get(MaterialTypes.WIRE_COIL).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockIndustrial.get()));
		transformer("static", ModBlocks.TransformerStatic.get(),
				RecipeItem.of(ModMaterials.STATIC_METAL.get(MaterialTypes.WIRE_COIL).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockIndustrial.get()));
		transformer("energized", ModBlocks.TransformerEnergized.get(),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.WIRE_COIL).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockIndustrial.get()));
		transformer("lumum", ModBlocks.TransformerLumum.get(),
				RecipeItem.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.WIRE_COIL).getItemTag()),
				RecipeItem.of(ModBlocks.MachineBlockIndustrial.get()));
	}

	protected SCShapedRecipeBuilder beginShapedRecipe(ItemLike result) {
		return beginShapedRecipe(result, 1, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SCShapedRecipeBuilder beginShapedRecipe(ItemLike result, int count) {
		return beginShapedRecipe(result, count, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SCShapedRecipeBuilder beginShapedRecipe(ItemLike result, String nameOverride) {
		return beginShapedRecipe(result, 1, nameOverride);
	}

	protected void portableBattery(String name, ItemLike output, RecipeItem sides, RecipeItem top, RecipeItem core) {
		// @formatter:off
		beginShapedRecipe(output, "tools/portable_battery/" + name)
			.define('p', sides)
			.define('w', top)
			.define('r', core)
			.pattern(" w ")
			.pattern("prp")
			.pattern("prp")
			.unlockedBy("has_items", hasItems(sides, top, core));
		// @formatter:on
	}

	protected void batteryPack(String name, ItemLike output, RecipeItem wire, RecipeItem battery,
			RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(output, "tools/portable_battery_pack/" + name)
			.define('w', wire)
			.define('b', battery)
			.define('p', processor)
			.pattern("wpw")
			.pattern("bbb")
			.pattern("www")
			.unlockedBy("has_items", hasItems(wire, battery, processor));
		// @formatter:on
	}

	protected void heatCable(String name, ItemLike output, RecipeItem material) {
		// @formatter:off
		beginShapedRecipe(output, "heat_pipes/" + name)
			.define('i', material)
			.pattern(" i ")
			.pattern(" i ")
			.pattern(" i ")
			.unlockedBy("has_items", hasItems(material));
		// @formatter:on
	}

	protected void itemTube(String name, ItemLike output, RecipeItem plate, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 6, "item_tubes/" + name + "/from_plate")
			.define('i', plate)
			.define('g', Tags.Items.GLASS_PANES)
			.pattern("igi")
			.pattern("igi")
			.pattern("igi")
			.unlockedBy("has_items", hasItems(plate));
		
		beginShapedRecipe(output, 6, "item_tubes/" + name + "/from_ingot")
			.define('i', ingot)
			.define('g', Tags.Items.GLASS_PANES)
			.pattern("igi")
			.pattern("igi")
			.pattern("igi")
			.unlockedBy("has_items", hasItems(ingot));
		// @formatter:on
	}

	protected void fluidPipe(String name, ItemLike output, RecipeItem plate, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 6, "fluid_pipes/" + name + "/regular_from_plate")
			.define('i', plate)
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', ModItemTags.RUBBER_SHEET)
			.pattern("igi")
			.pattern("rgr")
			.pattern("igi")
			.unlockedBy("has_items", hasItems(plate));
		
		beginShapedRecipe(output, 6, "fluid_pipes/" + name + "/regular_from_ingot")
			.define('i', ingot)
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', ModItemTags.RUBBER_SHEET)
			.pattern("igi")
			.pattern("rgr")
			.pattern("igi")
			.unlockedBy("has_items", hasItems(ingot));
		// @formatter:on
	}

	protected void capillaryFluidPipe(String name, ItemLike output, RecipeItem plate, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 6, "fluid_pipes/" + name + "/capillary_from_plate")
			.define('i', plate)
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', ModItemTags.RUBBER_SHEET)
			.pattern(" r ")
			.pattern("igi")
			.pattern(" r ")
			.unlockedBy("has_items", hasItems(plate));
		
		beginShapedRecipe(output, 6, "fluid_pipes/" + name + "/capillary_from_ingot")
			.define('i', ingot)
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', ModItemTags.RUBBER_SHEET)
			.pattern("iri")
			.pattern("g g")
			.pattern("iri")
			.unlockedBy("has_items", hasItems(ingot));
		// @formatter:on
	}

	protected void industrialFluidPipe(String name, ItemLike output, RecipeItem pipe) {
		// @formatter:off
		beginShapedRecipe(output, 4, "fluid_pipes/" + name + "/industrial")
			.define('p', pipe)
			.define('g', Tags.Items.GLASS)
			.define('r', ModItemTags.RUBBER_SHEET)
			.pattern("prp")
			.pattern("rgr")
			.pattern("prp")
			.unlockedBy("has_items", hasItems(pipe));
		// @formatter:on
	}

	protected void powerCable(String name, ItemLike output, RecipeItem plate, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 6, "power_cables/" + name + "/from_plate")
			.define('i', plate)
			.define('r', ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.pattern("iri")
			.pattern("iri")
			.pattern("iri")
			.unlockedBy("has_items", hasItems(plate));
		
		beginShapedRecipe(output, 6, "power_cables/" + name + "/from_ingot")
			.define('i', ingot)
			.define('r', ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.pattern("iri")
			.pattern("iri")
			.pattern("iri")
			.unlockedBy("has_items", hasItems(ingot));
		// @formatter:on
	}

	protected void industrialPowerCable(String name, ItemLike output, RecipeItem cable) {
		// @formatter:off
		beginShapedRecipe(output, 4, "power_cables/" + name + "/industrial")
			.define('p', cable)
			.define('b', ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.STORAGE_BLOCK).getItemTag())
			.define('r', ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.pattern("prp")
			.pattern("rbr")
			.pattern("prp")
			.unlockedBy("has_items", hasItems(cable));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	protected void coloredRedstoneCable(String name, ItemLike output, RecipeItem wool) {
		// @formatter:off
		beginShapedRecipe(output, 6, "redstone_cable/" + name)
			.define('i', ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.define('w', wool)
			.pattern("www")
			.pattern("iii")
			.pattern("www")
			.unlockedBy("has_items", hasItems(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on
	}

	protected void insulatedPowerCable(String name, ItemLike output, RecipeItem cable) {
		beginShapelessRecipe(output, "power_cables/" + name + "/insulated").requires(cable).requires(ModItemTags.RUBBER)
				.unlockedBy("has_items", hasItems(cable));
	}

	protected void extractorAttachment(String name, ItemLike output, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 2, "cable_attachments/extractor/" + name)
			.define('i', ingot)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('s', ModItems.Servo.get())
			.pattern("iri")
			.pattern("isi")
			.pattern(" i ")
			.unlockedBy("has_items", hasItems(ModItems.Servo.get()));
		// @formatter:on
	}

	protected void filterAttachment(String name, ItemLike output, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 2, "cable_attachments/filter/" + name)
			.define('i', ingot)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('b', Blocks.IRON_BARS)
			.define('p', Items.PAPER)
			.pattern("iri")
			.pattern("ipi")
			.pattern(" b ")
			.unlockedBy("has_items", hasItems(Items.PAPER));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	protected void retrieverAttachment(String name, ItemLike output, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 2, "cable_attachments/retriever/" + name)
			.define('i', ingot)
			.define('p', Tags.Items.ENDER_PEARLS)
			.define('s', ModItems.Servo.get())
			.pattern("ipi")
			.pattern("isi")
			.pattern(" i ")
			.unlockedBy("has_items", hasItems(Tags.Items.ENDER_PEARLS));
		// @formatter:on
	}

	protected void wireCoil(String name, ItemLike output, RecipeItem wire) {
		// @formatter:off
		beginShapedRecipe(output, 4, "wire/coil_" + name)
			.define('w', wire)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("www")
			.pattern("wsw")
			.pattern("www")
			.unlockedBy("has_items", hasItems(wire));
		// @formatter:on
	}

	protected void chainsawBlade(String name, ItemLike output, RecipeItem blade) {
		// @formatter:off
		beginShapedRecipe(output, "tools/parts/chainsaw_blade_" + name)
			.define('b', blade)
			.define('c', Items.CHAIN)
			.pattern("ccc")
			.pattern("cbc")
			.pattern("ccc")
			.unlockedBy("has_items", hasItems(RecipeItem.of(Items.CHAIN), blade));
		// @formatter:on
	}

	protected void chest(String name, ItemLike output, RecipeItem ingot, RecipeItem plate) {
		// @formatter:off
		beginShapedRecipe(output,  "tools/parts/chainsaw_blade_" + name + "_from_ingot" )
			.define('i', ingot)
			.define('c', Tags.Items.CHESTS_WOODEN)
			.pattern("iii")
			.pattern("ici")
			.pattern("iii")
			.unlockedBy("has_items", hasItems(RecipeItem.of(Tags.Items.CHESTS_WOODEN), ingot));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(output,  "chests/" + name + "_from_plate" )
			.define('i', plate)
			.define('c', Tags.Items.CHESTS_WOODEN)
			.pattern("iii")
			.pattern("ici")
			.pattern("iii")
			.unlockedBy("has_items", hasItems(RecipeItem.of(Tags.Items.CHESTS_WOODEN), plate));
		// @formatter:on
	}

	protected void fluidCapsule(String name, ItemLike output, RecipeItem plate, RecipeItem originaCapsule) {
		// @formatter:off
		beginShapedRecipe(output, "tools/fluid_capsule/" + name)
			.define('p', plate)
			.define('b', originaCapsule)
			.define('g', Tags.Items.GLASS)
			.pattern("pbp")
			.pattern("gpg")
			.pattern("pbp")
			.unlockedBy("has_items", hasItems(originaCapsule));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	protected void lightbulb(String name, ItemLike output, RecipeItem glass) {
		// @formatter:off
		beginShapedRecipe(output, "decorative/lighting/light_bulb_" + name)
			.define('g', glass)
			.define('w', ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.define('c', ModMaterials.COPPER.get(MaterialTypes.INGOT).getItemTag())
			.pattern("ggg")
			.pattern("gwg")
			.pattern(" c ")
			.unlockedBy("has_items", hasItems(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()));
		// @formatter:on
	}

	protected void poweredMagnet(String name, ItemLike output, RecipeItem ingot, RecipeItem battery) {
		// @formatter:off
		beginShapedRecipe(output, "tools/magnet/" + name)
			.define('i', ingot)
			.define('b', battery)
			.pattern("iii")
			.pattern("i i")
			.pattern("b b")
			.unlockedBy("has_items", hasItems(ingot, battery));
		// @formatter:on
	}

	protected void solarPanel(String name, ItemLike output, RecipeItem processor, RecipeItem silicon) {
		// @formatter:off
		beginShapedRecipe(output, "power/solar_panels/" + name)
			.define('i', processor)
			.define('p', ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag())
			.define('g', Tags.Items.GLASS)
			.define('s', silicon)
			.pattern("ggg")
			.pattern("sis")
			.pattern("ppp")
			.unlockedBy("has_items", hasItems(silicon));
		// @formatter:on
	}

	protected void turbineBlade(String name, ItemLike output, RecipeItem ingot, RecipeItem gearBox) {
		// @formatter:off
		beginShapedRecipe(output, "power/turbine_blades_" + name)
			.define('i', ingot)
			.define('g', gearBox)
			.pattern("i i")
			.pattern(" g ")
			.pattern("i i")
			.unlockedBy("has_items", hasItems(ingot, gearBox));
		// @formatter:on
	}

	protected void batteryBlock(String name, ItemLike output, RecipeItem plate, RecipeItem machineBlock,
			RecipeItem batteryItem) {
		// @formatter:off
		beginShapedRecipe(output, "power/battery_blocks/" + name)
			.define('p', plate)
			.define('m', machineBlock)
			.define('b', batteryItem)
			.pattern("bpb")
			.pattern("bmb")
			.pattern("bpb")
			.unlockedBy("has_items", hasItems(machineBlock, batteryItem));
		// @formatter:on
	}

	protected void tank(String name, ItemLike output, RecipeItem plate, RecipeItem capsule) {
		// @formatter:off
		beginShapedRecipe(output, "tanks/" + name)
			.define('p', plate)
			.define('c', capsule)
			.define('i', RecipeItem.of(ModItems.IOPort.get()))
			.pattern("pcp")
			.pattern("cic")
			.pattern("pcp")
			.unlockedBy("has_items", hasItems(plate, capsule));
		// @formatter:on
	}

	protected void tankUpgrade(String name, ItemLike output, RecipeItem plate, RecipeItem tank) {
		// @formatter:off
		beginShapedRecipe(output, "tanks/" + name + "_upgrade")
			.define('p', plate)
			.define('t', tank)
			.define('i', RecipeItem.of(ModItems.IOPort.get()))
			.pattern("ppp")
			.pattern("tit")
			.pattern("ppp")
			.unlockedBy("has_items", hasItems(plate, tank));
		// @formatter:on
	}

	protected void wireTerminal(String name, ItemLike output, RecipeItem base, RecipeItem wire, RecipeItem ingot) {
		// @formatter:off
		beginShapedRecipe(output, 2, "power/wire_terminals/" + name)
			.define('w', wire)
			.define('i', ingot)
			.define('b', base)
			.pattern("wiw")
			.pattern("wiw")
			.pattern("bbb")
			.unlockedBy("has_items", hasItems(ingot, wire, base));
		// @formatter:on
	}

	protected void transformerUpgrade(String name, ItemLike output, RecipeItem upgradePlate, RecipeItem wire,
			RecipeItem transformer) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/transformer/" + name)
			.define('w', wire)
			.define('p', upgradePlate)
			.define('t', transformer)
			.pattern(" w ")
			.pattern("wtw")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void chainsaw(String name, ItemLike output, RecipeItem plate, RecipeItem gear, RecipeItem battery,
			RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(output, "tools/chainsaw/" + name)
			.define('i', plate)
			.define('g', gear)
			.define('b', battery)
			.define('p', processor)
			.define('m', ModItems.Motor.get())
			.define('l', Items.LEVER)
			.pattern("ipg")
			.pattern("bmb")
			.pattern("il ")
			.unlockedBy("has_items", hasItems(ModItems.Motor.get()));
		// @formatter:on
	}

	protected void miningDrill(String name, ItemLike output, RecipeItem plate, RecipeItem gear, RecipeItem battery,
			RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(output, "tools/mining_drill/" + name)
			.define('i', plate)
			.define('g', gear)
			.define('b', battery)
			.define('p', processor)
			.define('m', ModItems.Motor.get())
			.define('l', Items.LEVER)
			.pattern("ilg")
			.pattern("bmb")
			.pattern("ipi")
			.unlockedBy("has_items", hasItems(ModItems.Motor.get()));
		// @formatter:on
	}

	protected void hammer(String name, ItemLike output, RecipeItem material) {
		// @formatter:off
		beginShapedRecipe(output, "tools/hammer/" + name)
			.define('i', material)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("iii")
			.pattern("isi")
			.pattern(" s ")
			.unlockedBy("has_items", hasItems(material));
		// @formatter:on
	}

	protected void wireCutter(String name, ItemLike output, RecipeItem material, RecipeItem gear) {
		// @formatter:off
		beginShapedRecipe(output, "tools/wire_cutter/" + name)
			.define('i', material)
			.define('g', gear)
			.define('r', ModItemTags.RUBBER)
			.pattern("i i")
			.pattern(" g ")
			.pattern("r r")
			.unlockedBy("has_items", hasItems(gear));
		// @formatter:on
	}

	protected void saw(String name, ItemLike output, RecipeItem material) {
		// @formatter:off
		beginShapedRecipe(output, "tools/saw/" + name)
			.define('i', material)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("  s")
			.pattern(" si")
			.pattern("si ")
			.unlockedBy("has_items", hasItems(material));
		// @formatter:on
	}

	protected void drillBit(String name, ItemLike output, RecipeItem ingot, RecipeItem plate) {
		// @formatter:off
		beginShapedRecipe(output, "tools/parts/drill_bit_" + name)
			.define('i', ingot)
			.define('p', plate)
			.pattern("iii")
			.pattern("pip")
			.pattern(" i ")
			.unlockedBy("has_items", hasItems(ingot, plate));
		// @formatter:on
	}

	protected void farmland(String name, ItemLike output, RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(output, "farmland/" + name)
			.define('d', ItemTags.DIRT)
			.define('p', processor)
			.pattern("ddd")
			.pattern("dpd")
			.pattern("ddd")
			.unlockedBy("has_items", hasItems(processor));
		// @formatter:on
	}

	protected void bed(String name, ItemLike output, RecipeItem wool) {
		// @formatter:off
		beginShapedRecipe(output, "decorative/furniture/bed/" + name)
			.define('b', ModItems.BedFrame.get())
			.define('p', Blocks.WHITE_WOOL)
			.define('w', wool)
			.pattern("   ")
			.pattern("wwp")
			.pattern(" b ")
			.unlockedBy("has_items", hasItems(ModItems.BedFrame.get()));
		// @formatter:on
	}

	protected void digistoreCard(String name, ItemLike output, RecipeItem material, RecipeItem card,
			RecipeItem previousDigistoreCard) {
		// @formatter:off
		beginShapedRecipe(output, "digistore/card/" + name)
			.define('t', material)
			.define('c', previousDigistoreCard)
			.define('d', card)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.pattern("trt")
			.pattern("tct")
			.pattern("dtd")
			.unlockedBy("has_card", hasItems(card))
			.unlockedBy("has_previous_card", hasItems(previousDigistoreCard));
		// @formatter:on
	}

	protected void digistoreStackedCard(String name, ItemLike output, RecipeItem material, RecipeItem card,
			RecipeItem previousDigistoreCard) {
		// @formatter:off
		beginShapedRecipe(output, "digistore/stacked_card/" + name)
			.define('t', material)
			.define('c', previousDigistoreCard)
			.define('d', card)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('e', Tags.Items.CHESTS_WOODEN)
			.pattern("trt")
			.pattern("ece")
			.pattern("dtd")
			.unlockedBy("has_card", hasItems(card))
			.unlockedBy("has_previous_card", hasItems(previousDigistoreCard));
		// @formatter:on
	}

	protected void digistoreMonoCard(String name, ItemLike output, RecipeItem material, RecipeItem card,
			RecipeItem previousDigistoreCard) {
		// @formatter:off
		beginShapedRecipe(output, "digistore/mono_card/" + name)
			.define('t', material)
			.define('c', previousDigistoreCard)
			.define('d', card)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.pattern("trt")
			.pattern("tct")
			.pattern("tdt")
			.unlockedBy("has_card", hasItems(card))
			.unlockedBy("has_previous_card", hasItems(previousDigistoreCard));
		// @formatter:on
	}

	protected void pie(String name, ItemLike output, RecipeItem fruit) {
		beginShapelessRecipe(output, "food/pie_" + name).requires(fruit).requires(Items.EGG).requires(Items.SUGAR)
				.requires(fruit).unlockedBy("has_items", hasItems(fruit));
	}

	protected void filter(String name, ItemLike output, RecipeItem processor) {
		beginShapelessRecipe(output, "filter/" + name).requires(Items.PAPER).requires(processor).unlockedBy("has_items",
				hasItems(processor));
	}

	protected void upgradePlate(String name, ItemLike output, RecipeItem plate, RecipeItem processor) {
		TagKey<Item> ironPlate = ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag();
		// @formatter:off
		beginShapedRecipe(output, "upgrades/base_plate/" + name)
			.define('i', RecipeItem.of(ironPlate))
			.define('r', processor)
			.define('p', plate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(plate, processor));
		// @formatter:on
	}

	protected void tankUpgrade(String name, ItemLike output, RecipeItem upgradePlate) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/tank/" + name)
			.define('i', RecipeItem.of(Tags.Items.INGOTS_COPPER))
			.define('r', Items.BUCKET)
			.define('p', upgradePlate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void speedUpgrade(String name, ItemLike output, RecipeItem upgradePlate) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/speed/" + name)
			.define('i', RecipeItem.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()))
			.define('r', Tags.Items.DUSTS_GLOWSTONE)
			.define('p', upgradePlate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void rangeUpgrade(String name, ItemLike output, RecipeItem upgradePlate, RecipeItem gem) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/range/" + name)
			.define('i', Items.PISTON)
			.define('r', gem)
			.define('p', upgradePlate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void powerUpgrade(String name, ItemLike output, RecipeItem upgradePlate, RecipeItem redstoneAlloy) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/power/" + name)
			.define('i', Tags.Items.INGOTS_GOLD)
			.define('r', redstoneAlloy)
			.define('p', upgradePlate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void outputUpgrade(String name, ItemLike output, RecipeItem upgradePlate, RecipeItem lapis) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/output/" + name)
			.define('i', ModMaterials.IRON.get(MaterialTypes.GEAR).getItemTag())
			.define('r', lapis)
			.define('p', upgradePlate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void heatCapacityUpgrade(String name, ItemLike output, RecipeItem upgradePlate, RecipeItem heatsink) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/heat_capacity/" + name)
			.define('i', ModMaterials.LEAD.get(MaterialTypes.PLATE).getItemTag())
			.define('r', heatsink)
			.define('p', upgradePlate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void heatUpgrade(String name, ItemLike output, RecipeItem upgradePlate, RecipeItem headCapacityUpgrade) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/heat/" + name)
			.define('i', Items.PACKED_ICE)
			.define('r', headCapacityUpgrade)
			.define('p', upgradePlate)
			.pattern(" i ")
			.pattern(" r ")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate, headCapacityUpgrade));
		// @formatter:on
	}

	protected void centrifugeUpgrade(String name, ItemLike output, RecipeItem upgradePlate, RecipeItem gear) {
		// @formatter:off
		beginShapedRecipe(output, "upgrades/centrifuge/" + name)
			.define('i', ModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.define('r', gear)
			.define('p', upgradePlate)
			.define('m', ModItems.Motor.get())
			.pattern(" i ")
			.pattern("mrm")
			.pattern(" p ")
			.unlockedBy("has_items", hasItems(upgradePlate));
		// @formatter:on
	}

	protected void heatsink(String name, ItemLike output, RecipeItem wire) {
		// @formatter:off
		beginShapedRecipe(output, "heatsinks/" + name)
			.define('i', ModMaterials.IRON.get(MaterialTypes.STORAGE_BLOCK).getItemTag())
			.define('c', wire)
			.pattern("ccc")
			.pattern("cic")
			.pattern("ccc")
			.unlockedBy("has_items", hasItems(wire));
		// @formatter:on
	}

	protected void pump(String name, ItemLike output, RecipeItem machineBlock, RecipeItem cable) {
		// @formatter:off
		beginShapedRecipe(output, "pumps/" + name)
			.define('m', machineBlock)
			.define('c', cable)
			.define('p', RecipeItem.of(ModBlocks.PumpTube.get()))
			.define('t', RecipeItem.of(ModItems.Motor.get()))
			.define('g', RecipeItem.of(Tags.Items.GLASS))
			.pattern("gtg")
			.pattern("cpc")
			.pattern("gmg")
			.unlockedBy("has_items", hasItems(Items.PISTON));
		// @formatter:on
	}

	protected void machineBlock(String name, ItemLike output, RecipeItem plate, RecipeItem ingot,
			RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(output, "machines/machine_blocks/" + name + "_from_plate")
			.define('t', plate)
			.define('p', processor)
			.define('i', RecipeItem.of(ModItems.IOPort.get()))
			.pattern("ttt")
			.pattern("tit")
			.pattern("tpt")
			.unlockedBy("has_items", hasItems(processor));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(output, "machines/machine_blocks/" + name + "_from_ingot")
			.define('t', ingot)
			.define('p', processor)
			.define('i', RecipeItem.of(ModItems.IOPort.get()))
			.pattern("ttt")
			.pattern("tit")
			.pattern("tpt")
			.unlockedBy("has_items", hasItems(processor));
		// @formatter:on
	}

	protected void resistor(String name, ItemLike output, RecipeItem first, RecipeItem second, RecipeItem third) {
		// @formatter:off
		beginShapedRecipe(output, "power/resistors/" + name)
			.define('f', first)
			.define('s', second)
			.define('t', third)
			.define('w', RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()))
			.define('c', RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE_COIL).getItemTag()))
			.pattern("fst")
			.pattern("wcw")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())));
		// @formatter:on
	}

	protected void circuitBreaker(String name, ItemLike output, RecipeItem plate) {
		// @formatter:off
		beginShapedRecipe(output, "power/circuit_breakers/" + name)
			.define('l', Items.LEVER)
			.define('w', RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()))
			.define('p', RecipeItem.of(ModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()))
			.define('q', RecipeItem.of(ModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()))
			.pattern(" l ")
			.pattern("pwp")
			.pattern("pqp")
			.unlockedBy("has_items", hasItems(plate));
		// @formatter:on
	}

	protected void conveyor(String name, ItemLike straight, ItemLike up, ItemLike down, ItemLike hopper,
			ItemLike filteredHopper, ItemLike supplier, ItemLike extractor, RecipeItem plate, RecipeItem gear,
			RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(straight, "conveyors/straight/" + name)
			.define('r', ModItems.RubberSheet.get())
			.define('p', plate)
			.define('g', gear)
			.pattern("   ")
			.pattern("rrr")
			.pattern("pgp")
			.unlockedBy("has_items", hasItems(RecipeItem.of(ModItems.RubberSheet.get()), plate));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(up, "conveyors/ramp_up/" + name)
			.define('r', ModItems.RubberSheet.get())
			.define('p', plate)
			.define('g', gear)
			.pattern("  r")
			.pattern(" rp")
			.pattern("rpg")
			.unlockedBy("has_items", hasItems(RecipeItem.of(ModItems.RubberSheet.get()), plate));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(down, "conveyors/ramp_down/" + name)
			.define('r', ModItems.RubberSheet.get())
			.define('p', plate)
			.define('g', gear)
			.pattern("r  ")
			.pattern("pr ")
			.pattern("gpr")
			.unlockedBy("has_items", hasItems(RecipeItem.of(ModItems.RubberSheet.get()), plate));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(hopper, "conveyors/hopper/" + name)
			.define('r', ModItems.RubberSheet.get())
			.define('p', plate)
			.define('h', Items.HOPPER)
			.pattern("   ")
			.pattern("rrr")
			.pattern("php")
			.unlockedBy("has_items", hasItems(RecipeItem.of(ModItems.RubberSheet.get()), plate));
		// @formatter:on

		// @formatter:on
		beginShapelessRecipe(filteredHopper, 1).requires(hopper).requires(Items.IRON_BARS).unlockedBy("has_hopper",
				hasItems(hopper));

		// @formatter:off
		beginShapedRecipe(supplier, "conveyors/supplier/" + name)
			.define('r', ModItems.RubberSheet.get())
			.define('p', plate)
			.define('i', ModItems.IOPort.get())
			.define('g', gear)
			.define('c', processor)
			.pattern("p  ")
			.pattern("irr")
			.pattern("cgp")
			.unlockedBy("has_items", hasItems(RecipeItem.of(straight), plate));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(extractor, "conveyors/extractor/" + name)
			.define('r', ModItems.RubberSheet.get())
			.define('p', plate)
			.define('i', ModItems.IOPort.get())
			.define('g', gear)
			.define('c', processor)
			.pattern("  p")
			.pattern("rri")
			.pattern("pgc")
			.unlockedBy("has_items", hasItems(RecipeItem.of(straight), plate));
		// @formatter:on
	}

	protected void backpack(String name, ItemLike output, RecipeItem type) {
		// @formatter:off
		beginShapedRecipe(output, "backpacks/" + name)
			.define('w', ItemTags.WOOL)
			.define('s', Tags.Items.STRING)
			.define('g', Tags.Items.NUGGETS_GOLD)
			.define('t', type)
			.pattern("www")
			.pattern("tgt")
			.pattern("wsw")
			.unlockedBy("has_items", hasItems(RecipeItem.of(ItemTags.WOOL), type));
		// @formatter:on
	}

	protected void transformer(String name, ItemLike output, RecipeItem coil, RecipeItem machineBlock) {
		// @formatter:off
		beginShapedRecipe(output, "transformer/" + name)
			.define('c', coil)
			.define('m', machineBlock)
			.define('i', ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag())
			.pattern("c c")
			.pattern("cmc")
			.pattern("iii")
			.unlockedBy("has_items", hasItems(coil));
		// @formatter:on
	}

	protected SCShapedRecipeBuilder beginShapedRecipe(ItemLike result, int count, String nameOverride) {
		SCShapedRecipeBuilder builder = SCShapedRecipeBuilder.shaped(result, count);
		String name = "shaped/" + nameOverride;
		addRecipe(name, builder);
		return builder;
	}

	protected SCShapelessRecipeBuilder beginShapelessRecipe(ItemLike result) {
		return beginShapelessRecipe(result, 1, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SCShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, int count) {
		return beginShapelessRecipe(result, count, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SCShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, String nameOverride) {
		return beginShapelessRecipe(result, 1, nameOverride);
	}

	protected SCShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, int count, String nameOverride) {
		SCShapelessRecipeBuilder builder = SCShapelessRecipeBuilder.shapeless(result, count);
		String name = "shapeless/" + nameOverride;
		addRecipe(name, builder);
		return builder;
	}

	protected TriggerInstance hasWireCutter() {
		return inventoryTrigger(ItemPredicate.Builder.item().of(ModItemTags.WIRE_CUTTER).build());
	}
}
