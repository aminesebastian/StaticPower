package theking530.staticpower.fluid;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;

public abstract class AbstractStaticPowerFluid extends FlowingFluid implements IRenderLayerProvider {

	public Supplier<Item> Bucket;
	public Supplier<StaticPowerFluidBlock> FluidBlock;
	public Supplier<Source> StillFluid;
	public Supplier<Flowing> FlowingFluid;
	public Consumer<FluidAttributes.Builder> AdditionalAtrributesDelegate;
	public String StillTexture;
	public String FlowingTexture;
	public Named<Fluid> Tag;

	private Color fogColor;
	private Color overlayColor;

	public AbstractStaticPowerFluid(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock, Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture,
			String flowingTexture, Named<Fluid> tag, Color fogColor, Color overlayColor, Consumer<FluidAttributes.Builder> attributes) {
		setRegistryName(new ResourceLocation(StaticPower.MOD_ID, name));
		Bucket = bucket;
		FluidBlock = fluidBlock;
		StillFluid = stillFluid;
		FlowingFluid = flowingFluid;
		StillTexture = stillTexture;
		FlowingTexture = flowingTexture;
		this.fogColor = fogColor;
		this.overlayColor = overlayColor;
		Tag = tag;
		AdditionalAtrributesDelegate = attributes;
	}

	public Color getFogColor() {
		return fogColor;
	}

	public void setFogColor(Color fogColor) {
		this.fogColor = fogColor;
	}

	public Color getOverlayColor() {
		return overlayColor;
	}

	public void setOverlayColor(Color overlayColor) {
		this.overlayColor = overlayColor;
	}

	@Override
	public Fluid getFlowing() {
		return FlowingFluid.get();
	}

	@Override
	public Fluid getSource() {
		return StillFluid.get();
	}

	@Override
	public Item getBucket() {
		return Bucket.get();
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		BlockEntity tileentity = state.getBlock() instanceof EntityBlock ? worldIn.getBlockEntity(pos) : null;
		Block.dropResources(state, worldIn, pos, tileentity);
	}

	@Override
	protected int getSlopeFindDistance(LevelReader worldIn) {
		return 4;
	}

	@Override
	protected int getDropOff(LevelReader worldIn) {
		return 1;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
		return blockReader.getBlockState(pos).getBlock() == Blocks.AIR;
	}

	@Override
	public int getTickDelay(LevelReader reader) {
		return getAttributes().getViscosity() / 200;
	}

	@Override
	protected float getExplosionResistance() {
		return 100;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return FluidBlock.get().defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(state)));
	}

	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == getFlowing() || fluidIn == getSource();
	}

	@Override
	public void tick(Level worldIn, BlockPos pos, FluidState state) {
		// Check if we're gaseous.
		if (state.getType().getAttributes().isGaseous()) {
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

	@Override
	protected FluidAttributes createAttributes() {
		FluidAttributes.Builder attributes = FluidAttributes.builder(new ResourceLocation(StaticPower.MOD_ID, StillTexture), new ResourceLocation(StaticPower.MOD_ID, FlowingTexture))
				.translationKey(FluidBlock.get().getDescriptionId().replace("block", "fluid"));
		if (AdditionalAtrributesDelegate != null) {
			AdditionalAtrributesDelegate.accept(attributes);
		}
		return attributes.build(this);
	}

	public static class Source extends AbstractStaticPowerFluid {

		public Source(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock, Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture,
				String flowingTexture, Named<Fluid> tag, Color fogColor, Color overlayColor, Consumer<FluidAttributes.Builder> attributes) {
			super(name, bucket, fluidBlock, stillFluid, flowingFluid, stillTexture, flowingTexture, tag, fogColor, overlayColor, attributes);
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getAmount(FluidState p_207192_1_) {
			return 8;
		}
	}

	public static class Flowing extends AbstractStaticPowerFluid {

		public Flowing(String name, Supplier<Item> bucket, Supplier<StaticPowerFluidBlock> fluidBlock, Supplier<Source> stillFluid, Supplier<Flowing> flowingFluid, String stillTexture,
				String flowingTexture, Named<Fluid> tag, Color fogColor, Color overlayColor, Consumer<FluidAttributes.Builder> attributes) {
			super(name + "_flowing", bucket, fluidBlock, stillFluid, flowingFluid, stillTexture, flowingTexture, tag, fogColor, overlayColor, attributes);
		}

		@Override
		protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(AbstractStaticPowerFluid.LEVEL);
		}
	}
}
