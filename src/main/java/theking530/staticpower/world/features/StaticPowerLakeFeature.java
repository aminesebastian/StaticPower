package theking530.staticpower.world.features;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import theking530.staticcore.utilities.SDMath;

public class StaticPowerLakeFeature extends Feature<StaticPowerLakeFeatureConfiguration> {
	public StaticPowerLakeFeature(ResourceLocation registryName, Codec<StaticPowerLakeFeatureConfiguration> codec) {
		super(codec);
		this.setRegistryName(registryName);
	}

	@Override
	public boolean place(FeaturePlaceContext<StaticPowerLakeFeatureConfiguration> configContext) {
		BlockPos blockpos = configContext.origin();
		WorldGenLevel worldgenlevel = configContext.level();
		Random random = configContext.random();
		StaticPowerLakeFeatureConfiguration config = configContext.config();

		int depth = SDMath.getRandomIntInRange(config.getDepthProvider().getMinValue(), config.getDepthProvider().getMaxValue());
		int xSize = SDMath.getRandomIntInRange(config.getSizeProvider().getMinValue(), config.getSizeProvider().getMaxValue());
		int zSize = SDMath.getRandomIntInRange(config.getSizeProvider().getMinValue(), config.getSizeProvider().getMaxValue());

		for (int y = 0; y < depth; y++) {
			// For the first 20 levels, just place the central block.
			if (y < 20) {
				blockpos = configContext.origin().offset(0, -y, 0);
				if (shouldPlaceHere(worldgenlevel, config, blockpos)) {
					BlockState fluidBlock = config.getFluid().getState(random, blockpos);
					BlockState existingState = worldgenlevel.getBlockState(blockpos);
					if (!existingState.isAir()) {
						worldgenlevel.setBlock(blockpos, fluidBlock, 2);
						worldgenlevel.scheduleTick(blockpos, fluidBlock.getBlock(), 0);
						this.markAboveForPostProcessing(worldgenlevel, blockpos);
					}
				}
				continue;
			}
			
			// Don't let it go below the world.
			if (configContext.origin().getY() - y <= worldgenlevel.getMinBuildHeight()) {
				break;
			}

			int noisyX = (int) (random.nextDouble() * 5 + xSize);
			int noisyZ = (int) (random.nextDouble() * 5 + zSize);
			for (int x = -noisyX; x < noisyX; x++) {
				for (int z = -noisyZ; z < noisyZ; z++) {
					// Get the position to place at and the existing block.
					blockpos = configContext.origin().offset(x, -y, z);

					// If this is an air block, ignore it.
					if (!shouldPlaceHere(worldgenlevel, config, blockpos)) {
						continue;
					}

					BlockState fluidBlock = config.getFluid().getState(random, blockpos);
					worldgenlevel.setBlock(blockpos, fluidBlock, 2);
					worldgenlevel.scheduleTick(blockpos, fluidBlock.getBlock(), 0);
					markAboveForPostProcessing(worldgenlevel, blockpos);
				}
			}
		}
		return true;
	}

	private boolean shouldPlaceHere(WorldGenLevel level, StaticPowerLakeFeatureConfiguration config, BlockPos pos) {
		BlockState existingState = level.getBlockState(pos);
		if (!canReplaceBlock(existingState)) {
			return false;
		}

		for (Direction dir : Direction.values()) {
			if (dir == Direction.UP) {
				continue;
			}
			
			BlockPos offsetPos = pos.relative(dir);
			BlockState state = level.getBlockState(offsetPos);
			if (state.isAir()) {
				return false;
			}
		}
		return true;
	}

	private boolean canReplaceBlock(BlockState state) {
		return !state.is(BlockTags.FEATURES_CANNOT_REPLACE);

	}
}