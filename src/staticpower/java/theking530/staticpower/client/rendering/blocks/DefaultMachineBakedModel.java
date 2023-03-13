package theking530.staticpower.client.rendering.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.client.rendering.utilities.SideModeQuadGenerator;

@OnlyIn(Dist.CLIENT)
public class DefaultMachineBakedModel extends AbstractBakedModel {
	private static final Logger LOGGER = LogManager.getLogger(DefaultMachineBakedModel.class);
	private final HashMap<Direction, Boolean> sideConfigurationRenderControl;
	private final HashMap<BlockSide, Vector3f> sideOffsets;
	private final boolean useMiniSideModeQuads;

	public DefaultMachineBakedModel(BakedModel baseModel) {
		this(baseModel, false);
	}

	public DefaultMachineBakedModel(BakedModel baseModel, boolean useMiniSideModeQuads) {
		super(baseModel);
		this.useMiniSideModeQuads = useMiniSideModeQuads;
		sideConfigurationRenderControl = new HashMap<Direction, Boolean>();
		sideOffsets = new HashMap<BlockSide, Vector3f>();

		// Populate with defaults.
		for (Direction dir : Direction.values()) {
			sideConfigurationRenderControl.put(dir, true);
		}
		sideConfigurationRenderControl.put(null, true);
	}

	/**
	 * Set a side offset for any side i/o rendering.
	 * 
	 * @param side   The block side of the block to modify.
	 * @param offset The offset to apply RELATIVE TO THE CENTER OF THE BLOCK.
	 *               Example: A negative value will bring the side render CLOSER to
	 *               the center of the block.
	 * @return
	 */
	public DefaultMachineBakedModel setSideOffset(BlockSide side, Vector3f offset) {
		sideOffsets.put(side, offset);
		return this;
	}

	public DefaultMachineBakedModel setSideConfigVisiblity(Direction side, boolean visible) {
		sideConfigurationRenderControl.put(side, visible);
		return this;
	}

	@Override
	@Nonnull
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		return tileData;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected List<BakedQuad> getBakedQuadsFromModelData(@Nullable BlockState state, Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, RenderType renderLayer) {
		// If we didn't get a side configuration, skip this block and just return the
		// defaults.
		if (!data.has(SideConfigurationComponent.SIDE_CONFIG)) {
			conditionallyLogError("Encountered no side configuration data when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}

		// Attempt to get the side configuration.
		MachineSideMode[] sideConfigurations = data.get(SideConfigurationComponent.SIDE_CONFIG);

		// Capture the default quads and retexture the sides to match the desired side
		// textures based on the configuration.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data, renderLayer);
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();
		newQuads.addAll(baseQuads);

		// Get the block atlas texture.
		try {
			if (side != null) {
				MachineSideMode sideMode = sideConfigurations[side.ordinal()];
				Direction machineFacing = state.hasProperty(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING)
						? state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING)
						: state.getValue(StaticPowerRotateableBlockEntityBlock.FACING);
				BlockSide blockSide = SideConfigurationUtilities.getBlockSide(side, machineFacing);
				renderSideMode(state, newQuads, side, blockSide, sideMode, data);
			}
		} catch (Exception e) {
			LOGGER.warn("An error occured when attempting to render the model.", e);
		}

		// Build and return the new quad list.
		return newQuads.build();
	}

	protected void renderSideMode(@Nullable BlockState state, Builder<BakedQuad> newQuads, Direction side, BlockSide blockSide, MachineSideMode sideMode, ModelData data) {
		if (useMiniSideModeQuads) {
			SideModeQuadGenerator.renderMiniSideModeQuad(state, newQuads, side, sideMode, sideOffsets.containsKey(blockSide) ? sideOffsets.get(blockSide) : null);
		} else {
			SideModeQuadGenerator.renderSideModeQuad(state, newQuads, side, sideMode, sideOffsets.containsKey(blockSide) ? sideOffsets.get(blockSide) : null);
		}
	}

	protected Optional<MachineSideMode[]> getSideConfigurations(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos blockPos) {
		if (!world.getBlockState(blockPos).hasBlockEntity()) {
			return Optional.empty();
		}

		BlockEntity rawTileEntity = world.getBlockEntity(blockPos);

		if (rawTileEntity != null && rawTileEntity instanceof BlockEntityBase) {
			BlockEntityBase configurable = (BlockEntityBase) rawTileEntity;
			if (configurable.hasComponentOfType(SideConfigurationComponent.class)) {
				return Optional.of(configurable.getComponent(SideConfigurationComponent.class).getWorldSpaceConfiguration());
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean usesBlockLight() {
		return BaseModel.usesBlockLight();
	}
}
