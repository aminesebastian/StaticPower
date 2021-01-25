package theking530.staticpower.client.rendering.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import theking530.staticpower.tileentities.nonpowered.conveyors.straight.BlockStraightConveyor;

@OnlyIn(Dist.CLIENT)
public class ConveyorStraightModel extends AbstractBakedModel {
	public ConveyorStraightModel(IBakedModel baseModel) {
		super(baseModel);
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		return tileData;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// Get the base quads.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);

		// If this a top conveyor, translate it up.
		if (state != null && state.hasProperty(BlockStraightConveyor.IS_TOP) && state.get(BlockStraightConveyor.IS_TOP)) {
			baseQuads = this.transformQuads(baseQuads, new Vector3f(0, (1 / 16.0f) * 12, 0), new Vector3f(1, 1, 1), Quaternion.ONE);
		}

		return baseQuads;
	}

	protected ModelDataMap getEmptyIModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		ModelDataMap modelDataMap = builder.build();
		return modelDataMap;
	}

	@Override
	public boolean isSideLit() {
		return BaseModel.isSideLit();
	}
}
