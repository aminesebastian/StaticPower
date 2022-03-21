package theking530.staticpower.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class StaticPowerLakeFeatureConfiguration implements FeatureConfiguration {
	public static final Codec<StaticPowerLakeFeatureConfiguration> CODEC = RecordCodecBuilder.create((builder) -> {
		return builder.group(BlockStateProvider.CODEC.fieldOf("fluid").forGetter(StaticPowerLakeFeatureConfiguration::getFluid),
				IntProvider.CODEC.fieldOf("size").forGetter(StaticPowerLakeFeatureConfiguration::getSizeProvider),
				IntProvider.CODEC.fieldOf("depth").forGetter(StaticPowerLakeFeatureConfiguration::getDepthProvider)).apply(builder, StaticPowerLakeFeatureConfiguration::new);
	});
	private final BlockStateProvider fluidBlockState;
	private final IntProvider sizeProvider;
	private final IntProvider depthProvider;

	public StaticPowerLakeFeatureConfiguration(BlockStateProvider fluidBlockState, IntProvider sizeProvider, IntProvider depthProvider) {
		this.fluidBlockState = fluidBlockState;
		this.sizeProvider = sizeProvider;
		this.depthProvider = depthProvider;
	}

	public IntProvider getSizeProvider() {
		return sizeProvider;
	}

	public IntProvider getDepthProvider() {
		return depthProvider;
	}

	public BlockStateProvider getFluid() {
		return fluidBlockState;
	}
}