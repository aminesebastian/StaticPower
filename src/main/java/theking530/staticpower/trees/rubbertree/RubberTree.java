package theking530.staticpower.trees.rubbertree;

import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.trees.AbstractStaticPowerTree;

public class RubberTree extends AbstractStaticPowerTree {
	public static final BaseTreeFeatureConfig TREE_CONFIG = new BaseTreeFeatureConfig.Builder(
			new SimpleBlockStateProvider(ModBlocks.RubberTreeLog.getDefaultState()),
			new SimpleBlockStateProvider(ModBlocks.RubberTreeLeaves.getDefaultState()),
			new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3),
			new StraightTrunkPlacer(4, 2, 2), new TwoLayerFeature(1, 0, 1)).build();

	@Override
	protected BaseTreeFeatureConfig getTreeConfiguration() {
		return TREE_CONFIG;
	}

}
