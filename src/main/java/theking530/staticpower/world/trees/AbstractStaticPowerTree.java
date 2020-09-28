package theking530.staticpower.world.trees;

import java.util.Random;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public abstract class AbstractStaticPowerTree extends Tree {

	protected abstract BaseTreeFeatureConfig getTreeConfiguration();

	@Override
	protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean b) {
		return Feature.TREE.withConfiguration(getTreeConfiguration());
	}
}
