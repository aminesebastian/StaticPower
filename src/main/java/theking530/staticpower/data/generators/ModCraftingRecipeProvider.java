package theking530.staticpower.data.generators;

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
import theking530.staticpower.data.generators.helpers.SPShapedRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPShapelessRecipeBuilder;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class ModCraftingRecipeProvider extends RecipeProvider implements IConditionBuilder {
	private Map<String, RecipeBuilder> builders;

	public ModCraftingRecipeProvider(DataGenerator p_125973_) {
		super(p_125973_);
		builders = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (material.shouldGenerateStorageBlock() && material.shouldGenerateIngot()) {
				// @formatter:off
				beginShapedRecipe(material.getSmeltedMaterialStorageBlock().get(), "storage_blocks/from_ingots/" + material.getName())
					.define('i', material.getIngotTag())
					.pattern("iii")
					.pattern("iii")
					.pattern("iii")
					.unlockedBy("has_" + material.getName() + "_smelted_material", hasItems(material.getIngotTag()));
				// @formatter:on

				beginShapelessRecipe(material.getSmeltedMaterial().get(), 9, "ingots/from_block/" + material.getName()).requires(material.getStorageBlockItemTag())
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
				beginShapedRecipe(material.getSmeltedMaterial().get(), "ingots/from_nuggets/" + material.getName())
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
				wireCoil(material.getName(), material.getWireCoil().get(), new RecipeItem(material.getWireTag()));
			}

			if (material.shouldGenerateInsulatedWireCoil()) {
				wireCoil("insulted_" + material.getName(), material.getInsulatedWireCoil().get(), new RecipeItem(material.getInsulatedWireTag()));
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

		portableBattery("basic", ModItems.BasicPortableBattery.get(), new RecipeItem(ModMaterials.TIN.getPlateTag()), new RecipeItem(ModMaterials.COPPER.getNuggetTag()),
				new RecipeItem(Tags.Items.DUSTS_REDSTONE));
		portableBattery("advanced", ModItems.AdvancedPortableBattery.get(), new RecipeItem(ModMaterials.GOLD.getPlateTag()), new RecipeItem(ModMaterials.IRON.getNuggetTag()),
				new RecipeItem(Tags.Items.DUSTS_REDSTONE));
		portableBattery("static", ModItems.StaticPortableBattery.get(), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()), new RecipeItem(ModMaterials.SILVER.getNuggetTag()),
				new RecipeItem(ModItems.CrystalStatic.get()));
		portableBattery("energized", ModItems.EnergizedPortableBattery.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.GOLD.getNuggetTag()), new RecipeItem(ModItems.CrystalEnergized.get()));
		portableBattery("lumum", ModItems.LumumPortableBattery.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()), new RecipeItem(ModMaterials.PLATINUM.getNuggetTag()),
				new RecipeItem(ModItems.CrystalLumum.get()));

		batteryPack("basic", ModItems.BasicBatteryPack.get(), new RecipeItem(ModMaterials.COPPER.getWireTag()), new RecipeItem(ModItems.BasicPortableBattery.get()),
				new RecipeItem(ModItems.BasicProcessor.get()));
		batteryPack("advanced", ModItems.AdvancedBatteryPack.get(), new RecipeItem(ModMaterials.COPPER.getWireTag()), new RecipeItem(ModItems.AdvancedPortableBattery.get()),
				new RecipeItem(ModItems.AdvancedProcessor.get()));
		batteryPack("static", ModItems.StaticBatteryPack.get(), new RecipeItem(ModMaterials.BRASS.getWireTag()), new RecipeItem(ModItems.StaticPortableBattery.get()),
				new RecipeItem(ModItems.StaticProcessor.get()));
		batteryPack("energized", ModItems.EnergizedBatteryPack.get(), new RecipeItem(ModMaterials.GOLD.getWireTag()), new RecipeItem(ModItems.EnergizedPortableBattery.get()),
				new RecipeItem(ModItems.EnergizedProcessor.get()));
		batteryPack("lumum", ModItems.LumumBatteryPack.get(), new RecipeItem(ModMaterials.GOLD.getWireTag()), new RecipeItem(ModItems.LumumPortableBattery.get()),
				new RecipeItem(ModItems.LumumProcessor.get()));

		heatCable("aluminum", ModBlocks.HeatCables.get(StaticPowerTiers.ALUMINUM).get(), new RecipeItem(ModMaterials.ALUMINUM.getIngotTag()));
		heatCable("copper", ModBlocks.HeatCables.get(StaticPowerTiers.COPPER).get(), new RecipeItem(ModMaterials.COPPER.getIngotTag()));
		heatCable("gold", ModBlocks.HeatCables.get(StaticPowerTiers.GOLD).get(), new RecipeItem(ModMaterials.GOLD.getIngotTag()));

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

		itemTube("basic", ModBlocks.ItemCables.get(StaticPowerTiers.BASIC).get(), new RecipeItem(ModMaterials.TIN.getPlateTag()), new RecipeItem(ModMaterials.TIN.getIngotTag()));
		itemTube("advanced", ModBlocks.ItemCables.get(StaticPowerTiers.ADVANCED).get(), new RecipeItem(ModMaterials.BRASS.getPlateTag()),
				new RecipeItem(ModMaterials.BRASS.getIngotTag()));
		itemTube("static", ModBlocks.ItemCables.get(StaticPowerTiers.STATIC).get(), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()));
		itemTube("energized", ModBlocks.ItemCables.get(StaticPowerTiers.ENERGIZED).get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		itemTube("lumum", ModBlocks.ItemCables.get(StaticPowerTiers.LUMUM).get(), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()));

		fluidPipe("basic", ModBlocks.FluidCables.get(StaticPowerTiers.BASIC).get(), new RecipeItem(ModMaterials.TIN.getPlateTag()), new RecipeItem(ModMaterials.TIN.getIngotTag()));
		fluidPipe("advanced", ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get(), new RecipeItem(ModMaterials.BRASS.getPlateTag()),
				new RecipeItem(ModMaterials.BRASS.getIngotTag()));
		fluidPipe("static", ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get(), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()));
		fluidPipe("energized", ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		fluidPipe("lumum", ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get(), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()));

		capillaryFluidPipe("basic", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.BASIC).get(), new RecipeItem(ModMaterials.TIN.getPlateTag()),
				new RecipeItem(ModMaterials.TIN.getIngotTag()));
		capillaryFluidPipe("advanced", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ADVANCED).get(), new RecipeItem(ModMaterials.BRASS.getPlateTag()),
				new RecipeItem(ModMaterials.BRASS.getIngotTag()));
		capillaryFluidPipe("static", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.STATIC).get(), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()));
		capillaryFluidPipe("energized", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.ENERGIZED).get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		capillaryFluidPipe("lumum", ModBlocks.CapillaryFluidCables.get(StaticPowerTiers.LUMUM).get(), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()));

		industrialFluidPipe("basic", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.BASIC).get(), new RecipeItem(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));
		industrialFluidPipe("advanced", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ADVANCED).get(),
				new RecipeItem(ModBlocks.FluidCables.get(StaticPowerTiers.ADVANCED).get()));
		industrialFluidPipe("static", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.STATIC).get(), new RecipeItem(ModBlocks.FluidCables.get(StaticPowerTiers.STATIC).get()));
		industrialFluidPipe("energized", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.ENERGIZED).get(),
				new RecipeItem(ModBlocks.FluidCables.get(StaticPowerTiers.ENERGIZED).get()));
		industrialFluidPipe("lumum", ModBlocks.IndustrialFluidCables.get(StaticPowerTiers.LUMUM).get(), new RecipeItem(ModBlocks.FluidCables.get(StaticPowerTiers.LUMUM).get()));

		powerCable("basic", ModBlocks.PowerCables.get(StaticPowerTiers.BASIC).get(), new RecipeItem(ModMaterials.TIN.getPlateTag()),
				new RecipeItem(ModMaterials.TIN.getIngotTag()));
		powerCable("advanced", ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get(), new RecipeItem(ModMaterials.BRASS.getPlateTag()),
				new RecipeItem(ModMaterials.BRASS.getIngotTag()));
		powerCable("static", ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get(), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()));
		powerCable("energized", ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		powerCable("lumum", ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get(), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()),
				new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()));

		insulatedPowerCable("basic", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.BASIC).get(), new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.BASIC).get()));
		insulatedPowerCable("advanced", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.ADVANCED).get(),
				new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get()));
		insulatedPowerCable("static", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.STATIC).get(), new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get()));
		insulatedPowerCable("energized", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.ENERGIZED).get(),
				new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get()));
		insulatedPowerCable("lumum", ModBlocks.InsulatedPowerCables.get(StaticPowerTiers.LUMUM).get(), new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));

		industrialPowerCable("basic", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.BASIC).get(), new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));
		industrialPowerCable("advanced", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.ADVANCED).get(),
				new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.ADVANCED).get()));
		industrialPowerCable("static", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.STATIC).get(),
				new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.STATIC).get()));
		industrialPowerCable("energized", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.ENERGIZED).get(),
				new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.ENERGIZED).get()));
		industrialPowerCable("lumum", ModBlocks.IndustrialPowerCables.get(StaticPowerTiers.LUMUM).get(), new RecipeItem(ModBlocks.PowerCables.get(StaticPowerTiers.LUMUM).get()));

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
					new RecipeItem(ModItemTags.createForgeTag("wool/" + tier.color().getName())));
		}

		extractorAttachment("basic", ModItems.BasicExtractorAttachment.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()));
		extractorAttachment("advanced", ModItems.AdvancedExtractorAttachment.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()));
		extractorAttachment("static", ModItems.StaticExtractorAttachment.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()));
		extractorAttachment("energized", ModItems.EnergizedExtractorAttachment.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		extractorAttachment("lumum", ModItems.LumumExtractorAttachment.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()));

		filterAttachment("basic", ModItems.BasicFilterAttachment.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()));
		filterAttachment("advanced", ModItems.AdvancedFilterAttachment.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()));
		filterAttachment("static", ModItems.StaticFilterAttachment.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()));
		filterAttachment("energized", ModItems.EnergizedFilterAttachment.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		filterAttachment("lumum", ModItems.LumumFilterAttachment.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()));

		retrieverAttachment("basic", ModItems.BasicRetrieverAttachment.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()));
		retrieverAttachment("advanced", ModItems.AdvancedRetrieverAttachment.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()));
		retrieverAttachment("static", ModItems.StaticRetrieverAttachment.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()));
		retrieverAttachment("energized", ModItems.EnergizedRetrieverAttachment.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()));
		retrieverAttachment("lumum", ModItems.LumumRetrieverAttachment.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()));

		chainsawBlade("iron", ModItems.IronChainsawBlade.get(), new RecipeItem(ModItems.IronBlade.get()));
		chainsawBlade("bronze", ModItems.BronzeChainsawBlade.get(), new RecipeItem(ModItems.BronzeBlade.get()));
		chainsawBlade("advanced", ModItems.AdvancedChainsawBlade.get(), new RecipeItem(ModItems.AdvancedBlade.get()));
		chainsawBlade("static", ModItems.StaticChainsawBlade.get(), new RecipeItem(ModItems.StaticBlade.get()));
		chainsawBlade("energized", ModItems.EnergizedChainsawBlade.get(), new RecipeItem(ModItems.EnergizedBlade.get()));
		chainsawBlade("lumum", ModItems.LumumChainsawBlade.get(), new RecipeItem(ModItems.LumumBlade.get()));
		chainsawBlade("tungsten", ModItems.TungstenChainsawBlade.get(), new RecipeItem(ModItems.TungstenBlade.get()));

		chest("basic", ModBlocks.BasicChest.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()), new RecipeItem(ModMaterials.TIN.getPlateTag()));
		chest("advanced", ModBlocks.AdvancedChest.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()), new RecipeItem(ModMaterials.BRASS.getPlateTag()));
		chest("static", ModBlocks.StaticChest.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()));
		chest("energized", ModBlocks.EnergizedChest.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()), new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()));
		chest("lumum", ModBlocks.LumumChest.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()));

		fluidCapsule("iron", ModItems.IronFluidCapsule.get(), new RecipeItem(ModMaterials.IRON.getPlateTag()), new RecipeItem(Items.BUCKET));
		fluidCapsule("basic", ModItems.BasicFluidCapsule.get(), new RecipeItem(ModMaterials.TIN.getPlateTag()), new RecipeItem(ModItems.IronFluidCapsule.get()));
		fluidCapsule("advanced", ModItems.AdvancedFluidCapsule.get(), new RecipeItem(ModMaterials.BRASS.getPlateTag()), new RecipeItem(ModItems.BasicFluidCapsule.get()));
		fluidCapsule("static", ModItems.StaticFluidCapsule.get(), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()), new RecipeItem(ModItems.AdvancedFluidCapsule.get()));
		fluidCapsule("energized", ModItems.EnergizedFluidCapsule.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()),
				new RecipeItem(ModItems.StaticFluidCapsule.get()));
		fluidCapsule("lumum", ModItems.LumumFluidCapsule.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()), new RecipeItem(ModItems.EnergizedFluidCapsule.get()));

		pie("apple", ModItems.ApplePie.get(), new RecipeItem(Items.APPLE));
		pie("static", ModItems.StaticPie.get(), new RecipeItem(ModItems.StaticFruit.get()));
		pie("energized", ModItems.EnergizedPie.get(), new RecipeItem(ModItems.EnergizedFruit.get()));
		pie("lumum", ModItems.LumumPie.get(), new RecipeItem(ModItems.LumumFruit.get()));

		for (MinecraftColor color : MinecraftColor.values()) {
			lightbulb(color.getName(), ModItems.Lightbulbs.get(color).get(), new RecipeItem(ModItemTags.createForgeTag("glass_panes/" + color.getName())));
		}

		bed(MinecraftColor.WHITE.getName(), Blocks.WHITE_BED, new RecipeItem(Blocks.WHITE_WOOL));
		bed(MinecraftColor.LIGHT_GRAY.getName(), Blocks.LIGHT_GRAY_BED, new RecipeItem(Blocks.LIGHT_GRAY_WOOL));
		bed(MinecraftColor.GRAY.getName(), Blocks.GRAY_BED, new RecipeItem(Blocks.GRAY_WOOL));
		bed(MinecraftColor.BLACK.getName(), Blocks.BLACK_BED, new RecipeItem(Blocks.BLACK_WOOL));
		bed(MinecraftColor.BROWN.getName(), Blocks.BROWN_BED, new RecipeItem(Blocks.BROWN_WOOL));
		bed(MinecraftColor.PINK.getName(), Blocks.PINK_BED, new RecipeItem(Blocks.PINK_WOOL));
		bed(MinecraftColor.RED.getName(), Blocks.RED_BED, new RecipeItem(Blocks.RED_WOOL));
		bed(MinecraftColor.ORANGE.getName(), Blocks.ORANGE_BED, new RecipeItem(Blocks.ORANGE_WOOL));
		bed(MinecraftColor.YELLOW.getName(), Blocks.YELLOW_BED, new RecipeItem(Blocks.YELLOW_WOOL));
		bed(MinecraftColor.LIME.getName(), Blocks.LIME_BED, new RecipeItem(Blocks.LIME_WOOL));
		bed(MinecraftColor.GREEN.getName(), Blocks.GREEN_BED, new RecipeItem(Blocks.GREEN_WOOL));
		bed(MinecraftColor.CYAN.getName(), Blocks.CYAN_BED, new RecipeItem(Blocks.CYAN_WOOL));
		bed(MinecraftColor.LIGHT_BLUE.getName(), Blocks.LIGHT_BLUE_BED, new RecipeItem(Blocks.LIGHT_BLUE_WOOL));
		bed(MinecraftColor.BLUE.getName(), Blocks.BLUE_BED, new RecipeItem(Blocks.BLUE_WOOL));
		bed(MinecraftColor.PURPLE.getName(), Blocks.PURPLE_BED, new RecipeItem(Blocks.PURPLE_WOOL));
		bed(MinecraftColor.MAGENTA.getName(), Blocks.MAGENTA_BED, new RecipeItem(Blocks.MAGENTA_WOOL));

		poweredMagnet("basic", ModItems.BasicMagnet.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()), new RecipeItem(ModItems.BasicPortableBattery.get()));
		poweredMagnet("advanced", ModItems.AdvancedMagnet.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()), new RecipeItem(ModItems.AdvancedPortableBattery.get()));
		poweredMagnet("static", ModItems.StaticMagnet.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()), new RecipeItem(ModItems.StaticPortableBattery.get()));
		poweredMagnet("energized", ModItems.EnergizedMagnet.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				new RecipeItem(ModItems.EnergizedPortableBattery.get()));
		poweredMagnet("lumum", ModItems.LumumMagnet.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()), new RecipeItem(ModItems.LumumPortableBattery.get()));

		// @formatter:off
		beginShapedRecipe(ModItems.WeakMagnet.get(), "tools/magnet/weak")
			.define('t', ModMaterials.TIN.getIngotTag())
			.define('i', ModMaterials.IRON.getIngotTag())
			.pattern(" t ")
			.pattern("t t")
			.pattern("i i")
			.unlockedBy("has_items", hasItems(ModMaterials.TIN.getIngotTag(), ModMaterials.IRON.getIngotTag()));
		// @formatter:on

		solarPanel("basic", ModBlocks.SolarPanelBasic.get(), new RecipeItem(ModItems.BasicProcessor.get()), new RecipeItem(ModItems.Silicon.get()));
		solarPanel("advanced", ModBlocks.SolarPanelAdvanced.get(), new RecipeItem(ModItems.AdvancedProcessor.get()), new RecipeItem(ModItems.Silicon.get()));
		solarPanel("static", ModBlocks.SolarPanelStatic.get(), new RecipeItem(ModItems.StaticProcessor.get()), new RecipeItem(ModItems.StaticDopedSilicon.get()));
		solarPanel("energized", ModBlocks.SolarPanelEnergized.get(), new RecipeItem(ModItems.EnergizedProcessor.get()), new RecipeItem(ModItems.EnergizedDopedSilicon.get()));
		solarPanel("lumum", ModBlocks.SolarPanelLumum.get(), new RecipeItem(ModItems.LumumProcessor.get()), new RecipeItem(ModItems.LumumDopedSilicon.get()));

		turbineBlade("basic", ModItems.BasicTurbineBlades.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()), new RecipeItem(ModMaterials.TIN.getGearBox().get()));
		turbineBlade("advanced", ModItems.AdvancedTurbineBlades.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()), new RecipeItem(ModMaterials.BRASS.getGearBox().get()));
		turbineBlade("static", ModItems.StaticTurbineBlades.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()),
				new RecipeItem(ModMaterials.STATIC_METAL.getGearBox().get()));
		turbineBlade("energized", ModItems.EnergizedTurbineBlades.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getGearBox().get()));
		turbineBlade("lumum", ModItems.LumumTurbineBlades.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()),
				new RecipeItem(ModMaterials.LUMUM_METAL.getGearBox().get()));

		wireTerminal("low_voltage", ModBlocks.WireConnectorLV.get(), new RecipeItem(Items.BRICK), new RecipeItem(ModMaterials.IRON.getIngotTag()),
				new RecipeItem(ModMaterials.COPPER.getWireTag()));
		wireTerminal("medium_voltage", ModBlocks.WireConnectorMV.get(), new RecipeItem(ModItemTags.RUBBER), new RecipeItem(ModMaterials.IRON.getIngotTag()),
				new RecipeItem(ModMaterials.BRASS.getWireTag()));
		wireTerminal("high_voltage", ModBlocks.WireConnectorHV.get(), new RecipeItem(ModMaterials.SILVER.getIngotTag()), new RecipeItem(ModMaterials.IRON.getIngotTag()),
				new RecipeItem(ModMaterials.STATIC_METAL.getWireTag()));
		wireTerminal("extreme_voltage", ModBlocks.WireConnectorEV.get(), new RecipeItem(ModMaterials.SILVER.getIngotTag()), new RecipeItem(ModMaterials.IRON.getIngotTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getWireTag()));
		wireTerminal("bonkers_voltage", ModBlocks.WireConnectorBV.get(), new RecipeItem(ModMaterials.SILVER.getIngotTag()), new RecipeItem(ModMaterials.IRON.getIngotTag()),
				new RecipeItem(ModMaterials.LUMUM_METAL.getWireTag()));
		wireTerminal("digistore", ModBlocks.WireConnectorDigistore.get(), new RecipeItem(ItemTags.WOOL), new RecipeItem(ModMaterials.BRASS.getIngotTag()),
				new RecipeItem(ModMaterials.COPPER.getWireTag()));

		chainsaw("basic", ModItems.BasicChainsaw.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()), new RecipeItem(ModMaterials.TIN.getGearTag()),
				new RecipeItem(ModItems.BasicPortableBattery.get()), new RecipeItem(ModItems.BasicProcessor.get()));
		chainsaw("advanced", ModItems.AdvancedChainsaw.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()), new RecipeItem(ModMaterials.BRASS.getGearTag()),
				new RecipeItem(ModItems.AdvancedPortableBattery.get()), new RecipeItem(ModItems.AdvancedProcessor.get()));
		chainsaw("static", ModItems.StaticChainsaw.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()), new RecipeItem(ModMaterials.STATIC_METAL.getGearTag()),
				new RecipeItem(ModItems.StaticPortableBattery.get()), new RecipeItem(ModItems.StaticProcessor.get()));
		chainsaw("energized", ModItems.EnergizedChainsaw.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getGearTag()), new RecipeItem(ModItems.EnergizedPortableBattery.get()),
				new RecipeItem(ModItems.EnergizedProcessor.get()));
		chainsaw("lumum", ModItems.LumumChainsaw.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()), new RecipeItem(ModMaterials.LUMUM_METAL.getGearTag()),
				new RecipeItem(ModItems.LumumPortableBattery.get()), new RecipeItem(ModItems.LumumProcessor.get()));

		miningDrill("basic", ModItems.BasicMiningDrill.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()), new RecipeItem(ModMaterials.TIN.getGearTag()),
				new RecipeItem(ModItems.BasicPortableBattery.get()), new RecipeItem(ModItems.BasicProcessor.get()));
		miningDrill("advanced", ModItems.AdvancedMiningDrill.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()), new RecipeItem(ModMaterials.BRASS.getGearTag()),
				new RecipeItem(ModItems.AdvancedPortableBattery.get()), new RecipeItem(ModItems.AdvancedProcessor.get()));
		miningDrill("static", ModItems.StaticMiningDrill.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()), new RecipeItem(ModMaterials.STATIC_METAL.getGearTag()),
				new RecipeItem(ModItems.StaticPortableBattery.get()), new RecipeItem(ModItems.StaticProcessor.get()));
		miningDrill("energized", ModItems.EnergizedMiningDrill.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getGearTag()), new RecipeItem(ModItems.EnergizedPortableBattery.get()),
				new RecipeItem(ModItems.EnergizedProcessor.get()));
		miningDrill("lumum", ModItems.LumumMiningDrill.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()), new RecipeItem(ModMaterials.LUMUM_METAL.getGearTag()),
				new RecipeItem(ModItems.LumumPortableBattery.get()), new RecipeItem(ModItems.LumumProcessor.get()));

		hammer("iron", ModItems.IronMetalHammer.get(), new RecipeItem(ModMaterials.IRON.getIngotTag()));
		hammer("zinc", ModItems.ZincMetalHammer.get(), new RecipeItem(ModMaterials.ZINC.getIngotTag()));
		hammer("tin", ModItems.TinMetalHammer.get(), new RecipeItem(ModMaterials.TIN.getIngotTag()));
		hammer("copper", ModItems.CopperMetalHammer.get(), new RecipeItem(ModMaterials.COPPER.getIngotTag()));
		hammer("bronze", ModItems.BronzeMetalHammer.get(), new RecipeItem(ModMaterials.BRONZE.getIngotTag()));
		hammer("tungsten", ModItems.TungstenMetalHammer.get(), new RecipeItem(ModMaterials.TUNGSTEN.getIngotTag()));

		wireCutter("iron", ModItems.IronWireCutters.get(), new RecipeItem(ModMaterials.IRON.getIngotTag()), new RecipeItem(ModMaterials.IRON.getGearTag()));
		wireCutter("zinc", ModItems.ZincWireCutters.get(), new RecipeItem(ModMaterials.ZINC.getIngotTag()), new RecipeItem(ModMaterials.ZINC.getGearTag()));
		wireCutter("bronze", ModItems.BronzeWireCutters.get(), new RecipeItem(ModMaterials.BRONZE.getIngotTag()), new RecipeItem(ModMaterials.BRONZE.getGearTag()));
		wireCutter("tungsten", ModItems.TungstenWireCutters.get(), new RecipeItem(ModMaterials.TUNGSTEN.getIngotTag()), new RecipeItem(ModMaterials.TUNGSTEN.getGearTag()));

		saw("iron", ModItems.IronCoverSaw.get(), new RecipeItem(ModMaterials.IRON.getIngotTag()));
		saw("ruby", ModItems.RubyCoverSaw.get(), new RecipeItem(ModMaterials.RUBY.getRawMaterialTag()));
		saw("saphhire", ModItems.SapphireCoverSaw.get(), new RecipeItem(ModMaterials.SAPPHIRE.getRawMaterialTag()));
		saw("diamond", ModItems.DiamondCoverSaw.get(), new RecipeItem(Tags.Items.GEMS_DIAMOND));
		saw("tungsten", ModItems.TungstenCoverSaw.get(), new RecipeItem(ModMaterials.TUNGSTEN.getIngotTag()));

		drillBit("iron", ModItems.IronDrillBit.get(), new RecipeItem(ModMaterials.IRON.getIngotTag()), new RecipeItem(ModMaterials.IRON.getPlateTag()));
		drillBit("advanced", ModItems.AdvancedDrillBit.get(), new RecipeItem(ModMaterials.BRASS.getIngotTag()), new RecipeItem(ModMaterials.BRASS.getPlateTag()));
		drillBit("bronze", ModItems.BronzeDrillBit.get(), new RecipeItem(ModMaterials.BRONZE.getIngotTag()), new RecipeItem(ModMaterials.BRONZE.getPlateTag()));
		drillBit("static", ModItems.StaticDrillBit.get(), new RecipeItem(ModMaterials.STATIC_METAL.getIngotTag()), new RecipeItem(ModMaterials.STATIC_METAL.getPlateTag()));
		drillBit("energized", ModItems.EnergizedDrillBit.get(), new RecipeItem(ModMaterials.ENERGIZED_METAL.getIngotTag()),
				new RecipeItem(ModMaterials.ENERGIZED_METAL.getPlateTag()));
		drillBit("lumum", ModItems.LumumDrillBit.get(), new RecipeItem(ModMaterials.LUMUM_METAL.getIngotTag()), new RecipeItem(ModMaterials.LUMUM_METAL.getPlateTag()));
		drillBit("tungsten", ModItems.TungstenDrillBit.get(), new RecipeItem(ModMaterials.TUNGSTEN.getIngotTag()), new RecipeItem(ModMaterials.TUNGSTEN.getPlateTag()));

		filter("basic", ModItems.BasicFilter.get(), new RecipeItem(ModItems.BasicProcessor.get()));
		filter("advanced", ModItems.AdvancedFilter.get(), new RecipeItem(ModItems.AdvancedProcessor.get()));
		filter("static", ModItems.StaticFilter.get(), new RecipeItem(ModItems.StaticProcessor.get()));
		filter("energized", ModItems.EnergizedFilter.get(), new RecipeItem(ModItems.EnergizedProcessor.get()));
		filter("lumum", ModItems.LumumFilter.get(), new RecipeItem(ModItems.LumumProcessor.get()));

		farmland("static", ModBlocks.StaticFarmland.get(), new RecipeItem(ModItems.StaticProcessor.get()));
		farmland("energized", ModBlocks.EnergizedFarmland.get(), new RecipeItem(ModItems.EnergizedProcessor.get()));
		farmland("lumum", ModBlocks.LumumFarmland.get(), new RecipeItem(ModItems.LumumProcessor.get()));

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
			.unlockedBy("has_items", hasItems(new RecipeItem(Items.CHAIN), blade));
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
			.unlockedBy("has_items", hasItems(new RecipeItem(Tags.Items.CHESTS_WOODEN), ingot));
		// @formatter:on

		// @formatter:off
		beginShapedRecipe(output,  "chests/" + name + "_from_plate" )
			.define('i', plate)
			.define('c', Tags.Items.CHESTS_WOODEN)
			.pattern("iii")
			.pattern("ici")
			.pattern("iii")
			.unlockedBy("has_items", hasItems(new RecipeItem(Tags.Items.CHESTS_WOODEN), plate));
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

	protected void chainsaw(String name, ItemLike output, RecipeItem ingot, RecipeItem gear, RecipeItem battery, RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(output, "tools/chainsaw/" + name)
			.define('i', ingot)
			.define('g', gear)
			.define('b', battery)
			.define('p', processor)
			.define('m', ModItems.Motor.get())
			.define('l', Items.LEVER)
			.pattern("ibi")
			.pattern("bmg")
			.pattern("lpi")
			.unlockedBy("has_items", hasItems(ModItems.Motor.get()));
		// @formatter:on
	}

	protected void miningDrill(String name, ItemLike output, RecipeItem ingot, RecipeItem gear, RecipeItem battery, RecipeItem processor) {
		// @formatter:off
		beginShapedRecipe(output, "tools/mining_drill/" + name)
			.define('i', ingot)
			.define('g', gear)
			.define('b', battery)
			.define('p', processor)
			.define('m', ModItems.Motor.get())
			.define('l', Items.LEVER)
			.pattern("ili")
			.pattern("bmg")
			.pattern("bpi")
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
				pair.getValue().save(finishedRecipeConsumer, new ResourceLocation(StaticPower.MOD_ID, "crafting/" + pair.getKey()).toString());
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to save recipe: %1$s.", pair.getKey()), e);
			}
		}
	}
}
