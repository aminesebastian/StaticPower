package theking530.staticpower.world.features;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;

public class StaticPowerLakeFeature extends Feature<StaticPowerLakeFeatureConfiguration> {
	private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

	public StaticPowerLakeFeature(Codec<StaticPowerLakeFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<StaticPowerLakeFeatureConfiguration> configContext) {
		WorldGenLevel worldgenlevel = configContext.level();
		RandomSource random = configContext.random();
		StaticPowerLakeFeatureConfiguration config = configContext.config();
		int depth = SDMath.getRandomIntInRange(config.getDepthProvider().getMinValue(), config.getDepthProvider().getMaxValue());
		int xSize = SDMath.getRandomIntInRange(config.getRadiusProvider().getMinValue(), config.getRadiusProvider().getMaxValue());
		int bulbStartDepth = 10;
		int bulbRadius = depth - bulbStartDepth;

		placeSpout(5, worldgenlevel, configContext.origin(), random, config);

		placeCenterColumn(depth, worldgenlevel, configContext.origin(), random, config);

		placeBulb(xSize, bulbRadius, worldgenlevel, configContext.origin().relative(Direction.DOWN, bulbStartDepth), random, config);

		return true;
	}

	private void placeSpout(int height, WorldGenLevel worldgenlevel, BlockPos origin, RandomSource random, StaticPowerLakeFeatureConfiguration config) {
		for (int i = 0; i < height; i++) {
			BlockPos pos = origin.relative(Direction.UP, i);
			if (worldgenlevel.getBlockState(pos).isAir()) {
				BlockState fluidBlockState = config.getFluid().getState(random, pos);
				worldgenlevel.setBlock(pos, fluidBlockState, 2);
				worldgenlevel.scheduleTick(pos, fluidBlockState.getBlock(), 0);
				markAboveForPostProcessing(worldgenlevel, pos);
			}
		}
	}

	private void placeCenterColumn(int depth, WorldGenLevel worldgenlevel, BlockPos origin, RandomSource random, StaticPowerLakeFeatureConfiguration config) {
		for (int i = 0; i < depth; i++) {
			BlockPos pos = origin.relative(Direction.DOWN, i);
			// Don't let it go below the world.
			if (pos.getY() >= worldgenlevel.getMaxBuildHeight() || pos.getY() <= worldgenlevel.getMinBuildHeight() + 2) {
				break;
			}

			if (shouldPlaceHere(worldgenlevel, config, pos)) {
				BlockState fluidBlockState = config.getFluid().getState(random, pos);
				worldgenlevel.setBlock(pos, fluidBlockState, 2);
				worldgenlevel.scheduleTick(pos, fluidBlockState.getBlock(), 0);
				markAboveForPostProcessing(worldgenlevel, pos);
			}
		}
	}

	private void placeBulb(int radius, int bulbHeight, WorldGenLevel worldgenlevel, BlockPos origin, RandomSource random, StaticPowerLakeFeatureConfiguration config) {
		int bulbRadius = bulbHeight / 2;
		for (int y = 0; y < bulbHeight; y++) {
			int distanceFromCenter = Math.abs(bulbRadius - y);
			float normalizedDistanceFromCenter = (float) distanceFromCenter / bulbRadius;
			normalizedDistanceFromCenter = 1.0f - normalizedDistanceFromCenter;
			int sliceRadius = Math.max(1, (int) (normalizedDistanceFromCenter * radius));
			System.out.println(sliceRadius);
			for (int x = -sliceRadius - 1; x < sliceRadius + 1; x++) {
				for (int z = -sliceRadius + 1; z < sliceRadius - 1; z++) {
					float currentRadius = new Vector2D(x, z).getLength();
					if (currentRadius > sliceRadius + 2) {
						continue;
					}

					// Get the position to place at and the existing block.
					BlockPos placePos = origin.offset(x, -y, z);

					// Don't let it go below the world.
					if (placePos.getY() >= worldgenlevel.getMaxBuildHeight() || placePos.getY() <= worldgenlevel.getMinBuildHeight() + 2) {
						break;
					}

					// If we can't place here, skip it.
					if (!shouldPlaceHere(worldgenlevel, config, placePos)) {
						continue;
					}

					// Set the barrier block if we're larger than the slice radius.
					if (currentRadius > sliceRadius) {
						BlockState wallState = config.getBarrier().getState(random, placePos);
						worldgenlevel.setBlock(placePos, wallState, 2);
						worldgenlevel.scheduleTick(placePos, wallState.getBlock(), 0);
						markAboveForPostProcessing(worldgenlevel, placePos);
					} else {
						BlockState fluidBlockState = config.getFluid().getState(random, placePos);
						worldgenlevel.setBlock(placePos, fluidBlockState, 2);
						worldgenlevel.scheduleTick(placePos, fluidBlockState.getBlock(), 0);
						markAboveForPostProcessing(worldgenlevel, placePos);
					}
				}
			}
		}
	}

	private boolean shouldPlaceHere(WorldGenLevel level, StaticPowerLakeFeatureConfiguration config, BlockPos pos) {
		BlockState existingState = level.getBlockState(pos);
		if (existingState.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
			return false;
		}

		for (Direction dir : Direction.values()) {
			if (dir == Direction.UP) {
				continue;
			}

			// Do the check like this so void air/cave air are still okay.
			if (level.getBlockState(pos.relative(dir)).is(Blocks.AIR)) {
				return false;
			}
		}
		return true;
	}

	private boolean canReplaceBlock(BlockState state) {
		return !state.is(BlockTags.FEATURES_CANNOT_REPLACE);

	}
}