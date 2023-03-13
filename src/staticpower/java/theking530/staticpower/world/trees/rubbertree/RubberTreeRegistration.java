package theking530.staticpower.world.trees.rubbertree;

import java.util.List;

import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.world.trees.ModTreeRegistration;

public class RubberTreeRegistration extends ModTreeRegistration<RubberTreeRegistration> {

	public RubberTreeRegistration() {
		super("rubber_tree");
	}

	@Override
	protected ConfiguredFeature<?, ?> getConfiguredFeature() {
		return new ConfiguredFeature<>(Feature.TREE,
				new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.RubberTreeLog.get()), new StraightTrunkPlacer(4, 2, 0),
						BlockStateProvider.simple(ModBlocks.RubberTreeLeaves.get()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)).build());
	}

	@Override
	protected PlacedFeature getCheckedFeature() {
		return new PlacedFeature(configuredFeature.getHolder().get(), List.of(PlacementUtils.filteredByBlockSurvival(ModBlocks.RubberTreeSapling.get())));
	}

	@Override
	protected ConfiguredFeature<?, ?> getConfiguredSpawnFeature() {
		return new ConfiguredFeature<>(Feature.RANDOM_SELECTOR,
				new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(saplingPlacedFeature.getHolder().get(), 0.25f)), saplingPlacedFeature.getHolder().get()));
	}

	@Override
	protected PlacedFeature getPlacedFeature() {
		return new PlacedFeature(saplingSpawnConfiguredFeature.getHolder().get(), VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.25f, 1)));
	}

}
