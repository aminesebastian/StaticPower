package theking530.staticpower.data.generators.recipes;

import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.Tiers;
import theking530.staticpower.data.Tiers.RedstoneCableTier;
import theking530.staticpower.data.generators.RecipeItem;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.generators.helpers.SPShapedRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPShapelessRecipeBuilder;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.NewModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class CraftingRecipeGenerator extends SPRecipeProvider<CraftingRecipe> {

	public CraftingRecipeGenerator(DataGenerator generator) {
		super("crafting", generator);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildRecipes() {

		beginShapelessRecipe(ModBlocks.StaticPlanks.get(), 4, "wood/static_planks").requires(ModBlocks.StaticLog.get()).unlockedBy("has_log", hasItems(ModBlocks.StaticLog.get()));
		beginShapelessRecipe(ModBlocks.EnergizedPlanks.get(), 4, "wood/energized_planks").requires(ModBlocks.EnergizedLog.get()).unlockedBy("has_log",
				hasItems(ModBlocks.EnergizedLog.get()));
		beginShapelessRecipe(ModBlocks.LumumPlanks.get(), 4, "wood/lumum_planks").requires(ModBlocks.LumumLog.get()).unlockedBy("has_log", hasItems(ModBlocks.LumumLog.get()));
		beginShapelessRecipe(ModBlocks.RubberTreePlanks.get(), 4, "wood/rubber_planks").requires(ModItemTags.RUBBER_WOOD_LOGS).unlockedBy("has_log",
				hasItems(ModItemTags.RUBBER_WOOD_LOGS));

		// @formatter:off
		beginShapedRecipe(ModBlocks.RubberTreeStrippedWood.get(), "wood/rubber_wood_stripped/")
			.define('w',ModBlocks.RubberTreeStrippedLog.get())
			.pattern("ww ")
			.pattern("ww ")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(ModBlocks.RubberTreeStrippedLog.get()));
		// @formatter:on

		for (MaterialBundle material : NewModMaterials.MATERIALS.values()) {
			if (material.hasGeneratedMaterial(MaterialTypes.STORAGE_BLOCK) && material.hasGeneratedMaterial(MaterialTypes.INGOT)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.STORAGE_BLOCK).get(), "storage_blocks/from_ingots/" + material.getName())
					.define('i', material.get(MaterialTypes.INGOT).getItemTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_ingot", hasItems(material.get(MaterialTypes.INGOT).getItemTag()));
				// @formatter:on

				beginShapelessRecipe(material.get(MaterialTypes.INGOT).get(), 9, "ingots/from_block/" + material.getName())
						.requires(material.get(MaterialTypes.STORAGE_BLOCK).getItemTag())
						.unlockedBy("has__block", hasItems(material.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));
			}

			if (material.hasGeneratedMaterial(MaterialTypes.RAW_STOARGE_BLOCK) && material.hasGeneratedMaterial(MaterialTypes.RAW_MATERIAL)) {
				// @formatter:off
				beginShapedRecipe(material.get(MaterialTypes.RAW_STOARGE_BLOCK).get(), "storage_blocks/from_raw_materials/" + material.getName())
					.define('i', material.get(MaterialTypes.RAW_MATERIAL).getItemTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_raw_material", hasItems(material.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
				// @formatter:on

				beginShapelessRecipe(material.get(MaterialTypes.RAW_MATERIAL).get(), 9, "raw_materials/from_block/" + material.getName())
						.requires(material.get(MaterialTypes.RAW_STOARGE_BLOCK).getItemTag())
						.unlockedBy("has_raw_block", hasItems(material.get(MaterialTypes.RAW_STOARGE_BLOCK).getItemTag()));
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

				beginShapelessRecipe(material.get(MaterialTypes.NUGGET).get(), 9, "nuggets/from_ingot/" + material.getName())
						.requires(material.get(MaterialTypes.INGOT).getItemTag()).unlockedBy("has_ingot", hasItems(material.get(MaterialTypes.INGOT).getItemTag()));
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
				beginShapelessRecipe(material.get(MaterialTypes.WIRE).get(), 3, "wire/from_ingot/" + material.getName()).requires(material.get(MaterialTypes.INGOT).getItemTag())
						.requires(ModItemTags.WIRE_CUTTER).unlockedBy("has_ingot", hasItems(material.get(MaterialTypes.INGOT).getItemTag()))
						.unlockedBy("has_wire_cutter", hasWireCutter());

				if (material.has(MaterialTypes.PLATE)) {
					beginShapelessRecipe(material.get(MaterialTypes.WIRE).get(), 3, "wire/from_plate/" + material.getName())
							.requires(material.get(MaterialTypes.PLATE).getItemTag()).requires(ModItemTags.WIRE_CUTTER)
							.unlockedBy("has_plate", hasItems(material.get(MaterialTypes.INGOT).getItemTag())).unlockedBy("has_wire_cutter", hasWireCutter());
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
				wireCoil(material.getName(), material.get(MaterialTypes.WIRE_COIL).get(), RecipeItem.of(material.get(MaterialTypes.WIRE).getItemTag()));
			}

			if (material.has(MaterialTypes.INSULATED_WIRE_COIL)) {
				wireCoil("insulted_" + material.getName(), material.get(MaterialTypes.INSULATED_WIRE_COIL).get(),
						RecipeItem.of(material.get(MaterialTypes.INSULATED_WIRE).getItemTag()));
			}
		}

		// @formatter:off
		beginShapedRecipe(ModItems.WireCoilDigistore.get(), 4, "wire/coil_digistore")
			.define('i', NewModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.define('s', Tags.Items.RODS_WOODEN)
			.define('w', ItemTags.WOOL)
			.pattern("wiw")
			.pattern("isi")
			.pattern("wiw")
			.unlockedBy("has_items", hasItems(Tags.Items.INGOTS_COPPER));
		// @formatter:on

		beginShapelessRecipe(NewModMaterials.COPPER.get(MaterialTypes.DUST).get(), 2, "dusts/brass_from_dusts")
				.requires(NewModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag()).requires(NewModMaterials.ZINC.get(MaterialTypes.DUST).getItemTag())
				.unlockedBy("has_brass_dust_components",
						hasItems(NewModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag(), NewModMaterials.ZINC.get(MaterialTypes.DUST).getItemTag()));

		beginShapelessRecipe(NewModMaterials.COPPER.get(MaterialTypes.DUST).get(), 4, "dusts/bronze_from_dusts")
				.requires(NewModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag()).requires(NewModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag())
				.requires(NewModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag()).requires(NewModMaterials.TIN.get(MaterialTypes.DUST).getItemTag())
				.unlockedBy("has_brass_dust_components",
						hasItems(NewModMaterials.COPPER.get(MaterialTypes.DUST).getItemTag(), NewModMaterials.TIN.get(MaterialTypes.DUST).getItemTag()));

		beginShapelessRecipe(NewModMaterials.INERT_INFUSION.get(MaterialTypes.DUST).get(), 3, "dusts/inert_infusion_from_dusts")
				.requires(NewModMaterials.IRON.get(MaterialTypes.DUST).getItemTag()).requires(NewModMaterials.GOLD.get(MaterialTypes.DUST).getItemTag())
				.requires(NewModMaterials.PLATINUM.get(MaterialTypes.DUST).getItemTag())
				.unlockedBy("has_inert_infusion_dust_components", hasItems(NewModMaterials.IRON.get(MaterialTypes.DUST).getItemTag(),
						NewModMaterials.GOLD.get(MaterialTypes.DUST).getItemTag(), NewModMaterials.PLATINUM.get(MaterialTypes.DUST).getItemTag()));

		beginShapelessRecipe(NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.DUST).get(), 2, "dusts/redstone_alloy_from_dusts")
				.requires(NewModMaterials.SILVER.get(MaterialTypes.DUST).getItemTag()).requires(Items.REDSTONE)
				.unlockedBy("has_redston_alloy_dust_components", hasItems(NewModMaterials.SILVER.get(MaterialTypes.DUST).getItemTag(), Tags.Items.DUSTS_REDSTONE));

		portableBattery("basic", ModItems.BasicPortableBattery.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.COPPER.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		portableBattery("advanced", ModItems.AdvancedPortableBattery.get(), RecipeItem.of(NewModMaterials.GOLD.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		portableBattery("static", ModItems.StaticPortableBattery.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.SILVER.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItems.CrystalStatic.get()));
		portableBattery("energized", ModItems.EnergizedPortableBattery.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.GOLD.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItems.CrystalEnergized.get()));
		portableBattery("lumum", ModItems.LumumPortableBattery.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.PLATINUM.get(MaterialTypes.NUGGET).getItemTag()), RecipeItem.of(ModItems.CrystalLumum.get()));

		batteryPack("basic", ModItems.BasicBatteryPack.get(), RecipeItem.of(NewModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()), RecipeItem.of(ModItems.BasicProcessor.get()));
		batteryPack("advanced", ModItems.AdvancedBatteryPack.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		batteryPack("static", ModItems.StaticBatteryPack.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()), RecipeItem.of(ModItems.StaticProcessor.get()));
		batteryPack("energized", ModItems.EnergizedBatteryPack.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		batteryPack("lumum", ModItems.LumumBatteryPack.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.WIRE).getItemTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()), RecipeItem.of(ModItems.LumumProcessor.get()));

		heatCable("aluminum", ModBlocks.HeatCables.get(StaticPowerTiers.ALUMINUM).get(), RecipeItem.of(NewModMaterials.ALUMINUM.get(MaterialTypes.INGOT).getItemTag()));
		heatCable("copper", ModBlocks.HeatCables.get(StaticPowerTiers.COPPER).get(), RecipeItem.of(NewModMaterials.COPPER.get(MaterialTypes.INGOT).getItemTag()));
		heatCable("gold", ModBlocks.HeatCables.get(StaticPowerTiers.GOLD).get(), RecipeItem.of(NewModMaterials.GOLD.get(MaterialTypes.INGOT).getItemTag()));

		// @formatter:off
		beginShapedRecipe(ModBlocks.ScaffoldCable.get(), 8, "scaffold_cable")
			.define('i', Tags.Items.INGOTS_IRON)
			.define('t', NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.pattern(" t ")
			.pattern(" i ")
			.pattern(" t ")
			.unlockedBy("has_items", hasItems(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
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

		itemTube("basic", ModBlocks.ItemCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("advanced", ModBlocks.ItemCables.get(StaticPowerTiers.ADVANCED).get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("static", ModBlocks.ItemCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("energized", ModBlocks.ItemCables.get(StaticPowerTiers.ENERGIZED).get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		itemTube("lumum", ModBlocks.ItemCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		fluidPipe("basic", ModBlocks.FluidCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("advanced", ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("static", ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("energized", ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		fluidPipe("lumum", ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		capillaryFluidPipe("basic", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("advanced", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("static", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.STATIC).get(),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("energized", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		capillaryFluidPipe("lumum", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.LUMUM).get(),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		industrialFluidPipe("basic", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));
		industrialFluidPipe("advanced", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get()));
		industrialFluidPipe("static", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get()));
		industrialFluidPipe("energized", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get()));
		industrialFluidPipe("lumum", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));

		powerCable("basic", ModBlocks.PowerCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("advanced", ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("static", ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("energized", ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		powerCable("lumum", ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		insulatedPowerCable("basic", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.BASIC).get()));
		insulatedPowerCable("advanced", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get()));
		insulatedPowerCable("static", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get()));
		insulatedPowerCable("energized", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get()));
		insulatedPowerCable("lumum", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));

		industrialPowerCable("basic", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));
		industrialPowerCable("advanced", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get()));
		industrialPowerCable("static", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get()));
		industrialPowerCable("energized", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get()));
		industrialPowerCable("lumum", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));

		// @formatter:off
		beginShapedRecipe(ModBlocks.BasicRedstoneCableNaked.get(), 6, "redstone_cable/naked")
			.define('i', NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.pattern("   ")
			.pattern("iii")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on

		for (RedstoneCableTier tier : Tiers.getRedstone()) {
			coloredRedstoneCable(tier.color().getName(), ModBlocks.RedstoneCables.get(tier.location()).get(),
					RecipeItem.of(ModItemTags.createForgeTag("wool/" + tier.color().getName())));
		}

		extractorAttachment("basic", ModItems.BasicExtractorAttachment.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("advanced", ModItems.AdvancedExtractorAttachment.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("static", ModItems.StaticExtractorAttachment.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("energized", ModItems.EnergizedExtractorAttachment.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		extractorAttachment("lumum", ModItems.LumumExtractorAttachment.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		filterAttachment("basic", ModItems.BasicFilterAttachment.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("advanced", ModItems.AdvancedFilterAttachment.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("static", ModItems.StaticFilterAttachment.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("energized", ModItems.EnergizedFilterAttachment.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		filterAttachment("lumum", ModItems.LumumFilterAttachment.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		retrieverAttachment("basic", ModItems.BasicRetrieverAttachment.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("advanced", ModItems.AdvancedRetrieverAttachment.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("static", ModItems.StaticRetrieverAttachment.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("energized", ModItems.EnergizedRetrieverAttachment.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()));
		retrieverAttachment("lumum", ModItems.LumumRetrieverAttachment.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()));

		chainsawBlade("iron", ModItems.IronChainsawBlade.get(), RecipeItem.of(ModItems.IronBlade.get()));
		chainsawBlade("bronze", ModItems.BronzeChainsawBlade.get(), RecipeItem.of(ModItems.BronzeBlade.get()));
		chainsawBlade("advanced", ModItems.AdvancedChainsawBlade.get(), RecipeItem.of(ModItems.AdvancedBlade.get()));
		chainsawBlade("static", ModItems.StaticChainsawBlade.get(), RecipeItem.of(ModItems.StaticBlade.get()));
		chainsawBlade("energized", ModItems.EnergizedChainsawBlade.get(), RecipeItem.of(ModItems.EnergizedBlade.get()));
		chainsawBlade("lumum", ModItems.LumumChainsawBlade.get(), RecipeItem.of(ModItems.LumumBlade.get()));
		chainsawBlade("tungsten", ModItems.TungstenChainsawBlade.get(), RecipeItem.of(ModItems.TungstenBlade.get()));

		chest("basic", ModBlocks.BasicChest.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()));
		chest("advanced", ModBlocks.AdvancedChest.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()));
		chest("static", ModBlocks.StaticChest.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()));
		chest("energized", ModBlocks.EnergizedChest.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()));
		chest("lumum", ModBlocks.LumumChest.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()));

		fluidCapsule("iron", ModItems.IronFluidCapsule.get(), RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag()), RecipeItem.of(Items.BUCKET));
		fluidCapsule("basic", ModItems.BasicFluidCapsule.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.IronFluidCapsule.get()));
		fluidCapsule("advanced", ModItems.AdvancedFluidCapsule.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicFluidCapsule.get()));
		fluidCapsule("static", ModItems.StaticFluidCapsule.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedFluidCapsule.get()));
		fluidCapsule("energized", ModItems.EnergizedFluidCapsule.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticFluidCapsule.get()));
		fluidCapsule("lumum", ModItems.LumumFluidCapsule.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedFluidCapsule.get()));

		pie("apple", ModItems.ApplePie.get(), RecipeItem.of(Items.APPLE));
		pie("static", ModItems.StaticPie.get(), RecipeItem.of(ModItems.StaticFruit.get()));
		pie("energized", ModItems.EnergizedPie.get(), RecipeItem.of(ModItems.EnergizedFruit.get()));
		pie("lumum", ModItems.LumumPie.get(), RecipeItem.of(ModItems.LumumFruit.get()));

		for (MinecraftColor color : MinecraftColor.values()) {
			lightbulb(color.getName(), ModItems.Lightbulbs.get(color).get(), RecipeItem.of(ModItemTags.createForgeTag("glass_panes/" + color.getName())));
		}

		bed(MinecraftColor.WHITE.getName(), Blocks.WHITE_BED, RecipeItem.of(Blocks.WHITE_WOOL));
		bed(MinecraftColor.LIGHT_GRAY.getName(), Blocks.LIGHT_GRAY_BED, RecipeItem.of(Blocks.LIGHT_GRAY_WOOL));
		bed(MinecraftColor.GRAY.getName(), Blocks.GRAY_BED, RecipeItem.of(Blocks.GRAY_WOOL));
		bed(MinecraftColor.BLACK.getName(), Blocks.BLACK_BED, RecipeItem.of(Blocks.BLACK_WOOL));
		bed(MinecraftColor.BROWN.getName(), Blocks.BROWN_BED, RecipeItem.of(Blocks.BROWN_WOOL));
		bed(MinecraftColor.PINK.getName(), Blocks.PINK_BED, RecipeItem.of(Blocks.PINK_WOOL));
		bed(MinecraftColor.RED.getName(), Blocks.RED_BED, RecipeItem.of(Blocks.RED_WOOL));
		bed(MinecraftColor.ORANGE.getName(), Blocks.ORANGE_BED, RecipeItem.of(Blocks.ORANGE_WOOL));
		bed(MinecraftColor.YELLOW.getName(), Blocks.YELLOW_BED, RecipeItem.of(Blocks.YELLOW_WOOL));
		bed(MinecraftColor.LIME.getName(), Blocks.LIME_BED, RecipeItem.of(Blocks.LIME_WOOL));
		bed(MinecraftColor.GREEN.getName(), Blocks.GREEN_BED, RecipeItem.of(Blocks.GREEN_WOOL));
		bed(MinecraftColor.CYAN.getName(), Blocks.CYAN_BED, RecipeItem.of(Blocks.CYAN_WOOL));
		bed(MinecraftColor.LIGHT_BLUE.getName(), Blocks.LIGHT_BLUE_BED, RecipeItem.of(Blocks.LIGHT_BLUE_WOOL));
		bed(MinecraftColor.BLUE.getName(), Blocks.BLUE_BED, RecipeItem.of(Blocks.BLUE_WOOL));
		bed(MinecraftColor.PURPLE.getName(), Blocks.PURPLE_BED, RecipeItem.of(Blocks.PURPLE_WOOL));
		bed(MinecraftColor.MAGENTA.getName(), Blocks.MAGENTA_BED, RecipeItem.of(Blocks.MAGENTA_WOOL));

		poweredMagnet("basic", ModItems.BasicMagnet.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()));
		poweredMagnet("advanced", ModItems.AdvancedMagnet.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()));
		poweredMagnet("static", ModItems.StaticMagnet.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()));
		poweredMagnet("energized", ModItems.EnergizedMagnet.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()));
		poweredMagnet("lumum", ModItems.LumumMagnet.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.WeakMagnet.get(), "tools/magnet/weak")
			.define('t', NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag())
			.define('i', NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag())
			.pattern(" t ")
			.pattern("t t")
			.pattern("i i")
			.unlockedBy("has_items", hasItems(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag(), NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on

		solarPanel("basic", ModBlocks.SolarPanelBasic.get(), RecipeItem.of(ModItems.BasicProcessor.get()), RecipeItem.of(ModItems.Silicon.get()));
		solarPanel("advanced", ModBlocks.SolarPanelAdvanced.get(), RecipeItem.of(ModItems.AdvancedProcessor.get()), RecipeItem.of(ModItems.Silicon.get()));
		solarPanel("static", ModBlocks.SolarPanelStatic.get(), RecipeItem.of(ModItems.StaticProcessor.get()), RecipeItem.of(ModItems.StaticDopedSilicon.get()));
		solarPanel("energized", ModBlocks.SolarPanelEnergized.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()), RecipeItem.of(ModItems.EnergizedDopedSilicon.get()));
		solarPanel("lumum", ModBlocks.SolarPanelLumum.get(), RecipeItem.of(ModItems.LumumProcessor.get()), RecipeItem.of(ModItems.LumumDopedSilicon.get()));

		turbineBlade("basic", ModItems.BasicTurbineBlades.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("advanced", ModItems.AdvancedTurbineBlades.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("static", ModItems.StaticTurbineBlades.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("energized", ModItems.EnergizedTurbineBlades.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR_BOX).get()));
		turbineBlade("lumum", ModItems.LumumTurbineBlades.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR_BOX).get()));

		wireTerminal("low_voltage", ModBlocks.WireConnectorLV.get(), RecipeItem.of(Items.BRICK), RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("medium_voltage", ModBlocks.WireConnectorMV.get(), RecipeItem.of(ModItemTags.RUBBER),
				RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("high_voltage", ModBlocks.WireConnectorHV.get(), RecipeItem.of(NewModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("extreme_voltage", ModBlocks.WireConnectorEV.get(), RecipeItem.of(NewModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("bonkers_voltage", ModBlocks.WireConnectorBV.get(), RecipeItem.of(NewModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.WIRE).getItemTag()));
		wireTerminal("digistore", ModBlocks.WireConnectorDigistore.get(), RecipeItem.of(ItemTags.WOOL), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()));

		chainsaw("basic", ModItems.BasicChainsaw.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.BasicPortableBattery.get()),
				RecipeItem.of(ModItems.BasicProcessor.get()));
		chainsaw("advanced", ModItems.AdvancedChainsaw.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.AdvancedPortableBattery.get()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()));
		chainsaw("static", ModItems.StaticChainsaw.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.StaticPortableBattery.get()),
				RecipeItem.of(ModItems.StaticProcessor.get()));
		chainsaw("energized", ModItems.EnergizedChainsaw.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.EnergizedPortableBattery.get()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		chainsaw("lumum", ModItems.LumumChainsaw.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.LumumPortableBattery.get()),
				RecipeItem.of(ModItems.LumumProcessor.get()));

		miningDrill("basic", ModItems.BasicMiningDrill.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.BasicPortableBattery.get()),
				RecipeItem.of(ModItems.BasicProcessor.get()));
		miningDrill("advanced", ModItems.AdvancedMiningDrill.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.AdvancedPortableBattery.get()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()));
		miningDrill("static", ModItems.StaticMiningDrill.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.StaticPortableBattery.get()),
				RecipeItem.of(ModItems.StaticProcessor.get()));
		miningDrill("energized", ModItems.EnergizedMiningDrill.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.EnergizedPortableBattery.get()),
				RecipeItem.of(ModItems.EnergizedProcessor.get()));
		miningDrill("lumum", ModItems.LumumMiningDrill.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.GEAR).getItemTag()), RecipeItem.of(ModItems.LumumPortableBattery.get()),
				RecipeItem.of(ModItems.LumumProcessor.get()));

		hammer("iron", ModItems.IronMetalHammer.get(), RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()));
		hammer("zinc", ModItems.ZincMetalHammer.get(), RecipeItem.of(NewModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()));
		hammer("tin", ModItems.TinMetalHammer.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).getItemTag()));
		hammer("copper", ModItems.CopperMetalHammer.get(), RecipeItem.of(NewModMaterials.COPPER.get(MaterialTypes.INGOT).getItemTag()));
		hammer("bronze", ModItems.BronzeMetalHammer.get(), RecipeItem.of(NewModMaterials.BRONZE.get(MaterialTypes.INGOT).getItemTag()));
		hammer("tungsten", ModItems.TungstenMetalHammer.get(), RecipeItem.of(NewModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()));

		wireCutter("iron", ModItems.IronWireCutters.get(), RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.GEAR).getItemTag()));
		wireCutter("zinc", ModItems.ZincWireCutters.get(), RecipeItem.of(NewModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.ZINC.get(MaterialTypes.GEAR).getItemTag()));
		wireCutter("bronze", ModItems.BronzeWireCutters.get(), RecipeItem.of(NewModMaterials.BRONZE.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.BRONZE.get(MaterialTypes.GEAR).getItemTag()));
		wireCutter("tungsten", ModItems.TungstenWireCutters.get(), RecipeItem.of(NewModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.TUNGSTEN.get(MaterialTypes.GEAR).getItemTag()));

		saw("iron", ModItems.IronCoverSaw.get(), RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()));
		saw("ruby", ModItems.RubyCoverSaw.get(), RecipeItem.of(NewModMaterials.RUBY.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		saw("saphhire", ModItems.SapphireCoverSaw.get(), RecipeItem.of(NewModMaterials.SAPPHIRE.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		saw("diamond", ModItems.DiamondCoverSaw.get(), RecipeItem.of(Tags.Items.GEMS_DIAMOND));
		saw("tungsten", ModItems.TungstenCoverSaw.get(), RecipeItem.of(NewModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()));

		drillBit("iron", ModItems.IronDrillBit.get(), RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("advanced", ModItems.AdvancedDrillBit.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("bronze", ModItems.BronzeDrillBit.get(), RecipeItem.of(NewModMaterials.BRONZE.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.BRONZE.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("static", ModItems.StaticDrillBit.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("energized", ModItems.EnergizedDrillBit.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("lumum", ModItems.LumumDrillBit.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()));
		drillBit("tungsten", ModItems.TungstenDrillBit.get(), RecipeItem.of(NewModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getItemTag()),
				RecipeItem.of(NewModMaterials.TUNGSTEN.get(MaterialTypes.PLATE).getItemTag()));

		filter("basic", ModItems.BasicFilter.get(), RecipeItem.of(ModItems.BasicProcessor.get()));
		filter("advanced", ModItems.AdvancedFilter.get(), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		filter("static", ModItems.StaticFilter.get(), RecipeItem.of(ModItems.StaticProcessor.get()));
		filter("energized", ModItems.EnergizedFilter.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		filter("lumum", ModItems.LumumFilter.get(), RecipeItem.of(ModItems.LumumProcessor.get()));

		farmland("static", ModBlocks.StaticFarmland.get(), RecipeItem.of(ModItems.StaticProcessor.get()));
		farmland("energized", ModBlocks.EnergizedFarmland.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		farmland("lumum", ModBlocks.LumumFarmland.get(), RecipeItem.of(ModItems.LumumProcessor.get()));

		digistoreCard("basic", ModItems.BasicDigistoreCard.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()), RecipeItem.of(ModItems.BasicCard.get()),
				RecipeItem.of(ModItems.DigistoreCore.get()));
		digistoreStackedCard("basic", ModItems.BasicStackedDigistoreCard.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicCard.get()), RecipeItem.of(ModItems.DigistoreCore.get()));
		digistoreMonoCard("basic", ModItems.BasicSingularDigistoreCard.get(), RecipeItem.of(NewModMaterials.TIN.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.BasicCard.get()), RecipeItem.of(ModItems.DigistoreCore.get()));

		digistoreCard("advanced", ModItems.AdvancedDigistoreCard.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedCard.get()), RecipeItem.of(ModItems.BasicDigistoreCard.get()));
		digistoreStackedCard("advanced", ModItems.AdvancedStackedDigistoreCard.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedCard.get()), RecipeItem.of(ModItems.BasicStackedDigistoreCard.get()));
		digistoreMonoCard("advanced", ModItems.AdvancedSingularDigistoreCard.get(), RecipeItem.of(NewModMaterials.BRASS.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.AdvancedCard.get()), RecipeItem.of(ModItems.BasicSingularDigistoreCard.get()));

		digistoreCard("static", ModItems.StaticDigistoreCard.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticCard.get()), RecipeItem.of(ModItems.AdvancedDigistoreCard.get()));
		digistoreStackedCard("static", ModItems.StaticStackedDigistoreCard.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticCard.get()), RecipeItem.of(ModItems.AdvancedStackedDigistoreCard.get()));
		digistoreMonoCard("static", ModItems.StaticSingularDigistoreCard.get(), RecipeItem.of(NewModMaterials.STATIC_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticCard.get()), RecipeItem.of(ModItems.AdvancedSingularDigistoreCard.get()));

		digistoreCard("energized", ModItems.EnergizedDigistoreCard.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.EnergizedCard.get()), RecipeItem.of(ModItems.StaticDigistoreCard.get()));
		digistoreStackedCard("energized", ModItems.EnergizedStackedDigistoreCard.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticStackedDigistoreCard.get()), RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));
		digistoreMonoCard("energized", ModItems.EnergizedSingularDigistoreCard.get(), RecipeItem.of(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.StaticSingularDigistoreCard.get()), RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));

		digistoreCard("lumum", ModItems.LumumDigistoreCard.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumCard.get()), RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));
		digistoreStackedCard("lumum", ModItems.LumumStackedDigistoreCard.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumCard.get()), RecipeItem.of(ModItems.EnergizedStackedDigistoreCard.get()));
		digistoreMonoCard("lumum", ModItems.LumumSingularDigistoreCard.get(), RecipeItem.of(NewModMaterials.LUMUM_METAL.get(MaterialTypes.PLATE).getItemTag()),
				RecipeItem.of(ModItems.LumumCard.get()), RecipeItem.of(ModItems.EnergizedSingularDigistoreCard.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.PatternCard.get(), "digistore/pattern_card")
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('c', ModItems.DigistoreCore.get())
			.define('p', NewModMaterials.MAGNESIUM.get(MaterialTypes.INGOT).getItemTag())
			.pattern("ggg")
			.pattern("rcr")
			.pattern("ppp")
			.unlockedBy("has_digistore_core", hasItems(ModItems.DigistoreCore.get()));
		// @formatter:on
	}

	protected SPShapedRecipeBuilder beginShapedRecipe(ItemLike result) {
		return beginShapedRecipe(result, 1, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SPShapedRecipeBuilder beginShapedRecipe(ItemLike result, int count) {
		return beginShapedRecipe(result, count, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SPShapedRecipeBuilder beginShapedRecipe(ItemLike result, String nameOverride) {
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

	protected void batteryPack(String name, ItemLike output, RecipeItem wire, RecipeItem battery, RecipeItem processor) {
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
			.define('r', NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.pattern("iri")
			.pattern("iri")
			.pattern("iri")
			.unlockedBy("has_items", hasItems(plate));
		
		beginShapedRecipe(output, 6, "power_cables/" + name + "/from_ingot")
			.define('i', ingot)
			.define('r', NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
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
			.define('b', NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.STORAGE_BLOCK).getItemTag())
			.define('r', NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
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
			.define('i', NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag())
			.define('w', wool)
			.pattern("www")
			.pattern("iii")
			.pattern("www")
			.unlockedBy("has_items", hasItems(NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getItemTag()));
		// @formatter:on
	}

	protected void insulatedPowerCable(String name, ItemLike output, RecipeItem cable) {
		beginShapelessRecipe(output, "power_cables/" + name + "/insulated").requires(cable).requires(ModItemTags.RUBBER).unlockedBy("has_items", hasItems(cable));
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
			.define('w', NewModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag())
			.define('c', NewModMaterials.COPPER.get(MaterialTypes.INGOT).getItemTag())
			.pattern("ggg")
			.pattern("gwg")
			.pattern(" c ")
			.unlockedBy("has_items", hasItems(NewModMaterials.COPPER.get(MaterialTypes.WIRE).getItemTag()));
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
			.define('p', NewModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag())
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

	protected void chainsaw(String name, ItemLike output, RecipeItem plate, RecipeItem gear, RecipeItem battery, RecipeItem processor) {
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

	protected void miningDrill(String name, ItemLike output, RecipeItem plate, RecipeItem gear, RecipeItem battery, RecipeItem processor) {
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

	protected void digistoreCard(String name, ItemLike output, RecipeItem material, RecipeItem card, RecipeItem previousDigistoreCard) {
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

	protected void digistoreStackedCard(String name, ItemLike output, RecipeItem material, RecipeItem card, RecipeItem previousDigistoreCard) {
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

	protected void digistoreMonoCard(String name, ItemLike output, RecipeItem material, RecipeItem card, RecipeItem previousDigistoreCard) {
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
		beginShapelessRecipe(output, "food/pie_" + name).requires(fruit).requires(Items.EGG).requires(Items.SUGAR).requires(fruit).unlockedBy("has_items", hasItems(fruit));
	}

	protected void filter(String name, ItemLike output, RecipeItem processor) {
		beginShapelessRecipe(output, "filter/" + name).requires(Items.PAPER).requires(processor).unlockedBy("has_items", hasItems(processor));
	}

	protected SPShapedRecipeBuilder beginShapedRecipe(ItemLike result, int count, String nameOverride) {
		SPShapedRecipeBuilder builder = SPShapedRecipeBuilder.shaped(result, count);
		String name = "shaped/" + nameOverride;
		addRecipe(name, builder);
		return builder;
	}

	protected SPShapelessRecipeBuilder beginShapelessRecipe(ItemLike result) {
		return beginShapelessRecipe(result, 1, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SPShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, int count) {
		return beginShapelessRecipe(result, count, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected SPShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, String nameOverride) {
		return beginShapelessRecipe(result, 1, nameOverride);
	}

	protected SPShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, int count, String nameOverride) {
		SPShapelessRecipeBuilder builder = SPShapelessRecipeBuilder.shapeless(result, count);
		String name = "shapeless/" + nameOverride;
		addRecipe(name, builder);
		return builder;
	}

	protected TriggerInstance hasWireCutter() {
		return inventoryTrigger(ItemPredicate.Builder.item().of(ModItemTags.WIRE_CUTTER).build());
	}
}
