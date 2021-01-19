package theking530.staticpower.client.rendering.items;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class ItemCustomRendererPassthroughModel implements IBakedModel {
	private final IBakedModel baseModel;

	public ItemCustomRendererPassthroughModel(IBakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrideList getOverrides() {
		ItemCustomRendererPassthroughModel original = this;
		return new ItemOverrideList() {
			@Override
			public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
				return original;
			}
		};
	}

	public IBakedModel getBaseModel() {
		return baseModel;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return baseModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean isSideLit() {
		return baseModel.isSideLit();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseModel.getParticleTexture();
	}
}
