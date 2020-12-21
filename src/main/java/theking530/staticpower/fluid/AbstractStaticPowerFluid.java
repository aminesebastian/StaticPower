package theking530.staticpower.fluid;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import theking530.staticpower.StaticPower;

public abstract class AbstractStaticPowerFluid extends FlowingFluid {

	public Supplier<Item> Bucket;
	public Supplier<StaticPowerFluidBlock> FluidBlock;
	public Supplier<Source> StillFluid;
	public Supplier<Flowing> FlowingFluid;
	public Consumer<FluidAttributes.Builder> AdditionalAtrributesDelegate;
	public String StillTexture;
	public String FlowingTexture;
	public INamedTag<Fluid> Tag;

	public AbstractStaticPowerFluid(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock, Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid,
			String stillTexture, String flowingTexture, INamedTag<Fluid> tag, Consumer<FluidAttributes.Builder> attributes) {
		setRegistryName(name);
		Bucket = bucket;
		FluidBlock = fluidBlock;
		StillFluid = stillFluid;
		FlowingFluid = flowingFluid;
		StillTexture = stillTexture;
		FlowingTexture = flowingTexture;
		Tag = tag;
		AdditionalAtrributesDelegate = attributes;
	}

	@Override
	public Fluid getFlowingFluid() {
		return FlowingFluid.get();
	}

	@Override
	public Fluid getStillFluid() {
		return StillFluid.get();
	}

	@Override
	public Item getFilledBucket() {
		return Bucket.get();
	}

	@Override
	protected boolean canSourcesMultiply() {
		return false;
	}

	@Override
	protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
		TileEntity tileentity = state.getBlock().hasTileEntity(state) ? worldIn.getTileEntity(pos) : null;
		Block.spawnDrops(state, worldIn, pos, tileentity);
	}

	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn) {
		return 4;
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
		return 1;
	}

	@Override
	protected boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction) {
		return blockReader.getBlockState(pos).getBlock() == Blocks.AIR;
	}

	@Override
	public int getTickRate(IWorldReader reader) {
		return getAttributes().getViscosity() / 200;
	}

	@Override
	protected float getExplosionResistance() {
		return 100;
	}

	@Override
	protected BlockState getBlockState(FluidState state) {
		return FluidBlock.get().getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
	}

	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == getFlowingFluid() || fluidIn == getStillFluid();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(World worldIn, BlockPos pos, FluidState state) {
		// Check if we're gaseous.
		if (getFluid().getAttributes().isGaseous()) {
			// If the fluid is near the world height, kill it.
			if (pos.getY() > worldIn.getHeight() - 5) {
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				return;
			}

			// Check if this is a source.
			if (state.isSource()) {
				// Get the block above.
				BlockState getStateAbove = worldIn.getBlockState(pos.offset(Direction.UP));

				// If we can flow upwards, do so. Otherwise, check if the block above is solid.
				// If it is not, let the gas continue going up.
				if (canFlow(worldIn, pos, worldIn.getBlockState(pos), Direction.UP, pos.offset(Direction.UP), getStateAbove, state, getFluid())) {
					worldIn.setBlockState(pos.add(0, 1, 0), this.getBlockState(state), 3);
					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				} else if (!worldIn.getBlockState(pos.offset(Direction.UP)).isSolid()) {
					// Make sure the block above the non solid block is air and replaceable.
					if (worldIn.getBlockState(pos.offset(Direction.UP, 2)).isAir()) {
						worldIn.setBlockState(pos.add(0, 2, 0), this.getBlockState(state), 3);
						worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					}
				}
			}
		} else {
			super.tick(worldIn, pos, state);
		}
	}

	@Override
	protected FluidAttributes createAttributes() {
		FluidAttributes.Builder attributes = FluidAttributes.builder(new ResourceLocation(StaticPower.MOD_ID, StillTexture), new ResourceLocation(StaticPower.MOD_ID, FlowingTexture))
				.translationKey(FluidBlock.get().getTranslationKey().replace("block", "fluid")).color(0xaaffffff);
		if (AdditionalAtrributesDelegate != null) {
			AdditionalAtrributesDelegate.accept(attributes);
		}
		return attributes.build(this);
	}

	public static class Source extends AbstractStaticPowerFluid {

		public Source(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock, Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture,
				String flowingTexture, INamedTag<Fluid> tag, Consumer<FluidAttributes.Builder> attributes) {
			super(name, bucket, fluidBlock, stillFluid, flowingFluid, stillTexture, flowingTexture, tag, attributes);
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getLevel(FluidState p_207192_1_) {
			return 8;
		}
	}

	public static class Flowing extends AbstractStaticPowerFluid {

		public Flowing(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock, Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture,
				String flowingTexture, INamedTag<Fluid> tag, Consumer<FluidAttributes.Builder> attributes) {
			super(name + "_flowing", bucket, fluidBlock, stillFluid, flowingFluid, stillTexture, flowingTexture, tag, attributes);
		}

		@Override
		protected void fillStateContainer(Builder<Fluid, FluidState> builder) {
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}

		@Override
		public int getLevel(FluidState state) {
			return state.get(AbstractStaticPowerFluid.LEVEL_1_8);
		}
	}
}
