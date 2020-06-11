package theking530.staticpower.client.rendering.blocks;

import java.util.List;
import java.util.Optional;
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
import theking530.staticpower.tileentities.network.CableStateWrapper;
import theking530.staticpower.tileentities.network.CableStateWrapper.CableConnectionState;

public class CableBakedModel extends AbstractBakedModel {
	private static final ModelProperty<CableStateWrapper> CABLE_STATE = new ModelProperty<>();
	private final IBakedModel Extension;
	private final IBakedModel Straight;

	public CableBakedModel(IBakedModel coreModel, IBakedModel extensionModel, IBakedModel straightModel) {
		super(coreModel);
		Extension = extensionModel;
		Straight = straightModel;
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		ModelDataMap modelDataMap = getEmptyIModelData();
		modelDataMap.setData(CABLE_STATE, new CableStateWrapper(state));
		return modelDataMap;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// Check if the data has the ATTACHED_SIDES property. If not, something has gone
		// wrong. Return just the base model.
		if (!data.hasProperty(CABLE_STATE)) {
			conditionallyLogError("Encountered invalid cable state when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}

		// Get the connection state.
		CableStateWrapper cableStateOptional = data.getData(CABLE_STATE);

		// Build the proper quad array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		newQuads.addAll(BaseModel.getQuads(state, side, rand, data));

		for (Direction dir : Direction.values()) {
			if (cableStateOptional.getConnectionState(dir) != CableConnectionState.EMPTY) {
				newQuads.addAll(getTransformedQuads(Extension, dir, side, state, rand));
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
