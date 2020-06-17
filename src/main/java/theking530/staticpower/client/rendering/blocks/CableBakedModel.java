package theking530.staticpower.client.rendering.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.cables.CableUtilities;

public class CableBakedModel extends AbstractBakedModel {
	public static final Logger LOGGER = LogManager.getLogger(AbstractBakedModel.class);
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
	@Nonnull
	public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			return te.getModelData();
		}
		return new ModelDataMap.Builder().build();
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// If we're missing a property, just return the default core model.
		if (!data.hasProperty(AbstractCableProviderComponent.DISABLED_CABLE_SIDES) || !data.hasProperty(AbstractCableProviderComponent.CABLE_CONNECTION_STATES)
				|| !data.hasProperty(AbstractCableProviderComponent.CABLE_ATTACHMENT_MODELS)) {
			LOGGER.error("Cable is missing one of the required model data properties.");
			return BaseModel.getQuads(state, side, rand, data);
		}

		// Build the proper quad array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		// Get the model properties.
		boolean[] disabledSides = data.getData(AbstractCableProviderComponent.DISABLED_CABLE_SIDES);
		CableConnectionState[] cableConnectionStates = data.getData(AbstractCableProviderComponent.CABLE_CONNECTION_STATES);
		ResourceLocation[] attachmentModels = data.getData(AbstractCableProviderComponent.CABLE_ATTACHMENT_MODELS);

		// If we have a simple straight connection, just add that mode. Otherwise, add
		// the core and then apply any additional models.
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
				} else if (connectionState == CableConnectionState.TILE_ENTITY || attachmentModels[dir.ordinal()] != null) {
					newQuads.addAll(getTransformedQuads(Extension, dir, side, state, rand));
					if (attachmentModels[dir.ordinal()] != null) {
						IBakedModel model = Minecraft.getInstance().getModelManager().getModel(attachmentModels[dir.ordinal()]);
						newQuads.addAll(getTransformedQuads(model, dir, side, state, rand));
					} else {
						newQuads.addAll(getTransformedQuads(Attachment, dir, side, state, rand));
					}
				}
			}
		}

		return newQuads.build();
	}
}
