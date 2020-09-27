package theking530.staticpower.client.rendering.blocks;

import java.util.Collections;
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
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;

public abstract class AbstractBakedModel implements IBakedModel {
	protected static final float UNIT = 1.0f / 16.0f;
	protected static final Logger LOGGER = LogManager.getLogger(AbstractBakedModel.class);
	protected static final Map<Direction, Quaternion> FACING_ROTATIONS = new EnumMap<Direction, Quaternion>(
			Direction.class);
	protected static final Map<Direction, TransformationMatrix> SIDE_TRANSFORMS = new EnumMap<>(Direction.class);

	protected final HashSet<String> LoggedErrors = new HashSet<String>();
	protected final FaceBakery FaceBaker = new FaceBakery();
	protected final IBakedModel BaseModel;

	static {
		for (Direction dir : Direction.values()) {
			Quaternion quaternion;
			if (dir == Direction.UP) {
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(90, 0, 0), true);
			} else if (dir == Direction.DOWN) {
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(270, 0, 0), true);
			} else {
				double r = Math.PI * (360 - dir.getOpposite().getHorizontalIndex() * 90) / 180d;
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false);
			}
			FACING_ROTATIONS.put(dir, quaternion);
			SIDE_TRANSFORMS.put(dir, new TransformationMatrix(null, quaternion, null, null).blockCenterToCorner());
		}
	}

	public AbstractBakedModel(IBakedModel baseModel) {
		BaseModel = baseModel;
	}

	@Override
	@Nonnull
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand,
			@Nonnull IModelData extraData) {
		return getBakedQuadsFromIModelData(state, side, rand, extraData);
	}

	protected abstract List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side,
			@Nonnull Random rand, @Nonnull IModelData data);

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
	}

	protected List<BakedQuad> rotateQuadsToFaceDirection(IBakedModel model, Direction desiredRotation,
			Direction drawingSide, BlockState state, Random rand) {
		TransformationMatrix transformation = SIDE_TRANSFORMS.get(desiredRotation);
		ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

		if (drawingSide != null && drawingSide.getHorizontalIndex() > -1) {
			int faceOffset = 4 + Direction.NORTH.getHorizontalIndex() - desiredRotation.getHorizontalIndex();
			drawingSide = Direction.byHorizontalIndex((drawingSide.getHorizontalIndex() + faceOffset) % 4);
		}

		// Build the output.
		if (model != null) {
			try {
				for (BakedQuad quad : model.getQuads(state, drawingSide, rand, EmptyModelData.INSTANCE)) {
					BakedQuadBuilder builder = new BakedQuadBuilder(quad.getSprite());
					TRSRTransformer transformer = new TRSRTransformer(builder, transformation);
					quad.pipe(transformer);
					quads.add(builder.build());
				}
			} catch (Exception e) {
				LOGGER.error(String.format(
						"An error occured when attempting to rotate a model to face the desired rotation. Model: %1$s.",
						model), e);
			}
		}

		return quads.build();
	}

	protected List<BakedQuad> transformQuads(IBakedModel model, Vector3f translation, Vector3f scale,
			Quaternion rotation, Direction drawingSide, BlockState state, Random rand) {
		// Build the output.
		if (model != null) {
			return transformQuads(model.getQuads(state, drawingSide, rand, EmptyModelData.INSTANCE), translation, scale,
					rotation);
		}

		return Collections.emptyList();
	}

	protected List<BakedQuad> transformQuads(List<BakedQuad> inQuads, Vector3f translation, Vector3f scale,
			Quaternion rotation) {
		ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

		TransformationMatrix transformation = new TransformationMatrix(translation, rotation, scale, null)
				.blockCenterToCorner();

		// Build the output.
		if (inQuads != null && inQuads.size() > 0) {
			try {
				for (BakedQuad quad : inQuads) {
					BakedQuadBuilder builder = new BakedQuadBuilder(quad.getSprite());
					TRSRTransformer transformer = new TRSRTransformer(builder, transformation);
					quad.pipe(transformer);
					quads.add(builder.build());
				}
			} catch (Exception e) {
				LOGGER.error("An error occured when attempting to translate the quads of a model.", e);
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
			LOGGER.error(log);
		}
	}
}
