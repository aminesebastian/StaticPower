package theking530.staticpower.world.trees.rubbertree;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import theking530.staticpower.world.ModConfiguredFeatures;
import theking530.staticpower.world.ModPlacedFeatures;
import theking530.staticpower.world.trees.AbstractStaticPowerTree;

public class RubberTreePlacer extends AbstractStaticPowerTree {
	public RubberTreePlacer() {
	}

	@Override
	protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean p_204330_) {
		return ModConfiguredFeatures.RUBBER_WOOD_TREE;
	}

	@Override
	public TreeGenerationBundle getGenerationBundle() {
		return new TreeGenerationBundle(ModConfiguredFeatures.RUBBER_WOOD_TREE, ModConfiguredFeatures.RUBBER_WOOD_TREE_CHECKED, ModConfiguredFeatures.RUBBER_WOOD_TREE_SPAWN,
				ModPlacedFeatures.RUBBER_WOOD_TREE_PLACED);
	}
}
