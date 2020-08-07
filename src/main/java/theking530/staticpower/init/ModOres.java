package theking530.staticpower.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class ModOres {
	private static List<OreConfigBuilder> ORE_GENERATORS = new ArrayList<OreConfigBuilder>();

	public static final OreConfigBuilder Zinc = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreZinc).setMaxLevel(70).setMinLevel(30).setMaxVeinSize(4).setRarity(15));
	public static final OreConfigBuilder Magnesium = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreMagnesium).setMaxLevel(70).setMinLevel(30).setMaxVeinSize(4).setRarity(15));
	public static final OreConfigBuilder Copper = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreCopper).setMaxLevel(100).setMinLevel(40).setMaxVeinSize(10).setRarity(20));
	public static final OreConfigBuilder Tin = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreTin).setMaxLevel(100).setMinLevel(40).setMaxVeinSize(7).setRarity(20));
	public static final OreConfigBuilder Lead = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreLead).setMaxLevel(35).setMinLevel(0).setMaxVeinSize(3).setRarity(15));
	public static final OreConfigBuilder Silver = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreSilver).setMaxLevel(40).setMinLevel(0).setMaxVeinSize(5).setRarity(18));
	public static final OreConfigBuilder Tungsten = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreTungsten).setMaxLevel(30).setMinLevel(0).setMaxVeinSize(2).setRarity(6));
	public static final OreConfigBuilder Platinum = registerOreGeneration(new OreConfigBuilder(ModBlocks.OrePlatinum).setMaxLevel(30).setMinLevel(0).setMaxVeinSize(4).setRarity(12));
	public static final OreConfigBuilder Aluminium = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreAluminium).setMaxLevel(80).setMinLevel(40).setMaxVeinSize(10).setRarity(25));
	public static final OreConfigBuilder Sapphire = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreSapphire).setMaxLevel(20).setMinLevel(0).setMaxVeinSize(4).setRarity(8));
	public static final OreConfigBuilder Ruby = registerOreGeneration(new OreConfigBuilder(ModBlocks.OreRuby).setMaxLevel(25).setMinLevel(0).setMaxVeinSize(4).setRarity(8));

	public static OreConfigBuilder registerOreGeneration(OreConfigBuilder builder) {
		ORE_GENERATORS.add(builder);
		return builder;
	}

	public static void init() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			for (OreConfigBuilder config : ORE_GENERATORS) {
				biome.addFeature(Decoration.UNDERGROUND_ORES, config.build());
			}
		}
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
			return Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, blockSupplier.getDefaultState(), maxVeinSize))
					.withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(rarity, minLevel, 0, maxLevel)));
		}
	}
}
