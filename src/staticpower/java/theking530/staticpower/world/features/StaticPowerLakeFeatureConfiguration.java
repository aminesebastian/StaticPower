package theking530.staticpower.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class StaticPowerLakeFeatureConfiguration implements FeatureConfiguration {
	public static final Codec<StaticPowerLakeFeatureConfiguration> CODEC = RecordCodecBuilder.create((builder) -> {
		return builder.group(BlockStateProvider.CODEC.fieldOf("fluid").forGetter(StaticPowerLakeFeatureConfiguration::getFluid),

				BlockStateProvider.CODEC.fieldOf("barrier").forGetter(StaticPowerLakeFeatureConfiguration::getBarrier),
				IntProvider.CODEC.fieldOf("size").forGetter(StaticPowerLakeFeatureConfiguration::getRadiusProvider),
				IntProvider.CODEC.fieldOf("depth").forGetter(StaticPowerLakeFeatureConfiguration::getDepthProvider)).apply(builder, StaticPowerLakeFeatureConfiguration::new);
	});
	private final BlockStateProvider fluidBlockState;
	private final BlockStateProvider barrier;
	private final IntProvider radiusProvider;
	private final IntProvider depthProvider;

	public StaticPowerLakeFeatureConfiguration(BlockStateProvider fluidBlockState, BlockStateProvider barrier, IntProvider radiusProvider, IntProvider depthProvider) {
		this.fluidBlockState = fluidBlockState;
		this.barrier = barrier;
		this.radiusProvider = radiusProvider;
		this.depthProvider = depthProvider;
	}

	public IntProvider getRadiusProvider() {
		return radiusProvider;
	}

	public IntProvider getDepthProvider() {
		return depthProvider;
	}

	public BlockStateProvider getFluid() {
		return fluidBlockState;
	}

	public BlockStateProvider getBarrier() {
		return barrier;
	}
}