package theking530.staticpower.world.trees;

import java.util.Random;

import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public abstract class AbstractStaticPowerTree extends AbstractTreeGrower {
	protected abstract TreeConfiguration getTreeConfiguration();

	@Override
	protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random randomIn, boolean b) {
		return Feature.TREE.configured(getTreeConfiguration());
	}

	public abstract boolean addToBiome(BiomeLoadingEvent event);
}
