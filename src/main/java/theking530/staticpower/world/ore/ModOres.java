package theking530.staticpower.world.ore;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

public class ModOres {
	public static final PlacedFeature ZINC = register("ore_zinc", new OreConfigBuilder(ModBlocks.OreZinc).setMaxLevel(70).setMinLevel(30).setMaxVeinSize(4).setRarity(8));
	public static final PlacedFeature MAGENSIUM = register("ore_magnesium", new OreConfigBuilder(ModBlocks.OreMagnesium).setMaxLevel(70).setMinLevel(30).setMaxVeinSize(4).setRarity(8));
	public static final PlacedFeature TIN = register("ore_tin", new OreConfigBuilder(ModBlocks.OreTin).setMaxLevel(100).setMinLevel(30).setMaxVeinSize(10).setRarity(18));
	public static final PlacedFeature LEAD = register("ore_lead", new OreConfigBuilder(ModBlocks.OreLead).setMaxLevel(35).setMinLevel(0).setMaxVeinSize(3).setRarity(10));
	public static final PlacedFeature SILVER = register("ore_silver", new OreConfigBuilder(ModBlocks.OreSilver).setMaxLevel(40).setMinLevel(0).setMaxVeinSize(5).setRarity(12));
	public static final PlacedFeature TUNGSTEN = register("ore_tungsten", new OreConfigBuilder(ModBlocks.OreTungsten).setMaxLevel(20).setMinLevel(0).setMaxVeinSize(4).setRarity(8));
	public static final PlacedFeature PLATINUM = register("ore_platinum", new OreConfigBuilder(ModBlocks.OrePlatinum).setMaxLevel(30).setMinLevel(0).setMaxVeinSize(4).setRarity(10));
	public static final PlacedFeature ALUMINUM = register("ore_aluminum", new OreConfigBuilder(ModBlocks.OreAluminum).setMaxLevel(80).setMinLevel(40).setMaxVeinSize(10).setRarity(12));
	public static final PlacedFeature SAPPHIRE = register("ore_sapphire", new OreConfigBuilder(ModBlocks.OreSapphire).setMaxLevel(20).setMinLevel(0).setMaxVeinSize(4).setRarity(3));
	public static final PlacedFeature RUBY = register("ore_ruby", new OreConfigBuilder(ModBlocks.OreRuby).setMaxLevel(25).setMinLevel(0).setMaxVeinSize(4).setRarity(3));
	public static final PlacedFeature RUSTY_IRON_ORE = register("ore_rusty_iron", new OreConfigBuilder(ModBlocks.OreRustyIron).setMaxLevel(100).setMinLevel(50).setMaxVeinSize(8).setRarity(8));

