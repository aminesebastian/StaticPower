package theking530.staticpower.world.ore;

import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

public class ModOres {
	public static final ConfiguredFeature<OreFeatureConfig, ?> ZINC = register("ore_zinc",
			new OreConfigBuilder(ModBlocks.OreZinc).setMaxLevel(70).setMinLevel(30).setMaxVeinSize(4).setRarity(8));
	public static final ConfiguredFeature<OreFeatureConfig, ?> MAGENSIUM = register("ore_magnesium",
			new OreConfigBuilder(ModBlocks.OreMagnesium).setMaxLevel(70).setMinLevel(30).setMaxVeinSize(4).setRarity(8));
	public static final ConfiguredFeature<OreFeatureConfig, ?> COPPER = register("ore_copper",
			new OreConfigBuilder(ModBlocks.OreCopper).setMaxLevel(100).setMinLevel(40).setMaxVeinSize(10).setRarity(16));
	public static final ConfiguredFeature<OreFeatureConfig, ?> TIN = register("ore_tin",
			new OreConfigBuilder(ModBlocks.OreTin).setMaxLevel(100).setMinLevel(30).setMaxVeinSize(10).setRarity(18));
	public static final ConfiguredFeature<OreFeatureConfig, ?> LEAD = register("ore_lead",
			new OreConfigBuilder(ModBlocks.OreLead).setMaxLevel(35).setMinLevel(0).setMaxVeinSize(3).setRarity(10));
	public static final ConfiguredFeature<OreFeatureConfig, ?> SILVER = register("ore_silver",
			new OreConfigBuilder(ModBlocks.OreSilver).setMaxLevel(40).setMinLevel(0).setMaxVeinSize(5).setRarity(12));
	public static final ConfiguredFeature<OreFeatureConfig, ?> TUNGSTEN = register("ore_tungsten",
			new OreConfigBuilder(ModBlocks.OreTungsten).setMaxLevel(20).setMinLevel(0).setMaxVeinSize(4).setRarity(8));
	public static final ConfiguredFeature<OreFeatureConfig, ?> PLATINUM = register("ore_platinum",
			new OreConfigBuilder(ModBlocks.OrePlatinum).setMaxLevel(30).setMinLevel(0).setMaxVeinSize(4).setRarity(10));
	public static final ConfiguredFeature<OreFeatureConfig, ?> ALUMINIUM = register("ore_aluminium",
			new OreConfigBuilder(ModBlocks.OreAluminium).setMaxLevel(80).setMinLevel(40).setMaxVeinSize(10).setRarity(12));
	public static final ConfiguredFeature<OreFeatureConfig, ?> SAPPHIRE = register("ore_sapphire",
			new OreConfigBuilder(ModBlocks.OreSapphire).setMaxLevel(20).setMinLevel(0).setMaxVeinSize(4).setRarity(3));
	public static final ConfiguredFeature<OreFeatureConfig, ?> RUBY = register("ore_ruby",
			new OreConfigBuilder(ModBlocks.OreRuby).setMaxLevel(25).setMinLevel(0).setMaxVeinSize(4).setRarity(3));

	public static void addOreGenFeatures(BiomeLoadingEvent event) {
		if (StaticPowerConfig.SERVER.generateZincOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ZINC);
		}
		if (StaticPowerConfig.SERVER.generateMagnesiumOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, MAGENSIUM);
		}
		if (StaticPowerConfig.SERVER.generateCopperOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, COPPER);
		}
		if (StaticPowerConfig.SERVER.generateTinOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TIN);
		}
		if (StaticPowerConfig.SERVER.generateLeadOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, LEAD);
		}
		if (StaticPowerConfig.SERVER.generateSilverOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, SILVER);
		}
		if (StaticPowerConfig.SERVER.generateTungstenOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TUNGSTEN);
		}
		if (StaticPowerConfig.SERVER.generatePlatinumOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, PLATINUM);
		}
		if (StaticPowerConfig.SERVER.generateAluminiumOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ALUMINIUM);
		}
		if (StaticPowerConfig.SERVER.generateSapphireOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, SAPPHIRE);
		}
		if (StaticPowerConfig.SERVER.generateRubyOre.get()) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, RUBY);
		}
	}

	@SuppressWarnings("unchecked")
	private static ConfiguredFeature<OreFeatureConfig, ?> register(String name, OreConfigBuilder config) {
		return (ConfiguredFeature<OreFeatureConfig, ?>) Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, name, config.build());
	}

	private static class OreConfigBuilder {
		private final Block blockSupplier;
		private int maxVeinSize = 10;
		private int minLevel = 0;
		private int maxLevel = 128;
		private int rarity = 20;

		public OreConfigBuilder(Block blockSupplier) {
			this.blockSupplier = blockSupplier;
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

		public ConfiguredFeature<?, ?> build() {
			return Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, blockSupplier.getDefaultState(), maxVeinSize))
					.withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(minLevel, 0, maxLevel))).square().func_242731_b(rarity);
		}
	}
}
