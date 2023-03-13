package theking530.staticcore.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.init.ModFeatures;

public class OreRegistryObject {
	private final String name;
	private final List<OreGenerationPair> pairs;
	private int maxVeinSize = 10;
	private int minLevel = 0;
	private int maxLevel = 128;
	private int veinsPerChunk = 20;
	private OreRegistrationResult result;

	public static OreRegistryObject createOre(String name) {
		return new OreRegistryObject(name);
	}

	protected OreRegistryObject(String name) {
		this.name = name;
		pairs = new ArrayList<OreGenerationPair>();
	}

	public OreRegistrationResult get() {
		return result;
	}

	public <T extends Block> OreRegistryObject blockSource(Supplier<T> block) {
		return blockSource(block, OreFeatures.STONE_ORE_REPLACEABLES);
	}

	public OreRegistryObject source(Supplier<BlockState> blockState) {
		return blockStateSource(blockState, OreFeatures.STONE_ORE_REPLACEABLES);
	}

	public <T extends Block> OreRegistryObject blockSource(Supplier<T> block, RuleTest rule) {
		pairs.add(new OreGenerationPair(rule, () -> block.get().defaultBlockState()));
		return this;
	}

	public OreRegistryObject blockStateSource(Supplier<BlockState> blockState, RuleTest rule) {
		pairs.add(new OreGenerationPair(rule, blockState));
		return this;
	}

	public OreRegistryObject veinSize(int maxVeinSize) {
		this.maxVeinSize = maxVeinSize;
		return this;
	}

	public OreRegistryObject levelRange(int minLevel, int maxLevel) {
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		return this;
	}

	public OreRegistryObject veinsPerChunk(int veinsPerChunk) {
		this.veinsPerChunk = veinsPerChunk;
		return this;
	}

	public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
		return orePlacement(CountPlacement.of(p_195344_), p_195345_);
	}

	public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
		return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
	}

	public OreRegistrationResult register() {
		Supplier<ConfiguredFeature<OreConfiguration, ?>> configuration = () -> {
			List<OreConfiguration.TargetBlockState> targets = new ArrayList<>();
			for (OreGenerationPair pair : pairs) {
				targets.add(OreConfiguration.target(pair.getLocation(), pair.getBlockState().get()));
			}
			return new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(targets, maxVeinSize));
		};
		RegistryObject<ConfiguredFeature<?, ?>> registeredConfiguredFeature = ModFeatures.CONFIGURED_FEATURES.register(name, configuration);

		Supplier<PlacedFeature> testFeature = () -> new PlacedFeature(registeredConfiguredFeature.getHolder().get(),
				commonOrePlacement(veinsPerChunk, HeightRangePlacement.uniform(VerticalAnchor.absolute(minLevel), VerticalAnchor.absolute(maxLevel))));
		RegistryObject<PlacedFeature> registeredPlacedFeatue = ModFeatures.PLACED_FEATURES.register(name + "_placed", testFeature);

		result = new OreRegistrationResult(registeredConfiguredFeature, registeredPlacedFeatue);
		return result;
	}

	public class OreRegistrationResult {
		public final RegistryObject<ConfiguredFeature<?, ?>> configuration;
		public final RegistryObject<PlacedFeature> feature;

		public OreRegistrationResult(RegistryObject<ConfiguredFeature<?, ?>> configuration, RegistryObject<PlacedFeature> feature) {
			this.configuration = configuration;
			this.feature = feature;
		}
	}

	protected class OreGenerationPair {
		private final RuleTest location;
		private final Supplier<BlockState> blockState;

		public OreGenerationPair(RuleTest location, Supplier<BlockState> blockState) {
			this.location = location;
			this.blockState = blockState;
		}

		public RuleTest getLocation() {
			return location;
		}

		public Supplier<BlockState> getBlockState() {
			return blockState;
		}
	}
}
