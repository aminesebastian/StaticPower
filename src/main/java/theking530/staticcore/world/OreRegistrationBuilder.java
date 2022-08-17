package theking530.staticcore.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import theking530.staticpower.world.ore.ModOrePlacement;

public class OreRegistrationBuilder {
	private final String name;
	private final List<OreGenerationPair> pairs;
	private int maxVeinSize = 10;
	private int minLevel = 0;
	private int maxLevel = 128;
	private int rarity = 20;

	public static OreRegistrationBuilder createOre(String name) {
		return new OreRegistrationBuilder(name);
	}

	protected OreRegistrationBuilder(String name) {
		this.name = name;
		pairs = new ArrayList<OreGenerationPair>();
	}

	public OreRegistrationBuilder source(BlockState blockState) {
		return source(blockState, OreFeatures.STONE_ORE_REPLACEABLES);
	}

	public OreRegistrationBuilder source(Block block) {
		return source(block, OreFeatures.STONE_ORE_REPLACEABLES);
	}

	public OreRegistrationBuilder source(Block block, RuleTest rule) {
		pairs.add(new OreGenerationPair(rule, block.defaultBlockState()));
		return this;
	}

	public OreRegistrationBuilder source(BlockState blockState, RuleTest rule) {
		pairs.add(new OreGenerationPair(rule, blockState));
		return this;
	}

	public OreRegistrationBuilder veinSize(int maxVeinSize) {
		this.maxVeinSize = maxVeinSize;
		return this;
	}

	public OreRegistrationBuilder levelRange(int minLevel, int maxLevel) {
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		return this;
	}

	public OreRegistrationBuilder rarity(int rarity) {
		this.rarity = rarity;
		return this;
	}

	public OreGenerationResult register() {
		List<OreConfiguration.TargetBlockState> targets = new ArrayList<>();
		for (OreGenerationPair pair : pairs) {
			targets.add(OreConfiguration.target(pair.getLocation(), pair.getBlockState()));
		}

		Holder<ConfiguredFeature<OreConfiguration, ?>> configuration = FeatureUtils.register(name, Feature.ORE,
				new OreConfiguration(targets, maxVeinSize));

		Holder<PlacedFeature> feature = PlacementUtils.register(name + "_placed", configuration,
				ModOrePlacement.commonOrePlacement(rarity, HeightRangePlacement
						.triangle(VerticalAnchor.aboveBottom(minLevel), VerticalAnchor.aboveBottom(maxLevel))));

		return new OreGenerationResult(targets, configuration, feature);
	}

	public class OreGenerationResult {
		private final List<OreConfiguration.TargetBlockState> targets;
		private final Holder<ConfiguredFeature<OreConfiguration, ?>> configuration;
		private final Holder<PlacedFeature> feature;

		public OreGenerationResult(List<TargetBlockState> targets,
				Holder<ConfiguredFeature<OreConfiguration, ?>> configuration, Holder<PlacedFeature> feature) {
			this.targets = targets;
			this.configuration = configuration;
			this.feature = feature;
		}

		public List<OreConfiguration.TargetBlockState> getTargets() {
			return targets;
		}

		public Holder<ConfiguredFeature<OreConfiguration, ?>> getConfiguration() {
			return configuration;
		}

		public Holder<PlacedFeature> getFeature() {
			return feature;
		}	
	}

	protected class OreGenerationPair {
		private final RuleTest location;
		private final BlockState blockState;

		public OreGenerationPair(RuleTest location, BlockState blockState) {
			this.location = location;
			this.blockState = blockState;
		}

		public RuleTest getLocation() {
			return location;
		}

		public BlockState getBlockState() {
			return blockState;
		}
	}
}
