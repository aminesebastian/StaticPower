package theking530.staticpower.cables.fluid;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableBoundsCache;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.initialization.ModTileEntityTypes;

public class BlockIndustrialFluidCable extends AbstractCableBlock {

	public BlockIndustrialFluidCable(String name) {
		super(name, new CableBoundsCache(3.5D, new Vector3D(4.25f, 4.25f, 2.0f)));
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_EXTENSION);
		IBakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ATTACHMENT);
		IBakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_STRAIGHT);

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(world, pos);
		if (cable instanceof FluidCableComponent) {
			FluidStack fluid = ((FluidCableComponent) cable).getFluidInTank(0);
			if (!fluid.isEmpty()) {
				FluidAttributes attributes = fluid.getFluid().getAttributes();
				return attributes.getLuminosity(fluid);
			}
		}
		return state.getLightValue();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getTranslucent();
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntityTypes.INDUSTRIAL_FLUID_CABLE.create();
	}
}
