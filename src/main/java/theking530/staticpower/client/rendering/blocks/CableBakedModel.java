package theking530.staticpower.client.rendering.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.cables.CableUtilities;

public class CableBakedModel extends AbstractBakedModel {
	private final IBakedModel Extension;
	private final IBakedModel Straight;
	private final IBakedModel Attachment;

	public CableBakedModel(IBakedModel coreModel, IBakedModel extensionModel, IBakedModel straightModel, IBakedModel attachmentModel) {
		super(coreModel);
		Extension = extensionModel;
		Straight = straightModel;
		Attachment = attachmentModel;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// Build the proper quad array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		boolean[] disabledSides = data.getData(AbstractCableProviderComponent.DISABLED_CABLE_SIDES);
		CableConnectionState[] cableConnectionStates = data.getData(AbstractCableProviderComponent.CABLE_CONNECTION_STATES);

		if (CableUtilities.isCableStraightConnection(cableConnectionStates)) {
			newQuads.addAll(getTransformedQuads(Straight, CableUtilities.getStraightConnectionSide(cableConnectionStates), side, state, rand));
		} else {
			newQuads.addAll(BaseModel.getQuads(state, side, rand, data));
			
			for (Direction dir : Direction.values()) {
				// If a side is disabled, skip it.
				if (disabledSides[dir.ordinal()]) {
					continue;
				}

				// Get the connection state.
				CableConnectionState connectionState = cableConnectionStates[dir.ordinal()];

				// Decide what to render based on the connection state.
				if (connectionState == CableConnectionState.CABLE) {
					newQuads.addAll(getTransformedQuads(Extension, dir, side, state, rand));
				} else if (connectionState == CableConnectionState.TILE_ENTITY) {
					newQuads.addAll(getTransformedQuads(Extension, dir, side, state, rand));
					newQuads.addAll(getTransformedQuads(Attachment, dir, side, state, rand));
				}
			}
		}

		return newQuads.build();
	}
}
