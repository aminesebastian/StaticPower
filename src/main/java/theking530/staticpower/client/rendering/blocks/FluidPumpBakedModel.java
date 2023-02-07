package theking530.staticpower.client.rendering.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.machines.fluid_pump.BlockEntityFluidPump;
import theking530.staticpower.blockentities.machines.fluid_pump.BlockEntityFluidPump.FluidPumpRenderingState;
import theking530.staticpower.client.StaticPowerAdditionalModels;

@OnlyIn(Dist.CLIENT)
public class FluidPumpBakedModel extends DefaultMachineBakedModel {

	public FluidPumpBakedModel(BakedModel baseModel) {
		super(baseModel, true);
	}

	@Override
	@Nonnull
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		return super.getModelData(world, pos, state, tileData);
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromModelData(@Nullable BlockState state, Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, RenderType renderLayer) {
		// If the property is not there, return early.
		if (!data.has(BlockEntityFluidPump.PUMP_RENDERING_STATE)) {
			return Collections.emptyList();
		}

		// Get the data used in rendering.
		FluidPumpRenderingState renderingState = data.get(BlockEntityFluidPump.PUMP_RENDERING_STATE);
		BakedModel powerConnector = Minecraft.getInstance().getModelManager().getModel(StaticPowerAdditionalModels.FLUID_PUMP_POWER_CONNECTOR);

		// Create the output array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		// Add all the base quads for the server rack to the output.
		List<BakedQuad> baseQuads = super.getBakedQuadsFromModelData(state, side, rand, data, renderLayer);
		newQuads.addAll(baseQuads);

		for (Direction dir : Direction.values()) {
			if (!renderingState.hasPowerConnectedStatus(dir)) {
				continue;
			}

			Vector3f offset = new Vector3f(0, 0, 0);
			Vector3f scale = new Vector3f(1, 1, 1);
			List<BakedQuad> bakedBulbQuads = transformQuads(powerConnector, offset, scale, dir.getRotation(), side, state, rand, renderLayer);
			newQuads.addAll(bakedBulbQuads);
		}

		return newQuads.build();
	}

	@Override
	protected void renderSideMode(@Nullable BlockState state, Builder<BakedQuad> newQuads, Direction side, BlockSide blockSide, MachineSideMode sideMode, ModelData data) {
		if (blockSide == BlockSide.FRONT || blockSide == BlockSide.BACK) {
			super.renderSideMode(state, newQuads, side, blockSide, sideMode, data);
			return;
		}

		// If the property is not there, return early.
		if (!data.has(BlockEntityFluidPump.PUMP_RENDERING_STATE)) {
			return;
		}

		FluidPumpRenderingState renderingState = data.get(BlockEntityFluidPump.PUMP_RENDERING_STATE);
		if (!renderingState.hasPowerConnectedStatus(side)) {
			return;
		}

		// Only render the side mode if there is power connected or if it's the front or
		// back.
		super.renderSideMode(state, newQuads, side, blockSide, sideMode, data);
	}
}
