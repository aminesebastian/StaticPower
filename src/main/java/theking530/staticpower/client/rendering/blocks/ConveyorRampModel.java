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
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;

@OnlyIn(Dist.CLIENT)
public class ConveyorRampModel extends AbstractBakedModel {
	public final boolean rampUp;

	public ConveyorRampModel(IBakedModel baseModel, boolean rampUp) {
		super(baseModel);
		this.rampUp = rampUp;
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

		// Get the facing direction.
		Direction conveyorFacing = Direction.NORTH;
		if (state != null && state.hasProperty(StaticPowerTileEntityBlock.FACING)) {
			conveyorFacing = state.get(StaticPowerTileEntityBlock.FACING);
		}

		// Calculate the rotation.
		Quaternion rotation = new Quaternion(35, 0, 0, true);

		return transformQuads(baseQuads, new Vector3f(0, 0.33f, 0.18f), new Vector3f(1, 1, 1.3f), rotation);
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
