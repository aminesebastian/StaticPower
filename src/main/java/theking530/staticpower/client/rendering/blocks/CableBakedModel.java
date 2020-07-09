package theking530.staticpower.client.rendering.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableRenderingState;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.client.rendering.CoverBuilder;

public class CableBakedModel extends AbstractBakedModel {
	public static final Logger LOGGER = LogManager.getLogger(AbstractBakedModel.class);
	private final IBakedModel Extension;
	private final IBakedModel Straight;
	private final IBakedModel Attachment;
	private final CoverBuilder coverBuilder;

	public CableBakedModel(IBakedModel coreModel, IBakedModel extensionModel, IBakedModel straightModel, IBakedModel attachmentModel) {
		super(coreModel);
		Extension = extensionModel;
		Straight = straightModel;
		Attachment = attachmentModel;
		coverBuilder = new CoverBuilder();
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
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

		for (Direction dir : Direction.values()) {
			if (renderingState.covers[dir.ordinal()] != null) {
				// newQuads.addAll(getTransformedQuads(FullCover, dir, side, state, rand));
			}
		}

		RenderType layer = MinecraftForgeClient.getRenderLayer();

		if (side != null && renderingState.covers[side.ordinal()] != null) {
			coverBuilder.buildFacadeQuads(renderingState, layer, rand, newQuads, side);
		}

		// If we have a simple straight connection, just add that mode. Otherwise, add
		// the core and then apply any additional models.
		if (Straight != null && CableUtilities.isCableStraightConnection(renderingState.connectionStates)) {
			newQuads.addAll(rotateQuadsToFaceDirection(Straight, CableUtilities.getStraightConnectionSide(renderingState.connectionStates), side, state, rand));
			for (Direction dir : Direction.values()) {
				if (renderingState.attachments[dir.ordinal()] != null) {
					IBakedModel model = Minecraft.getInstance().getModelManager().getModel(renderingState.attachments[dir.ordinal()]);
					newQuads.addAll(rotateQuadsToFaceDirection(model, dir, side, state, rand));
					newQuads.addAll(rotateQuadsToFaceDirection(Extension, dir, side, state, rand));
				}
			}
		} else {
			newQuads.addAll(BaseModel.getQuads(state, side, rand, data));

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
					newQuads.addAll(rotateQuadsToFaceDirection(Extension, dir, side, state, rand));
					if (renderingState.attachments[dir.ordinal()] != null) {
						IBakedModel model = Minecraft.getInstance().getModelManager().getModel(renderingState.attachments[dir.ordinal()]);
						newQuads.addAll(rotateQuadsToFaceDirection(model, dir, side, state, rand));
					} else {
						newQuads.addAll(rotateQuadsToFaceDirection(Attachment, dir, side, state, rand));
					}
				}
			}
		}

		return newQuads;
	}
}
