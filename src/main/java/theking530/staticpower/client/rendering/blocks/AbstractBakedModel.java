package theking530.staticpower.client.rendering.blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.StaticPower;

public abstract class AbstractBakedModel implements IBakedModel {
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
