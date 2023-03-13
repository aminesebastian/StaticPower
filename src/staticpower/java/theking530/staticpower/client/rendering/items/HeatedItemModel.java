package theking530.staticpower.client.rendering.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class HeatedItemModel implements BakedModel {
	private final BakedModel baseModel;

	public HeatedItemModel(BakedModel emptyDrillModel) {
		this.baseModel = emptyDrillModel;
	}

	private BakedModel getBaseModel() {
		return baseModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {

				return baseModel;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return getBaseModel().getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return getBaseModel().useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return getBaseModel().isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return getBaseModel().usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return getBaseModel().isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return getBaseModel().getParticleIcon();
	}
}