	public static final PlacedFeature DEEPSLATE_ZINC = register("ore_deepslate_zinc",
			new OreConfigBuilder(ModBlocks.OreDeepslateZinc).setMaxLevel(0).setMinLevel(-32).setMaxVeinSize(4).setRarity(8).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_MAGENSIUM = register("ore_deepslate_magnesium",
			new OreConfigBuilder(ModBlocks.OreDeepslateMagnesium).setMaxLevel(0).setMinLevel(-32).setMaxVeinSize(4).setRarity(8).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_TIN = register("ore_deepslate_tin",
			new OreConfigBuilder(ModBlocks.OreDeepslateTin).setMaxLevel(0).setMinLevel(-16).setMaxVeinSize(10).setRarity(18).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_LEAD = register("ore_deepslate_lead",
			new OreConfigBuilder(ModBlocks.OreDeepslateLead).setMaxLevel(0).setMinLevel(-64).setMaxVeinSize(3).setRarity(10).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_SILVER = register("ore_deepslate_silver",
			new OreConfigBuilder(ModBlocks.OreDeepslateSilver).setMaxLevel(0).setMinLevel(-64).setMaxVeinSize(5).setRarity(12).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_TUNGSTEN = register("ore_deepslate_tungsten",
			new OreConfigBuilder(ModBlocks.OreDeepslateTungsten).setMaxLevel(0).setMinLevel(-64).setMaxVeinSize(4).setRarity(8).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_PLATINUM = register("ore_deepslate_platinum",
			new OreConfigBuilder(ModBlocks.OreDeepslatePlatinum).setMaxLevel(0).setMinLevel(-64).setMaxVeinSize(4).setRarity(10).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_ALUMINUM = register("ore_deepslate_aluminum",
			new OreConfigBuilder(ModBlocks.OreDeepslateAluminum).setMaxLevel(0).setMinLevel(-12).setMaxVeinSize(10).setRarity(12).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_SAPPHIRE = register("ore_deepslate_sapphire",
			new OreConfigBuilder(ModBlocks.OreDeepslateSapphire).setMaxLevel(0).setMinLevel(-64).setMaxVeinSize(4).setRarity(3).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));
	public static final PlacedFeature DEEPSLATE_RUBY = register("ore_deepslate_ruby",
			new OreConfigBuilder(ModBlocks.OreDeepslateRuby).setMaxLevel(0).setMinLevel(-64).setMaxVeinSize(4).setRarity(3).setLocation(OreFeatures.DEEPSLATE_ORE_REPLACEABLES));

	public static final PlacedFeature NETHER_SILVER = register("ore_nether_silver",
			new OreConfigBuilder(ModBlocks.OreNetherSilver).setMaxLevel(100).setMinLevel(0).setMaxVeinSize(5).setRarity(12).setLocation(OreFeatures.NETHER_ORE_REPLACEABLES));

	public static void addOreGenFeatures(BiomeLoadingEvent event) {
		// Overworld
		if (StaticPowerConfig.SERVER.generateZincOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ZINC);
		}
		if (StaticPowerConfig.SERVER.generateMagnesiumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MAGENSIUM);
		}
		if (StaticPowerConfig.SERVER.generateTinOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TIN);
		}
		if (StaticPowerConfig.SERVER.generateLeadOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, LEAD);
		}
		if (StaticPowerConfig.SERVER.generateSilverOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, SILVER);
		}
		if (StaticPowerConfig.SERVER.generateTungstenOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TUNGSTEN);
		}
		if (StaticPowerConfig.SERVER.generatePlatinumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PLATINUM);
		}
		if (StaticPowerConfig.SERVER.generateAluminumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ALUMINUM);
		}
		if (StaticPowerConfig.SERVER.generateSapphireOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, SAPPHIRE);
		}
		if (StaticPowerConfig.SERVER.generateRubyOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RUBY);
		}
		if (StaticPowerConfig.SERVER.generateRustyIronOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RUSTY_IRON_ORE);
		}

		// Deepslate
		if (StaticPowerConfig.SERVER.generateDeepslateZincOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_ZINC);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateMagnesiumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_MAGENSIUM);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateTinOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_TIN);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateLeadOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_LEAD);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateSilverOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_SILVER);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateTungstenOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_TUNGSTEN);
		}
		if (StaticPowerConfig.SERVER.generateDeepslatePlatinumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_PLATINUM);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateAluminumOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_ALUMINUM);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateSapphireOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_SAPPHIRE);
		}
		if (StaticPowerConfig.SERVER.generateDeepslateRubyOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_RUBY);
		}

		
		// Nether
		if (StaticPowerConfig.SERVER.generateNetherSilverOre.get()) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_SILVER);
		}
	}

	private static <C extends FeatureConfiguration, F extends Feature<C>> PlacedFeature register(String registryName, OreConfigBuilder builder) {
		PlacedFeature placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(registryName), builder.build()).placed(builder.getPlacementModifiers());
		return PlacementUtils.register(registryName, placed);
	}

	private static class OreConfigBuilder {
		private final BlockState blockState;
		private int maxVeinSize = 10;
		private int minLevel = 0;
		private int maxLevel = 128;
		private int rarity = 20;
		private RuleTest location;

		public OreConfigBuilder(Block block) {
			this.blockState = block.defaultBlockState();
			this.location = OreFeatures.STONE_ORE_REPLACEABLES;
		}

		public OreConfigBuilder setMaxVeinSize(int maxVeinSize) {
			this.maxVeinSize = maxVeinSize;
			return this;
		}

		public OreConfigBuilder setMinLevel(int minLevel) {
			this.minLevel = minLevel;
			return this;
		}

		public OreConfigBuilder setMaxLevel(int maxLevel) {
			this.maxLevel = maxLevel;
			return this;
		}

		public OreConfigBuilder setRarity(int rarity) {
			this.rarity = rarity;
			return this;
		}

		public OreConfigBuilder setLocation(RuleTest location) {
			this.location = location;
			return this;
		}

		public ConfiguredFeature<?, ?> build() {
			OreConfiguration configuration = new OreConfiguration(location, blockState, maxVeinSize);
			return Feature.ORE.configured(configuration);
		}

		public PlacementModifier[] getPlacementModifiers() {
			return new PlacementModifier[] { CountPlacement.of(rarity), InSquarePlacement.spread(), BiomeFilter.biome(),
					HeightRangePlacement.uniform(VerticalAnchor.absolute(minLevel), VerticalAnchor.absolute(maxLevel)) };
		}
	}
}
