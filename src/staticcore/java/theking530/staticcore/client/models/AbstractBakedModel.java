package theking530.staticcore.client.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;

public abstract class AbstractBakedModel implements BakedModel {
	protected static final float UNIT = 1.0f / 16.0f;
	protected static final Logger LOGGER = LogManager.getLogger(AbstractBakedModel.class);

	protected final HashSet<String> LoggedErrors = new HashSet<String>();
	protected final FaceBakery FaceBaker = new FaceBakery();
	protected final BakedModel BaseModel;

	public AbstractBakedModel(BakedModel baseModel) {
		this.BaseModel = baseModel;
	}

	@Override
	@Nonnull
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
		return getBakedQuadsFromModelData(state, side, rand, data, renderType);
	}

	protected abstract List<BakedQuad> getBakedQuadsFromModelData(@Nullable BlockState state, Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data,
			RenderType renderLayer);

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return getQuads(state, side, rand, ModelData.EMPTY, null);
	}

	protected List<BakedQuad> transformQuads(BakedModel model, Vector3f translation, Vector3f scale, Quaternion rotation, Direction drawingSide, BlockState state,
			RandomSource rand, @Nullable RenderType renderType) {
		// Build the output.
		if (model != null) {
			return transformQuads(model.getQuads(state, drawingSide, rand, ModelData.EMPTY, renderType), translation, scale, rotation);
		}

		return Collections.emptyList();
	}

	protected List<BakedQuad> transformQuads(List<BakedQuad> inQuads, Vector3f translation, Vector3f scale, Quaternion rotation) {
		ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

		Transformation transformation = new Transformation(translation, rotation, scale, null).blockCenterToCorner();

		// Build the output.
		if (inQuads != null && inQuads.size() > 0) {
			try {
				IQuadTransformer transformer = QuadTransformers.applying(transformation);
				for (BakedQuad quad : inQuads) {
					quads.add(transformer.process(quad));
				}
			} catch (Exception e) {
				LOGGER.error("An error occured when attempting to translate the quads of a model.", e);
			}
		}

		return quads.build();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return BaseModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return BaseModel.isGui3d();
	}

	@Override
	public boolean isCustomRenderer() {
		return BaseModel.isCustomRenderer();
	}

	@Override
	public boolean usesBlockLight() {
		return BaseModel.usesBlockLight();
	}

	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return BaseModel.getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return BaseModel.getOverrides();
	}

	protected void conditionallyLogError(String log) {
		if (!LoggedErrors.contains(log)) {
			LoggedErrors.add(log);
			LOGGER.error(log);
		}
	}
}
