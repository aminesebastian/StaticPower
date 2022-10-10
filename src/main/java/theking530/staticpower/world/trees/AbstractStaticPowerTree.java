package theking530.staticpower.world.trees;

import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@SuppressWarnings("deprecation")
public abstract class AbstractStaticPowerTree extends AbstractTreeGrower {

	public AbstractStaticPowerTree() {
	}

	public abstract TreeGenerationBundle getGenerationBundle();

	public static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block trunk, Block leaves, int p_195149_, int p_195150_, int p_195151_, int p_195152_) {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(trunk), new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_),
				BlockStateProvider.simple(leaves), new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
	}

	public class TreeGenerationBundle {
		public Holder<ConfiguredFeature<TreeConfiguration, ?>> treeFeature;
		public Holder<PlacedFeature> treePlacedFeatureChecked;
		public Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> treeSpawnFeature;
		public Holder<PlacedFeature> treePlacedFeature;

		public TreeGenerationBundle(Holder<ConfiguredFeature<TreeConfiguration, ?>> treeFeature, Holder<PlacedFeature> treePlacedFeatureChecked,
				Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> treeSpawnFeature, Holder<PlacedFeature> treePlacedFeature) {
			super();
			this.treeFeature = treeFeature;
			this.treePlacedFeatureChecked = treePlacedFeatureChecked;
			this.treeSpawnFeature = treeSpawnFeature;
			this.treePlacedFeature = treePlacedFeature;
		}
	}
}
