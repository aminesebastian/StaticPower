package theking530.staticcore.fluid;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import theking530.staticcore.block.IRenderLayerProvider;

public abstract class AbstractStaticPowerFluid extends ForgeFlowingFluid implements IRenderLayerProvider {

	public TagKey<Fluid> Tag;

	public AbstractStaticPowerFluid(Properties properties, TagKey<Fluid> tag) {
		super(properties);
		Tag = tag;
	}

	@Override
	public int getTickDelay(LevelReader reader) {
		return getFluidType().getViscosity() / 200;
	}

	protected void spread(LevelAccessor level, BlockPos pos, FluidState fluidState) {
		if (!fluidState.getType().getFluidType().isLighterThanAir()) {
			super.spread(level, pos, fluidState);
			return;
		}

		if (!fluidState.isEmpty()) {
			// If the fluid is near the world height, kill it.
			if (pos.getY() >= level.getMaxBuildHeight()) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				return;
			}

			// If we can flow upwards, do so. Otherwise, check if the block above is solid.
			// If it is not, let the gas continue going up.
			if (canSpreadTo(level, pos, level.getBlockState(pos), Direction.UP, pos.above(),
					level.getBlockState(pos.above()), fluidState, fluidState.getType())) {
				spreadTo(level, pos.relative(Direction.UP), level.getBlockState(pos.above()), Direction.UP, fluidState);
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	protected boolean canSpreadTo(BlockGetter level, BlockPos sourcePos, BlockState sourceState,
			Direction spreadDirection, BlockPos targetPos, BlockState targetState, FluidState fluidState, Fluid fluid) {
		if (super.canSpreadTo(level, sourcePos, sourceState, spreadDirection, targetPos, targetState, fluidState,
				fluid)) {
			return true;
		}
		return targetState.isAir();
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.translucent();
	}

	public static class Source extends AbstractStaticPowerFluid {

		public Source(Properties properties, TagKey<Fluid> tag) {
			super(properties, tag);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends AbstractStaticPowerFluid {

		public Flowing(Properties properties, TagKey<Fluid> tag) {
			super(properties, tag);
		}

		@Override
		protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}

	}
}
