package theking530.staticpower.client.rendering.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticcore.cablenetwork.CableRenderingState;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.cablenetwork.data.CableSideConnectionState.CableConnectionType;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.client.rendering.CoverBuilder;
import theking530.staticpower.client.rendering.utilities.QuadUtilities;

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
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		// If we're missing a property, just return the default core model.
		if (tileData.has(AbstractCableProviderComponent.CABLE_RENDERING_STATE)) {
			// Get the model properties and set the rendering world.
			CableRenderingState renderingState = tileData.get(AbstractCableProviderComponent.CABLE_RENDERING_STATE);
			renderingState.setRenderingLevel(world);
		} else {
			LOGGER.error("Cable is missing one of the required model data properties.");
		}
		return tileData;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromModelData(@Nullable BlockState state, Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, RenderType renderLayer) {
		// If we're missing a property, just return the default core model.
		if (!data.has(AbstractCableProviderComponent.CABLE_RENDERING_STATE)) {
			LOGGER.error("Cable is missing one of the required model data properties.");
			return BaseModel.getQuads(state, side, rand, data, renderLayer);
		}

		// Build the proper quad array.
		List<BakedQuad> newQuads = new ArrayList<BakedQuad>();

		// Get the model properties.
		CableRenderingState renderingState = data.get(AbstractCableProviderComponent.CABLE_RENDERING_STATE);
		if (renderingState == null) {
			return Collections.emptyList();
		}

		// Render the covers when we're on the NULL render side. Reason for this is, as
		// much as we lose some render optimization, if we don't do this, chests placed
		// on a cover will stop rendering the cover.
		if (side == null) {
			for (Direction dir : Direction.values()) {
				if (renderingState.hasCover(dir)) {
					coverBuilder.buildFacadeQuads(state, renderingState, renderLayer, rand, newQuads, dir);
				}
			}
		}

		// If we have a simple straight connection, just add that mode. Otherwise, add
		// the core and then apply any additional models.
		if (Straight != null && CableUtilities.isCableStraightConnection(renderingState)) {
			newQuads.addAll(QuadUtilities.rotateQuadsToFaceDirection(Straight, CableUtilities.getStraightConnectionSide(renderingState), side, state, rand));
			for (Direction dir : Direction.values()) {
				if (renderingState.hasAttachment(dir)) {
					BakedModel model = Minecraft.getInstance().getModelManager().getModel(renderingState.getAttachmentModelId(dir));
					newQuads.addAll(QuadUtilities.rotateQuadsToFaceDirection(model, dir, side, state, rand));
					newQuads.addAll(QuadUtilities.rotateQuadsToFaceDirection(Extension, dir, side, state, rand));
				}
			}
		} else {
			// Add the core.
			newQuads.addAll(BaseModel.getQuads(state, side, rand, data, renderLayer));

			// Add the attachments and connecting pieces.
			for (Direction dir : Direction.values()) {
				// If a side is disabled, skip it.
				if (renderingState.isDisabledOnSide(dir)) {
					continue;
				}

				// Get the connection state.
				CableConnectionType connectionState = renderingState.getConnectionType(dir);

				// Decide what to render based on the connection state.
				if (connectionState == CableConnectionType.CABLE) {
					newQuads.addAll(QuadUtilities.rotateQuadsToFaceDirection(Extension, dir, side, state, rand));
				} else if (connectionState == CableConnectionType.TILE_ENTITY || renderingState.hasAttachment(dir)) {
					// Rotate and render the extension model to the entity or attachment.
					newQuads.addAll(QuadUtilities.rotateQuadsToFaceDirection(Extension, dir, side, state, rand));

					// If there is an actual attachment, render that. Otherwise, just render the
					// default attachment for this cable.
					if (renderingState.hasAttachment(dir)) {
						BakedModel model = Minecraft.getInstance().getModelManager().getModel(renderingState.getAttachmentModelId(dir));
						newQuads.addAll(QuadUtilities.rotateQuadsToFaceDirection(model, dir, side, state, rand));
					} else {
						newQuads.addAll(QuadUtilities.rotateQuadsToFaceDirection(Attachment, dir, side, state, rand));
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
