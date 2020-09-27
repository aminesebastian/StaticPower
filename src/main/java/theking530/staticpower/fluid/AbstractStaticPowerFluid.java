package theking530.staticpower.fluid;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
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

	public AbstractStaticPowerFluid(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock,
			Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture, String flowingTexture,
			INamedTag<Fluid> tag, Consumer<FluidAttributes.Builder> attributes) {
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
	protected boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid,
			Direction direction) {
		return false; // !fluid.isIn(Tag) && !(blockReader.getBlockState(pos).has(LEVEL_1_8));
	}

	@Override
	public int getTickRate(IWorldReader reader) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100;
	}

	@Override
	protected BlockState getBlockState(FluidState state) {
		return FluidBlock.get().getDefaultState().with(FlowingFluidBlock.LEVEL,
				Integer.valueOf(getLevelFromState(state)));
	}

	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == getFlowingFluid() || fluidIn == getStillFluid();
	}

	@Override
	protected FluidAttributes createAttributes() {
		FluidAttributes.Builder attributes = FluidAttributes
				.builder(new ResourceLocation(StaticPower.MOD_ID, StillTexture),
						new ResourceLocation(StaticPower.MOD_ID, FlowingTexture))
				.translationKey(FluidBlock.get().getTranslationKey().replace("block", "fluid"));
		if (AdditionalAtrributesDelegate != null) {
			AdditionalAtrributesDelegate.accept(attributes);
		}
		return attributes.build(this);
	}

	public static class Source extends AbstractStaticPowerFluid {

		public Source(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock,
				Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture, String flowingTexture,
				INamedTag<Fluid> tag, Consumer<FluidAttributes.Builder> attributes) {
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

		public Flowing(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock,
				Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture, String flowingTexture,
				INamedTag<Fluid> tag, Consumer<FluidAttributes.Builder> attributes) {
			super(name + "_flowing", bucket, fluidBlock, stillFluid, flowingFluid, stillTexture, flowingTexture, tag,
					attributes);
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
