package theking530.staticcore.fluid;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
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

	@Override
	public void tick(Level worldIn, BlockPos pos, FluidState state) {
		// Check if we're gaseous.
		if (state.getType().getFluidType().isLighterThanAir()) {
			// If the fluid is near the world height, kill it.
			if (pos.getY() > worldIn.getMaxBuildHeight() - 5) {
				worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				return;
			}

			// Check if this is a source.
			if (state.isSource()) {
				// Get the block above.
				BlockState getStateAbove = worldIn.getBlockState(pos.relative(Direction.UP));

				// If we can flow upwards, do so. Otherwise, check if the block above is solid.
				// If it is not, let the gas continue going up.
				if (canSpreadTo(worldIn, pos, worldIn.getBlockState(pos), Direction.UP, pos.relative(Direction.UP), getStateAbove, state, state.getType())) {
					worldIn.setBlock(pos.offset(0, 1, 0), this.createLegacyBlock(state), 3);
					worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				} else if (!worldIn.getBlockState(pos.relative(Direction.UP)).canOcclude()) {
					// Make sure the block above the non solid block is air and replaceable.
					if (worldIn.getBlockState(pos.relative(Direction.UP, 2)).isAir()) {
						worldIn.setBlock(pos.offset(0, 2, 0), this.createLegacyBlock(state), 3);
						worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		} else {
			super.tick(worldIn, pos, state);
		}
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
