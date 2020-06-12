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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableAttachmentState;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.network.CableBoundsCache;

public class CableBakedModel extends AbstractBakedModel {
	@SuppressWarnings("unused")
	private static final ModelProperty<CableBoundsCache> CABLE_STATE = new ModelProperty<>();
	private final IBakedModel Extension;
	@SuppressWarnings("unused")
	private final IBakedModel Straight;
	private final IBakedModel Attachment;

	public CableBakedModel(IBakedModel coreModel, IBakedModel extensionModel, IBakedModel straightModel, IBakedModel attachmentModel) {
		super(coreModel);
		Extension = extensionModel;
		Straight = straightModel;
		Attachment = attachmentModel;
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		ModelDataMap modelDataMap = getEmptyIModelData();
		return modelDataMap;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// Build the proper quad array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		newQuads.addAll(BaseModel.getQuads(state, side, rand, data));

		for (Direction dir : Direction.values()) {
			if (CableUtilities.getConnectionState(state, dir) == CableAttachmentState.CABLE) {
				newQuads.addAll(getTransformedQuads(Extension, dir, side, state, rand));
			} else if (CableUtilities.getConnectionState(state, dir) == CableAttachmentState.TILE_ENTITY) {
				newQuads.addAll(getTransformedQuads(Extension, dir, side, state, rand));
				newQuads.addAll(getTransformedQuads(Attachment, dir, side, state, rand));
			}
		}
		return newQuads.build();
	}

	protected ModelDataMap getEmptyIModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		ModelDataMap modelDataMap = builder.build();
		return modelDataMap;
	}
}
