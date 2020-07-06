package theking530.staticpower.client.rendering.blocks;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;
import theking530.staticpower.StaticPower;

public abstract class AbstractBakedModel implements IBakedModel {
	protected static final Logger LOGGER = LogManager.getLogger(AbstractBakedModel.class);
	protected static final Map<Direction, TransformationMatrix> SIDE_TRANSFORMS = new EnumMap<>(Direction.class);
	protected final HashSet<String> LoggedErrors = new HashSet<String>();
	protected final FaceBakery FaceBaker = new FaceBakery();
	protected final IBakedModel BaseModel;

	public AbstractBakedModel(IBakedModel baseModel) {
		BaseModel = baseModel;
	}

	@Override
	@Nonnull
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		return getBakedQuadsFromIModelData(state, side, rand, extraData);
	}

	protected abstract List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data);

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
	}

	protected List<BakedQuad> getTransformedQuads(IBakedModel model, Direction desiredRotation, Direction drawingSide, BlockState state, Random rand) {
		TransformationMatrix transformation = SIDE_TRANSFORMS.computeIfAbsent(desiredRotation, face -> {
			Quaternion quaternion;
			if (face == Direction.UP) {
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(90, 0, 0), true);
			} else if (face == Direction.DOWN) {
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(270, 0, 0), true);
			} else {
				double r = Math.PI * (360 - face.getOpposite().getHorizontalIndex() * 90) / 180d;

				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false);
			}

			return new TransformationMatrix(null, quaternion, null, null).blockCenterToCorner();
		});

		ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

		if (drawingSide != null && drawingSide.getHorizontalIndex() > -1) {
			int faceOffset = 4 + Direction.NORTH.getHorizontalIndex() - desiredRotation.getHorizontalIndex();
			drawingSide = Direction.byHorizontalIndex((drawingSide.getHorizontalIndex() + faceOffset) % 4);
		}

		// Build the output.
		if (model != null) {
			try {
				for (BakedQuad quad : model.getQuads(state, drawingSide, rand, EmptyModelData.INSTANCE)) {
					BakedQuadBuilder builder = new BakedQuadBuilder(quad.func_187508_a());
					TRSRTransformer transformer = new TRSRTransformer(builder, transformation);

					quad.pipe(transformer);

					quads.add(builder.build());
				}
			} catch (Exception e) {
				LOGGER.error(String.format("An error occured when attempting to rotate a model to face the desired rotation. Model: %1$s.", model), e);
			}
		}

		return quads.build();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return BaseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return BaseModel.isGui3d();
	}

	@Override
	public boolean func_230044_c_() {
		return BaseModel.func_230044_c_();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return BaseModel.isBuiltInRenderer();
	}

	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return BaseModel.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return BaseModel.getOverrides();
	}

	protected void conditionallyLogError(String log) {
		if (!LoggedErrors.contains(log)) {
			LoggedErrors.add(log);
			StaticPower.LOGGER.error(log);
		}
	}
}
