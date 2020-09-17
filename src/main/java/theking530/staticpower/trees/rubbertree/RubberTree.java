package theking530.staticpower.trees.rubbertree;

import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraftforge.common.IPlantable;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.trees.AbstractStaticPowerTree;

public class RubberTree extends AbstractStaticPowerTree {
	public static final TreeFeatureConfig TREE_CONFIG = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.RubberTreeLog.getDefaultState()),
			new SimpleBlockStateProvider(ModBlocks.RubberTreeLeaves.getDefaultState()), new BlobFoliagePlacer(1, 1))).trunkHeight(4).heightRandA(3).baseHeight(5).foliageHeight(9).foliageHeightRandom(2).trunkTopOffsetRandom(1).ignoreVines()
					.setSapling((IPlantable) ModBlocks.RubberTreeSapling).build();

	@Override
	protected TreeFeatureConfig getTreeConfiguration() {
		return TREE_CONFIG;
	}

}
