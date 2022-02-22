package theking530.staticpower.client.rendering.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableRenderingState;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.client.rendering.CoverBuilder;

@OnlyIn(Dist.CLIENT)
public class CableBakedModel extends AbstractBakedModel {
	public static final Logger LOGGER = LogManager.getLogger(AbstractBakedModel.class);
	private final BakedModel Extension;
	private final BakedModel Straight;
	private final BakedModel Attachment;
	private final CoverBuilder coverBuilder;

	public CableBakedModel(BakedModel coreModel, BakedModel extensionModel, BakedModel straightModel, BakedModel attachmentModel) {
		super(coreModel);
		Extension = extensionModel;
		Straight = straightModel;
		Attachment = attachmentModel;
		coverBuilder = new CoverBuilder();
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		// If we're missing a property, just return the default core model.
		if (tileData.hasProperty(AbstractCableProviderComponent.CABLE_RENDERING_STATE)) {
			// Get the model properties and set the rendering world.
			CableRenderingState renderingState = tileData.getData(AbstractCableProviderComponent.CABLE_RENDERING_STATE);
			renderingState.setRenderingWorld(world);
		} else {
			LOGGER.error("Cable is missing one of the required model data properties.");
		}
		return tileData;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// If we're missing a property, just return the default core model.
		if (!data.hasProperty(AbstractCableProviderComponent.CABLE_RENDERING_STATE)) {
			LOGGER.error("Cable is missing one of the required model data properties.");
			return BaseModel.getQuads(state, side, rand, data);
		}

		// Build the proper quad array.
		List<BakedQuad> newQuads = new ArrayList<BakedQuad>();

		// Get the model properties.
		CableRenderingState renderingState = data.getData(AbstractCableProviderComponent.CABLE_RENDERING_STATE);
		if (renderingState == null) {
			return Collections.emptyList();
		}

		// Render the covers when we're on the NULL render side. Reason for this is, as
		// much as we lose some render optimization, if we don't do this, chests placed
		// on a cover will stop rendering the cover.
		RenderType layer = MinecraftForgeClient.getRenderType();
		if (side == null) {
			for (Direction dir : Direction.values()) {
				if (renderingState.covers[dir.ordinal()] != null) {
					coverBuilder.buildFacadeQuads(state, renderingState, layer, rand, newQuads, dir);
				}
			}
		}

		// If we have a simple straight connection, just add that mode. Otherwise, add
		// the core and then apply any additional models.
		if (Straight != null && CableUtilities.isCableStraightConnection(renderingState.connectionStates)) {
			newQuads.addAll(rotateQuadsToFaceDirection(Straight, CableUtilities.getStraightConnectionSide(renderingState.connectionStates), side, state, rand));
			for (Direction dir : Direction.values()) {
				if (renderingState.attachments[dir.ordinal()] != null) {
					BakedModel model = Minecraft.getInstance().getModelManager().getModel(renderingState.attachments[dir.ordinal()]);
					newQuads.addAll(rotateQuadsToFaceDirection(model, dir, side, state, rand));
					newQuads.addAll(rotateQuadsToFaceDirection(Extension, dir, side, state, rand));
				}
			}
		} else {
			// Add the core.
			newQuads.addAll(BaseModel.getQuads(state, side, rand, data));

			// Add the attachments and connecting pieces.
			for (Direction dir : Direction.values()) {
				// If a side is disabled, skip it.
				if (renderingState.disabledSides[dir.ordinal()]) {
					continue;
				}

				// Get the connection state.
				CableConnectionState connectionState = renderingState.connectionStates[dir.ordinal()];

				// Decide what to render based on the connection state.
				if (connectionState == CableConnectionState.CABLE) {
					newQuads.addAll(rotateQuadsToFaceDirection(Extension, dir, side, state, rand));
				} else if (connectionState == CableConnectionState.TILE_ENTITY || renderingState.attachments[dir.ordinal()] != null) {
					// Rotate and render the extension model to the entity or attachment.
					newQuads.addAll(rotateQuadsToFaceDirection(Extension, dir, side, state, rand));

					// If there is an actual attachment, render that. Otherwise, just render the
					// default attachment for this cable.
					if (renderingState.attachments[dir.ordinal()] != null) {
						BakedModel model = Minecraft.getInstance().getModelManager().getModel(renderingState.attachments[dir.ordinal()]);
						newQuads.addAll(rotateQuadsToFaceDirection(model, dir, side, state, rand));
					} else {
						newQuads.addAll(rotateQuadsToFaceDirection(Attachment, dir, side, state, rand));
					}
				}
			}
		}

		return newQuads;
	}

	@Override
	public boolean usesBlockLight() {
		return this.BaseModel.usesBlockLight();
	}
}
