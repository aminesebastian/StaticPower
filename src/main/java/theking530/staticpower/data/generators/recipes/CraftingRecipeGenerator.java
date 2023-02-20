package theking530.staticpower.data.generators.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.MaterialBundle;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.Tiers;
import theking530.staticpower.data.Tiers.RedstoneCableTier;
import theking530.staticpower.data.generators.RecipeItem;
import theking530.staticpower.data.generators.helpers.SPShapedRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPShapelessRecipeBuilder;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class CraftingRecipeGenerator extends RecipeProvider implements IConditionBuilder {
	private Map<String, RecipeBuilder> builders;

	public CraftingRecipeGenerator(DataGenerator p_125973_) {
		super(p_125973_);
		builders = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {

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

		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (material.shouldGenerateStorageBlock() && material.shouldGenerateIngot()) {
				// @formatter:off
				beginShapedRecipe(material.getStorageBlock().get(), "storage_blocks/from_ingots/" + material.getName())
					.define('i', material.getIngotTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_" + material.getName() + "_smelted_material", hasItems(material.getIngotTag()));
				// @formatter:on

				beginShapelessRecipe(material.getIngot().get(), 9, "ingots/from_block/" + material.getName()).requires(material.getStorageBlockItemTag())
						.unlockedBy("has_" + material.getName() + "_block", hasItems(material.getStorageBlockItemTag()));
			}

			if (material.shouldGenerateRawStorageBlock() && material.shouldGenerateRawMaterial()) {
				// @formatter:off
				beginShapedRecipe(material.getRawMaterialStorageBlock().get(),  "storage_blocks/from_raw_materials/" + material.getName())
					.define('i', material.getRawMaterialTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_" + material.getName() + "_raw_material", hasItems(material.getRawMaterialTag()));
				// @formatter:on

				beginShapelessRecipe(material.getRawMaterial().get(), 9, "raw_materials/from_block/" + material.getName()).requires(material.getRawStorageBlockItemTag())
						.unlockedBy("has_" + material.getName() + "_raw_block", hasItems(material.getRawStorageBlockItemTag()));
			}

			if (material.shouldGenerateIngot() && material.shouldGenerateNugget()) {
				// @formatter:off
				beginShapedRecipe(material.getIngot().get(), "ingots/from_nuggets/" + material.getName())
					.define('i', material.getNuggetTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_" + material.getName() + "_nugget", hasItems(material.getNuggetTag()));
				// @formatter:on

				beginShapelessRecipe(material.getNugget().get(), 9, "nuggets/from_ingot/" + material.getName()).requires(material.getIngotTag())
						.unlockedBy("has_" + material.getName() + "_ingot", hasItems(material.getIngotTag()));
			}

			if (material.shouldGenerateGear()) {
				// @formatter:off
				beginShapedRecipe(material.getGear().get(), "gear/from_ingots/" + material.getName())
					.define('i', material.getIngotTag())
					.pattern(" i ")
					.pattern("i i")
					.pattern(" i ")
					.unlockedBy("has_" + material.getName() + "_ingot", hasItems(material.getIngotTag()));
				// @formatter:on

				if (material.shouldGenerateGearBox()) {
					// @formatter:off
					beginShapedRecipe(material.getGearBox().get(), "gear_box/" + material.getName())
						.define('i', material.getIngotTag())
						.define('g', material.getGearTag())
						.pattern("g  ")
						.pattern(" ig")
						.pattern("g  ")
						.unlockedBy("has_items", hasItems(material.getGearTag()));
					// @formatter:on
				}
			}

			if (material.shouldGenerateWire()) {
				beginShapelessRecipe(material.getWire().get(), 3, "wire/from_ingot/" + material.getName()).requires(material.getIngotTag()).requires(ModItemTags.WIRE_CUTTER)
						.unlockedBy("has_" + material.getName() + "_ingot", hasItems(material.getIngotTag())).unlockedBy("has_wire_cutter", hasWireCutter());

				if (material.shouldGeneratePlate()) {
					beginShapelessRecipe(material.getWire().get(), 3, "wire/from_plate/" + material.getName()).requires(material.getPlateTag()).requires(ModItemTags.WIRE_CUTTER)
							.unlockedBy("has_" + material.getName() + "_plate", hasItems(material.getPlateTag())).unlockedBy("has_wire_cutter", hasWireCutter());
				}
			}

			if (material.shouldGenerateInsulatedWire()) {
				// @formatter:off
				beginShapedRecipe(material.getInsulatedWire().get(), 3, "wire/insulated/" + material.getName())
					.define('r', ModItemTags.RUBBER)
					.define('w', material.getWireTag())
					.pattern("rwr")
					.pattern("rwr")
					.pattern("rwr")
					.unlockedBy("has_" + material.getName() + "_wire", hasItems(material.getWireTag()))
					.unlockedBy("has_rubber", hasItems(ModItemTags.RUBBER));
				// @formatter:on
			}

			if (material.shouldGenerateWireCoil()) {
				wireCoil(material.getName(), material.getWireCoil().get(), RecipeItem.of(material.getWireTag()));
			}

			if (material.shouldGenerateInsulatedWireCoil()) {
				wireCoil("insulted_" + material.getName(), material.getInsulatedWireCoil().get(), RecipeItem.of(material.getInsulatedWireTag()));
			}
		}

		// @formatter:off
		beginShapedRecipe(ModItems.WireCoilDigistore.get(), 4, "wire/coil_digistore")
			.define('i', ModMaterials.COPPER.getWireTag())
			.define('s', Tags.Items.RODS_WOODEN)
			.define('w', ItemTags.WOOL)
			.pattern("wiw")
			.pattern("isi")
			.pattern("wiw")
			.unlockedBy("has_items", hasItems(Tags.Items.INGOTS_COPPER));
		// @formatter:on

		beginShapelessRecipe(ModMaterials.COPPER.getDust().get(), 2, "dusts/brass_from_dusts").requires(ModMaterials.COPPER.getDustTag()).requires(ModMaterials.ZINC.getDustTag())
				.unlockedBy("has_brass_dust_components", hasItems(ModMaterials.COPPER.getDustTag(), ModMaterials.ZINC.getDustTag()));

		beginShapelessRecipe(ModMaterials.COPPER.getDust().get(), 4, "dusts/bronze_from_dusts").requires(ModMaterials.COPPER.getDustTag())
				.requires(ModMaterials.COPPER.getDustTag()).requires(ModMaterials.COPPER.getDustTag()).requires(ModMaterials.TIN.getDustTag())
				.unlockedBy("has_brass_dust_components", hasItems(ModMaterials.COPPER.getDustTag(), ModMaterials.TIN.getDustTag()));

		beginShapelessRecipe(ModMaterials.INERT_INFUSION.getDust().get(), 3, "dusts/inert_infusion_from_dusts").requires(ModMaterials.IRON.getDustTag())
				.requires(ModMaterials.GOLD.getDustTag()).requires(ModMaterials.PLATINUM.getDustTag())
				.unlockedBy("has_inert_infusion_dust_components", hasItems(ModMaterials.IRON.getDustTag(), ModMaterials.GOLD.getDustTag(), ModMaterials.PLATINUM.getDustTag()));

		beginShapelessRecipe(ModMaterials.REDSTONE_ALLOY.getDust().get(), 2, "dusts/redstone_alloy_from_dusts").requires(ModMaterials.SILVER.getDustTag()).requires(Items.REDSTONE)
				.unlockedBy("has_redston_alloy_dust_components", hasItems(ModMaterials.SILVER.getDustTag(), Tags.Items.DUSTS_REDSTONE));

		portableBattery("basic", ModItems.BasicPortableBattery.get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModMaterials.COPPER.getNuggetTag()),
				RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		portableBattery("advanced", ModItems.AdvancedPortableBattery.get(), RecipeItem.of(ModMaterials.GOLD.getPlateTag()), RecipeItem.of(ModMaterials.IRON.getNuggetTag()),
				RecipeItem.of(Tags.Items.DUSTS_REDSTONE));
		portableBattery("static", ModItems.StaticPortableBattery.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()), RecipeItem.of(ModMaterials.SILVER.getNuggetTag()),
				RecipeItem.of(ModItems.CrystalStatic.get()));
		portableBattery("energized", ModItems.EnergizedPortableBattery.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.GOLD.getNuggetTag()), RecipeItem.of(ModItems.CrystalEnergized.get()));
		portableBattery("lumum", ModItems.LumumPortableBattery.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()), RecipeItem.of(ModMaterials.PLATINUM.getNuggetTag()),
				RecipeItem.of(ModItems.CrystalLumum.get()));

		batteryPack("basic", ModItems.BasicBatteryPack.get(), RecipeItem.of(ModMaterials.COPPER.getWireTag()), RecipeItem.of(ModItems.BasicPortableBattery.get()),
				RecipeItem.of(ModItems.BasicProcessor.get()));
		batteryPack("advanced", ModItems.AdvancedBatteryPack.get(), RecipeItem.of(ModMaterials.BRASS.getWireTag()), RecipeItem.of(ModItems.AdvancedPortableBattery.get()),
				RecipeItem.of(ModItems.AdvancedProcessor.get()));
		batteryPack("static", ModItems.StaticBatteryPack.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getWireTag()), RecipeItem.of(ModItems.StaticPortableBattery.get()),
				RecipeItem.of(ModItems.StaticProcessor.get()));
		batteryPack("energized", ModItems.EnergizedBatteryPack.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getWireTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		batteryPack("lumum", ModItems.LumumBatteryPack.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getWireTag()), RecipeItem.of(ModItems.LumumPortableBattery.get()),
				RecipeItem.of(ModItems.LumumProcessor.get()));

		heatCable("aluminum", ModBlocks.HeatCables.get(StaticPowerTiers.ALUMINUM).get(), RecipeItem.of(ModMaterials.ALUMINUM.getIngotTag()));
		heatCable("copper", ModBlocks.HeatCables.get(StaticPowerTiers.COPPER).get(), RecipeItem.of(ModMaterials.COPPER.getIngotTag()));
		heatCable("gold", ModBlocks.HeatCables.get(StaticPowerTiers.GOLD).get(), RecipeItem.of(ModMaterials.GOLD.getIngotTag()));

		// @formatter:off
		beginShapedRecipe(ModBlocks.ScaffoldCable.get(), 8, "scaffold_cable")
			.define('i', Tags.Items.INGOTS_IRON)
			.define('t', ModMaterials.TIN.getIngotTag())
			.pattern(" t ")
			.pattern(" i ")
			.pattern(" t ")
			.unlockedBy("has_items", hasItems(ModMaterials.TIN.getIngotTag()));
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

		itemTube("basic", ModBlocks.ItemCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		itemTube("advanced", ModBlocks.ItemCables.get(StaticPowerTiers.ADVANCED).get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()),
				RecipeItem.of(ModMaterials.BRASS.getIngotTag()));
		itemTube("static", ModBlocks.ItemCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()));
		itemTube("energized", ModBlocks.ItemCables.get(StaticPowerTiers.ENERGIZED).get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		itemTube("lumum", ModBlocks.ItemCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()));

		fluidPipe("basic", ModBlocks.FluidCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		fluidPipe("advanced", ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()),
				RecipeItem.of(ModMaterials.BRASS.getIngotTag()));
		fluidPipe("static", ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()));
		fluidPipe("energized", ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		fluidPipe("lumum", ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()));

		capillaryFluidPipe("basic", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()),
				RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		capillaryFluidPipe("advanced", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ADVANCED).get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()),
				RecipeItem.of(ModMaterials.BRASS.getIngotTag()));
		capillaryFluidPipe("static", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()));
		capillaryFluidPipe("energized", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ENERGIZED).get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		capillaryFluidPipe("lumum", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()));

		industrialFluidPipe("basic", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));
		industrialFluidPipe("advanced", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ADVANCED).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get()));
		industrialFluidPipe("static", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get()));
		industrialFluidPipe("energized", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ENERGIZED).get(),
				RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get()));
		industrialFluidPipe("lumum", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));

		powerCable("basic", ModBlocks.PowerCables.get(StaticPowerTiers.BASIC).get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		powerCable("advanced", ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()),
				RecipeItem.of(ModMaterials.BRASS.getIngotTag()));
		powerCable("static", ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()));
		powerCable("energized", ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		powerCable("lumum", ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()));

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
			.define('i', ModMaterials.REDSTONE_ALLOY.getIngotTag())
			.pattern("   ")
			.pattern("iii")
			.pattern("   ")
			.unlockedBy("has_items", hasItems(ModMaterials.REDSTONE_ALLOY.getIngotTag()));
		// @formatter:on

		for (RedstoneCableTier tier : Tiers.getRedstone()) {
			coloredRedstoneCable(tier.color().getName(), ModBlocks.RedstoneCables.get(tier.location()).get(),
					RecipeItem.of(ModItemTags.createForgeTag("wool/" + tier.color().getName())));
		}

		extractorAttachment("basic", ModItems.BasicExtractorAttachment.get(), RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		extractorAttachment("advanced", ModItems.AdvancedExtractorAttachment.get(), RecipeItem.of(ModMaterials.BRASS.getIngotTag()));
		extractorAttachment("static", ModItems.StaticExtractorAttachment.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()));
		extractorAttachment("energized", ModItems.EnergizedExtractorAttachment.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		extractorAttachment("lumum", ModItems.LumumExtractorAttachment.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()));

		filterAttachment("basic", ModItems.BasicFilterAttachment.get(), RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		filterAttachment("advanced", ModItems.AdvancedFilterAttachment.get(), RecipeItem.of(ModMaterials.BRASS.getIngotTag()));
		filterAttachment("static", ModItems.StaticFilterAttachment.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()));
		filterAttachment("energized", ModItems.EnergizedFilterAttachment.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		filterAttachment("lumum", ModItems.LumumFilterAttachment.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()));

		retrieverAttachment("basic", ModItems.BasicRetrieverAttachment.get(), RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		retrieverAttachment("advanced", ModItems.AdvancedRetrieverAttachment.get(), RecipeItem.of(ModMaterials.BRASS.getIngotTag()));
		retrieverAttachment("static", ModItems.StaticRetrieverAttachment.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()));
		retrieverAttachment("energized", ModItems.EnergizedRetrieverAttachment.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		retrieverAttachment("lumum", ModItems.LumumRetrieverAttachment.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()));

		chainsawBlade("iron", ModItems.IronChainsawBlade.get(), RecipeItem.of(ModItems.IronBlade.get()));
		chainsawBlade("bronze", ModItems.BronzeChainsawBlade.get(), RecipeItem.of(ModItems.BronzeBlade.get()));
		chainsawBlade("advanced", ModItems.AdvancedChainsawBlade.get(), RecipeItem.of(ModItems.AdvancedBlade.get()));
		chainsawBlade("static", ModItems.StaticChainsawBlade.get(), RecipeItem.of(ModItems.StaticBlade.get()));
		chainsawBlade("energized", ModItems.EnergizedChainsawBlade.get(), RecipeItem.of(ModItems.EnergizedBlade.get()));
		chainsawBlade("lumum", ModItems.LumumChainsawBlade.get(), RecipeItem.of(ModItems.LumumBlade.get()));
		chainsawBlade("tungsten", ModItems.TungstenChainsawBlade.get(), RecipeItem.of(ModItems.TungstenBlade.get()));

		chest("basic", ModBlocks.BasicChest.get(), RecipeItem.of(ModMaterials.TIN.getIngotTag()), RecipeItem.of(ModMaterials.TIN.getPlateTag()));
		chest("advanced", ModBlocks.AdvancedChest.get(), RecipeItem.of(ModMaterials.BRASS.getIngotTag()), RecipeItem.of(ModMaterials.BRASS.getPlateTag()));
		chest("static", ModBlocks.StaticChest.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()));
		chest("energized", ModBlocks.EnergizedChest.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()));
		chest("lumum", ModBlocks.LumumChest.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()));

		fluidCapsule("iron", ModItems.IronFluidCapsule.get(), RecipeItem.of(ModMaterials.IRON.getPlateTag()), RecipeItem.of(Items.BUCKET));
		fluidCapsule("basic", ModItems.BasicFluidCapsule.get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModItems.IronFluidCapsule.get()));
		fluidCapsule("advanced", ModItems.AdvancedFluidCapsule.get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()), RecipeItem.of(ModItems.BasicFluidCapsule.get()));
		fluidCapsule("static", ModItems.StaticFluidCapsule.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()), RecipeItem.of(ModItems.AdvancedFluidCapsule.get()));
		fluidCapsule("energized", ModItems.EnergizedFluidCapsule.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModItems.StaticFluidCapsule.get()));
		fluidCapsule("lumum", ModItems.LumumFluidCapsule.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()), RecipeItem.of(ModItems.EnergizedFluidCapsule.get()));

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

		poweredMagnet("basic", ModItems.BasicMagnet.get(), RecipeItem.of(ModMaterials.TIN.getIngotTag()), RecipeItem.of(ModItems.BasicPortableBattery.get()));
		poweredMagnet("advanced", ModItems.AdvancedMagnet.get(), RecipeItem.of(ModMaterials.BRASS.getIngotTag()), RecipeItem.of(ModItems.AdvancedPortableBattery.get()));
		poweredMagnet("static", ModItems.StaticMagnet.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()), RecipeItem.of(ModItems.StaticPortableBattery.get()));
		poweredMagnet("energized", ModItems.EnergizedMagnet.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()));
		poweredMagnet("lumum", ModItems.LumumMagnet.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()), RecipeItem.of(ModItems.LumumPortableBattery.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.WeakMagnet.get(), "tools/magnet/weak")
			.define('t', ModMaterials.TIN.getIngotTag())
			.define('i', ModMaterials.IRON.getIngotTag())
			.pattern(" t ")
			.pattern("t t")
			.pattern("i i")
			.unlockedBy("has_items", hasItems(ModMaterials.TIN.getIngotTag(), ModMaterials.IRON.getIngotTag()));
		// @formatter:on

		solarPanel("basic", ModBlocks.SolarPanelBasic.get(), RecipeItem.of(ModItems.BasicProcessor.get()), RecipeItem.of(ModItems.Silicon.get()));
		solarPanel("advanced", ModBlocks.SolarPanelAdvanced.get(), RecipeItem.of(ModItems.AdvancedProcessor.get()), RecipeItem.of(ModItems.Silicon.get()));
		solarPanel("static", ModBlocks.SolarPanelStatic.get(), RecipeItem.of(ModItems.StaticProcessor.get()), RecipeItem.of(ModItems.StaticDopedSilicon.get()));
		solarPanel("energized", ModBlocks.SolarPanelEnergized.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()), RecipeItem.of(ModItems.EnergizedDopedSilicon.get()));
		solarPanel("lumum", ModBlocks.SolarPanelLumum.get(), RecipeItem.of(ModItems.LumumProcessor.get()), RecipeItem.of(ModItems.LumumDopedSilicon.get()));

		turbineBlade("basic", ModItems.BasicTurbineBlades.get(), RecipeItem.of(ModMaterials.TIN.getIngotTag()), RecipeItem.of(ModMaterials.TIN.getGearBox().get()));
		turbineBlade("advanced", ModItems.AdvancedTurbineBlades.get(), RecipeItem.of(ModMaterials.BRASS.getIngotTag()), RecipeItem.of(ModMaterials.BRASS.getGearBox().get()));
		turbineBlade("static", ModItems.StaticTurbineBlades.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.getGearBox().get()));
		turbineBlade("energized", ModItems.EnergizedTurbineBlades.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getGearBox().get()));
		turbineBlade("lumum", ModItems.LumumTurbineBlades.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()), RecipeItem.of(ModMaterials.LUMUM_METAL.getGearBox().get()));

		wireTerminal("low_voltage", ModBlocks.WireConnectorLV.get(), RecipeItem.of(Items.BRICK), RecipeItem.of(ModMaterials.IRON.getIngotTag()),
				RecipeItem.of(ModMaterials.COPPER.getWireTag()));
		wireTerminal("medium_voltage", ModBlocks.WireConnectorMV.get(), RecipeItem.of(ModItemTags.RUBBER), RecipeItem.of(ModMaterials.IRON.getIngotTag()),
				RecipeItem.of(ModMaterials.BRASS.getWireTag()));
		wireTerminal("high_voltage", ModBlocks.WireConnectorHV.get(), RecipeItem.of(ModMaterials.SILVER.getIngotTag()), RecipeItem.of(ModMaterials.IRON.getIngotTag()),
				RecipeItem.of(ModMaterials.STATIC_METAL.getWireTag()));
		wireTerminal("extreme_voltage", ModBlocks.WireConnectorEV.get(), RecipeItem.of(ModMaterials.SILVER.getIngotTag()), RecipeItem.of(ModMaterials.IRON.getIngotTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getWireTag()));
		wireTerminal("bonkers_voltage", ModBlocks.WireConnectorBV.get(), RecipeItem.of(ModMaterials.SILVER.getIngotTag()), RecipeItem.of(ModMaterials.IRON.getIngotTag()),
				RecipeItem.of(ModMaterials.LUMUM_METAL.getWireTag()));
		wireTerminal("digistore", ModBlocks.WireConnectorDigistore.get(), RecipeItem.of(ItemTags.WOOL), RecipeItem.of(ModMaterials.BRASS.getIngotTag()),
				RecipeItem.of(ModMaterials.COPPER.getWireTag()));

		chainsaw("basic", ModItems.BasicChainsaw.get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModMaterials.TIN.getGearTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()), RecipeItem.of(ModItems.BasicProcessor.get()));
		chainsaw("advanced", ModItems.AdvancedChainsaw.get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()), RecipeItem.of(ModMaterials.BRASS.getGearTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		chainsaw("static", ModItems.StaticChainsaw.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()), RecipeItem.of(ModMaterials.STATIC_METAL.getGearTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()), RecipeItem.of(ModItems.StaticProcessor.get()));
		chainsaw("energized", ModItems.EnergizedChainsaw.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getGearTag()),
				RecipeItem.of(ModItems.EnergizedPortableBattery.get()), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		chainsaw("lumum", ModItems.LumumChainsaw.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()), RecipeItem.of(ModMaterials.LUMUM_METAL.getGearTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()), RecipeItem.of(ModItems.LumumProcessor.get()));

		miningDrill("basic", ModItems.BasicMiningDrill.get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModMaterials.TIN.getGearTag()),
				RecipeItem.of(ModItems.BasicPortableBattery.get()), RecipeItem.of(ModItems.BasicProcessor.get()));
		miningDrill("advanced", ModItems.AdvancedMiningDrill.get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()), RecipeItem.of(ModMaterials.BRASS.getGearTag()),
				RecipeItem.of(ModItems.AdvancedPortableBattery.get()), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		miningDrill("static", ModItems.StaticMiningDrill.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()), RecipeItem.of(ModMaterials.STATIC_METAL.getGearTag()),
				RecipeItem.of(ModItems.StaticPortableBattery.get()), RecipeItem.of(ModItems.StaticProcessor.get()));
		miningDrill("energized", ModItems.EnergizedMiningDrill.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getGearTag()), RecipeItem.of(ModItems.EnergizedPortableBattery.get()), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		miningDrill("lumum", ModItems.LumumMiningDrill.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()), RecipeItem.of(ModMaterials.LUMUM_METAL.getGearTag()),
				RecipeItem.of(ModItems.LumumPortableBattery.get()), RecipeItem.of(ModItems.LumumProcessor.get()));

		hammer("iron", ModItems.IronMetalHammer.get(), RecipeItem.of(ModMaterials.IRON.getIngotTag()));
		hammer("zinc", ModItems.ZincMetalHammer.get(), RecipeItem.of(ModMaterials.ZINC.getIngotTag()));
		hammer("tin", ModItems.TinMetalHammer.get(), RecipeItem.of(ModMaterials.TIN.getIngotTag()));
		hammer("copper", ModItems.CopperMetalHammer.get(), RecipeItem.of(ModMaterials.COPPER.getIngotTag()));
		hammer("bronze", ModItems.BronzeMetalHammer.get(), RecipeItem.of(ModMaterials.BRONZE.getIngotTag()));
		hammer("tungsten", ModItems.TungstenMetalHammer.get(), RecipeItem.of(ModMaterials.TUNGSTEN.getIngotTag()));

		wireCutter("iron", ModItems.IronWireCutters.get(), RecipeItem.of(ModMaterials.IRON.getIngotTag()), RecipeItem.of(ModMaterials.IRON.getGearTag()));
		wireCutter("zinc", ModItems.ZincWireCutters.get(), RecipeItem.of(ModMaterials.ZINC.getIngotTag()), RecipeItem.of(ModMaterials.ZINC.getGearTag()));
		wireCutter("bronze", ModItems.BronzeWireCutters.get(), RecipeItem.of(ModMaterials.BRONZE.getIngotTag()), RecipeItem.of(ModMaterials.BRONZE.getGearTag()));
		wireCutter("tungsten", ModItems.TungstenWireCutters.get(), RecipeItem.of(ModMaterials.TUNGSTEN.getIngotTag()), RecipeItem.of(ModMaterials.TUNGSTEN.getGearTag()));

		saw("iron", ModItems.IronCoverSaw.get(), RecipeItem.of(ModMaterials.IRON.getIngotTag()));
		saw("ruby", ModItems.RubyCoverSaw.get(), RecipeItem.of(ModMaterials.RUBY.getRawMaterialTag()));
		saw("saphhire", ModItems.SapphireCoverSaw.get(), RecipeItem.of(ModMaterials.SAPPHIRE.getRawMaterialTag()));
		saw("diamond", ModItems.DiamondCoverSaw.get(), RecipeItem.of(Tags.Items.GEMS_DIAMOND));
		saw("tungsten", ModItems.TungstenCoverSaw.get(), RecipeItem.of(ModMaterials.TUNGSTEN.getIngotTag()));

		drillBit("iron", ModItems.IronDrillBit.get(), RecipeItem.of(ModMaterials.IRON.getIngotTag()), RecipeItem.of(ModMaterials.IRON.getPlateTag()));
		drillBit("advanced", ModItems.AdvancedDrillBit.get(), RecipeItem.of(ModMaterials.BRASS.getIngotTag()), RecipeItem.of(ModMaterials.BRASS.getPlateTag()));
		drillBit("bronze", ModItems.BronzeDrillBit.get(), RecipeItem.of(ModMaterials.BRONZE.getIngotTag()), RecipeItem.of(ModMaterials.BRONZE.getPlateTag()));
		drillBit("static", ModItems.StaticDrillBit.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getIngotTag()), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()));
		drillBit("energized", ModItems.EnergizedDrillBit.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()));
		drillBit("lumum", ModItems.LumumDrillBit.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getIngotTag()), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()));
		drillBit("tungsten", ModItems.TungstenDrillBit.get(), RecipeItem.of(ModMaterials.TUNGSTEN.getIngotTag()), RecipeItem.of(ModMaterials.TUNGSTEN.getPlateTag()));

		filter("basic", ModItems.BasicFilter.get(), RecipeItem.of(ModItems.BasicProcessor.get()));
		filter("advanced", ModItems.AdvancedFilter.get(), RecipeItem.of(ModItems.AdvancedProcessor.get()));
		filter("static", ModItems.StaticFilter.get(), RecipeItem.of(ModItems.StaticProcessor.get()));
		filter("energized", ModItems.EnergizedFilter.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		filter("lumum", ModItems.LumumFilter.get(), RecipeItem.of(ModItems.LumumProcessor.get()));

		farmland("static", ModBlocks.StaticFarmland.get(), RecipeItem.of(ModItems.StaticProcessor.get()));
		farmland("energized", ModBlocks.EnergizedFarmland.get(), RecipeItem.of(ModItems.EnergizedProcessor.get()));
		farmland("lumum", ModBlocks.LumumFarmland.get(), RecipeItem.of(ModItems.LumumProcessor.get()));

		digistoreCard("basic", ModItems.BasicDigistoreCard.get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModItems.BasicCard.get()),
				RecipeItem.of(ModItems.DigistoreCore.get()));
		digistoreStackedCard("basic", ModItems.BasicStackedDigistoreCard.get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModItems.BasicCard.get()),
				RecipeItem.of(ModItems.DigistoreCore.get()));
		digistoreMonoCard("basic", ModItems.BasicSingularDigistoreCard.get(), RecipeItem.of(ModMaterials.TIN.getPlateTag()), RecipeItem.of(ModItems.BasicCard.get()),
				RecipeItem.of(ModItems.DigistoreCore.get()));

		digistoreCard("advanced", ModItems.AdvancedDigistoreCard.get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()), RecipeItem.of(ModItems.AdvancedCard.get()),
				RecipeItem.of(ModItems.BasicDigistoreCard.get()));
		digistoreStackedCard("advanced", ModItems.AdvancedStackedDigistoreCard.get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()), RecipeItem.of(ModItems.AdvancedCard.get()),
				RecipeItem.of(ModItems.BasicStackedDigistoreCard.get()));
		digistoreMonoCard("advanced", ModItems.AdvancedSingularDigistoreCard.get(), RecipeItem.of(ModMaterials.BRASS.getPlateTag()), RecipeItem.of(ModItems.AdvancedCard.get()),
				RecipeItem.of(ModItems.BasicSingularDigistoreCard.get()));

		digistoreCard("static", ModItems.StaticDigistoreCard.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()), RecipeItem.of(ModItems.StaticCard.get()),
				RecipeItem.of(ModItems.AdvancedDigistoreCard.get()));
		digistoreStackedCard("static", ModItems.StaticStackedDigistoreCard.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()), RecipeItem.of(ModItems.StaticCard.get()),
				RecipeItem.of(ModItems.AdvancedStackedDigistoreCard.get()));
		digistoreMonoCard("static", ModItems.StaticSingularDigistoreCard.get(), RecipeItem.of(ModMaterials.STATIC_METAL.getPlateTag()), RecipeItem.of(ModItems.StaticCard.get()),
				RecipeItem.of(ModItems.AdvancedSingularDigistoreCard.get()));

		digistoreCard("energized", ModItems.EnergizedDigistoreCard.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()), RecipeItem.of(ModItems.EnergizedCard.get()),
				RecipeItem.of(ModItems.StaticDigistoreCard.get()));
		digistoreStackedCard("energized", ModItems.EnergizedStackedDigistoreCard.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModItems.StaticStackedDigistoreCard.get()), RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));
		digistoreMonoCard("energized", ModItems.EnergizedSingularDigistoreCard.get(), RecipeItem.of(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				RecipeItem.of(ModItems.StaticSingularDigistoreCard.get()), RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));

		digistoreCard("lumum", ModItems.LumumDigistoreCard.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()), RecipeItem.of(ModItems.LumumCard.get()),
				RecipeItem.of(ModItems.EnergizedDigistoreCard.get()));
		digistoreStackedCard("lumum", ModItems.LumumStackedDigistoreCard.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()), RecipeItem.of(ModItems.LumumCard.get()),
				RecipeItem.of(ModItems.EnergizedStackedDigistoreCard.get()));
		digistoreMonoCard("lumum", ModItems.LumumSingularDigistoreCard.get(), RecipeItem.of(ModMaterials.LUMUM_METAL.getPlateTag()), RecipeItem.of(ModItems.LumumCard.get()),
				RecipeItem.of(ModItems.EnergizedSingularDigistoreCard.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.PatternCard.get(), "digistore/pattern_card")
			.define('g', Tags.Items.GLASS_PANES)
			.define('r', Tags.Items.DUSTS_REDSTONE)
			.define('c', ModItems.DigistoreCore.get())
			.define('p', ModMaterials.MAGNESIUM.getIngotTag())
			.pattern("ggg")
			.pattern("rcr")
			.pattern("ppp")
			.unlockedBy("has_digistore_core", hasItems(ModItems.DigistoreCore.get()));
		// @formatter:on

		completeBuilding(finishedRecipeConsumer);
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
			.define('r', ModMaterials.REDSTONE_ALLOY.getIngotTag())
			.pattern("iri")
			.pattern("iri")
			.pattern("iri")
			.unlockedBy("has_items", hasItems(plate));
		
		beginShapedRecipe(output, 6, "power_cables/" + name + "/from_ingot")
			.define('i', ingot)
			.define('r', ModMaterials.REDSTONE_ALLOY.getIngotTag())
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
			.define('b', ModMaterials.REDSTONE_ALLOY.getStorageBlockItemTag())
			.define('r', ModMaterials.REDSTONE_ALLOY.getIngotTag())
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
			.define('i', ModMaterials.REDSTONE_ALLOY.getIngotTag())
			.define('w', wool)
			.pattern("www")
			.pattern("iii")
			.pattern("www")
			.unlockedBy("has_items", hasItems(ModMaterials.REDSTONE_ALLOY.getIngotTag()));
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
			.define('w', ModMaterials.COPPER.getWireTag())
			.define('c', ModMaterials.COPPER.getIngotTag())
			.pattern("ggg")
			.pattern("gwg")
			.pattern(" c ")
			.unlockedBy("has_items", hasItems(ModMaterials.COPPER.getWireTag()));
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
			.define('p', ModMaterials.IRON.getPlateTag())
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
		if (builders.containsKey(name)) {
			throw new RuntimeException("Encountered duplicate recipe name: " + name);
		}
		builders.put(name, builder);
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
		if (builders.containsKey(name)) {
			throw new RuntimeException("Encountered duplicate recipe name: " + name);
		}
		builders.put(name, builder);
		return builder;
	}

	protected TriggerInstance hasWireCutter() {
		return inventoryTrigger(ItemPredicate.Builder.item().of(ModItemTags.WIRE_CUTTER).build());
	}

	protected TriggerInstance hasItems(ItemLike... items) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(items).build());
	}

	protected TriggerInstance hasItems(RecipeItem... items) {
		ItemPredicate.Builder builder = ItemPredicate.Builder.item();
		for (RecipeItem item : items) {
			if (item.hasItemTag()) {
				builder.of(item.getItemTag());
			} else {
				builder.of(item.getItem());
			}
		}
		return inventoryTrigger(builder.build());
	}

	@SuppressWarnings("unchecked")
	protected TriggerInstance hasItems(TagKey<Item>... items) {
		ItemPredicate.Builder builder = ItemPredicate.Builder.item();
		for (TagKey<Item> tag : items) {
			builder.of(tag);
		}
		return inventoryTrigger(builder.build());
	}

	private void completeBuilding(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		for (Entry<String, RecipeBuilder> pair : builders.entrySet()) {
			try {
				pair.getValue().save(finishedRecipeConsumer, new ResourceLocation(StaticPower.MOD_ID, "crafting/" + pair.getKey()));
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to save recipe: %1$s.", pair.getKey()), e);
			}
		}
	}
}
