package theking530.staticpower.world.ore;

import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.world.OreRegistrationBuilder;
import theking530.staticcore.world.OreRegistrationBuilder.OreGenerationResult;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

public class ModOres {
	public static final OreGenerationResult ZINC = OreRegistrationBuilder.createOre("ore_zinc")
			.source(ModBlocks.OreZinc).levelRange(30, 70).veinSize(4).rarity(8).register();
	public static final OreGenerationResult MAGENSIUM = OreRegistrationBuilder.createOre("ore_magnesium")
			.source(ModBlocks.OreMagnesium).levelRange(30, 70).veinSize(4).rarity(8).register();
	public static final OreGenerationResult TIN = OreRegistrationBuilder.createOre("ore_tin").source(ModBlocks.OreTin)
			.levelRange(40, 128).veinSize(10).rarity(18).register();
	public static final OreGenerationResult LEAD = OreRegistrationBuilder.createOre("ore_lead")
			.source(ModBlocks.OreLead).levelRange(0, 35).veinSize(4).rarity(10).register();
	public static final OreGenerationResult SILVER = OreRegistrationBuilder.createOre("ore_silver")
			.source(ModBlocks.OreSilver).levelRange(0, 40).veinSize(4).rarity(12).register();
	public static final OreGenerationResult TUNGSTEN = OreRegistrationBuilder.createOre("ore_tungsten")
			.source(ModBlocks.OreTungsten).levelRange(0, 20).veinSize(4).rarity(8).register();
	public static final OreGenerationResult PLATINUM = OreRegistrationBuilder.createOre("ore_platinum")
			.source(ModBlocks.OrePlatinum).levelRange(0, 30).veinSize(4).rarity(10).register();
	public static final OreGenerationResult ALUMINUM = OreRegistrationBuilder.createOre("ore_aluminum")
			.source(ModBlocks.OreAluminum).levelRange(50, 200).veinSize(4).rarity(12).register();
	public static final OreGenerationResult SAPPHIRE = OreRegistrationBuilder.createOre("ore_sapphire")
			.source(ModBlocks.OreSapphire).levelRange(0, 20).veinSize(4).rarity(5).register();
	public static final OreGenerationResult RUBY = OreRegistrationBuilder.createOre("ore_ruby")
			.source(ModBlocks.OreRuby).levelRange(0, 20).veinSize(4).rarity(5).register();
	public static final OreGenerationResult RUSTY_IRON_ORE = OreRegistrationBuilder.createOre("ore_rusty_iron")
			.source(ModBlocks.OreRustyIron).levelRange(50, 150).veinSize(4).rarity(8).register();

	public static final OreGenerationResult DEEPSLATE_ZINC = OreRegistrationBuilder.createOre("ore_deepslate_zinc")
			.source(ModBlocks.OreDeepslateZinc, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-32, 10).veinSize(4)
			.rarity(8).register();
	public static final OreGenerationResult DEEPSLATE_MAGENSIUM = OreRegistrationBuilder
			.createOre("ore_deepslate_magnesium")
			.source(ModBlocks.OreDeepslateMagnesium, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-32, 10)
			.veinSize(4).rarity(8).register();
	public static final OreGenerationResult DEEPSLATE_TIN = OreRegistrationBuilder.createOre("ore_deepslate_tin")
			.source(ModBlocks.OreDeepslateTin, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-16, 30).veinSize(10)
			.rarity(18).register();
	public static final OreGenerationResult DEEPSLATE_LEAD = OreRegistrationBuilder.createOre("ore_deepslate_lead")
			.source(ModBlocks.OreDeepslateLead, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4)
			.rarity(10).register();
	public static final OreGenerationResult DEEPSLATE_SILVER = OreRegistrationBuilder.createOre("ore_deepslate_silver")
			.source(ModBlocks.OreDeepslateSilver, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4)
			.rarity(12).register();
	public static final OreGenerationResult DEEPSLATE_TUNGSTEN = OreRegistrationBuilder
			.createOre("ore_deepslate_tungsten")
			.source(ModBlocks.OreDeepslateTungsten, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0)
			.veinSize(4).rarity(8).register();
	public static final OreGenerationResult DEEPSLATE_PLATINUM = OreRegistrationBuilder
			.createOre("ore_deepslate_platinum")
			.source(ModBlocks.OreDeepslatePlatinum, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0)
			.veinSize(4).rarity(10).register();
	public static final OreGenerationResult DEEPSLATE_ALUMINUM = OreRegistrationBuilder
			.createOre("ore_deepslate_aluminum")
			.source(ModBlocks.OreDeepslateAluminum, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-12, 30)
			.veinSize(4).rarity(12).register();
	public static final OreGenerationResult DEEPSLATE_SAPPHIRE = OreRegistrationBuilder
			.createOre("ore_deepslate_sapphire")
			.source(ModBlocks.OreDeepslateSapphire, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0)
			.veinSize(4).rarity(5).register();
	public static final OreGenerationResult DEEPSLATE_RUBY = OreRegistrationBuilder.createOre("ore_deepslate_ruby")
			.source(ModBlocks.OreDeepslateRuby, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4)
			.rarity(5).register();

	public static final OreGenerationResult NETHER_SILVER = OreRegistrationBuilder.createOre("ore_nether_silver")
			.source(ModBlocks.OreNetherSilver, OreFeatures.NETHER_ORE_REPLACEABLES).levelRange(0, 100).veinSize(5)
			.rarity(12).register();
	public static final OreGenerationResult NETHER_PLATINUM = OreRegistrationBuilder.createOre("ore_nether_platinum")
			.source(ModBlocks.OreNetherPlatinum, OreFeatures.NETHER_ORE_REPLACEABLES).levelRange(0, 100).veinSize(5)
			.rarity(12).register();
	public static final OreGenerationResult NETHER_TUNGSTEN = OreRegistrationBuilder.createOre("ore_nether_tungsten")
			.source(ModBlocks.OreNetherTungsten, OreFeatures.NETHER_ORE_REPLACEABLES).levelRange(0, 100).veinSize(5)
			.rarity(12).register();

	public static void addOreGenFeatures(BiomeLoadingEvent event) {
		// Overworld
		if (StaticPowerConfig.SERVER.generateZincOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ZINC.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateMagnesiumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MAGENSIUM.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateTinOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TIN.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateLeadOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, LEAD.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateSilverOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, SILVER.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateTungstenOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TUNGSTEN.getFeature());
		}
		if (StaticPowerConfig.SERVER.generatePlatinumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PLATINUM.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateAluminumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ALUMINUM.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateSapphireOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, SAPPHIRE.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateRubyOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RUBY.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateRustyIronOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RUSTY_IRON_ORE.getFeature());
		}

		// Deepslate
		if (StaticPowerConfig.SERVER.generateDeepslateZincOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_ZINC.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateMagnesiumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
					DEEPSLATE_MAGENSIUM.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateTinOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_TIN.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateLeadOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_LEAD.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateSilverOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_SILVER.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateTungstenOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
					DEEPSLATE_TUNGSTEN.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslatePlatinumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
					DEEPSLATE_PLATINUM.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateAluminumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
					DEEPSLATE_ALUMINUM.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateSapphireOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
					DEEPSLATE_SAPPHIRE.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateDeepslateRubyOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_RUBY.getFeature());
		}

		// Nether
		if (StaticPowerConfig.SERVER.generateNetherSilverOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_SILVER.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateNetherPlatinumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_PLATINUM.getFeature());
		}
		if (StaticPowerConfig.SERVER.generateNetherTungstenOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_TUNGSTEN.getFeature());
		}
	}
}
