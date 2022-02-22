package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.client.rendering.CoverBuilder;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class CoverItemModelProvider implements BakedModel {
	private final CoverBuilder coverBuilder;
	private final Int2ObjectMap<CoverItemModel> cache = new Int2ObjectArrayMap<>();
	private final BakedModel baseModel;

	public CoverItemModelProvider(BakedModel baseModel, CoverBuilder coverBuilder) {
		this.baseModel = baseModel;
		this.coverBuilder = coverBuilder;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world,
					@Nullable LivingEntity livingEntity, int x) {
				// If this is not a CableCover item or it has no tag, return the original
				// moodel.
				if (!(stack.getItem() instanceof CableCover) || !stack.hasTag()) {
					return originalModel;
				}

				// Get the itemstack representing the block this cover contains.
				ItemStack coverItemStack = new ItemStack(CableCover.getBlockStateForCover(stack).getBlock());

				int hash = Objects.hash(coverItemStack.getItem().getRegistryName(), coverItemStack.getTag());
				CoverItemModel model = CoverItemModelProvider.this.cache.get(hash);
				if (model == null) {
					model = new CoverItemModel(CoverItemModelProvider.this.baseModel, coverItemStack,
							CoverItemModelProvider.this.coverBuilder);
					CoverItemModelProvider.this.cache.put(hash, model);
				}

				return model;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return baseModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return baseModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return baseModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return baseModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return baseModel.getParticleIcon();
	}

	private class CoverItemModel extends AbstractBakedModel {
		private final ItemStack textureStack;
		private final CoverBuilder coverBuilder;
		private List<BakedQuad> quads = null;

		protected CoverItemModel(BakedModel baseModel, ItemStack textureStack, CoverBuilder facadeBuilder) {
			super(baseModel);
			this.textureStack = textureStack;
			this.coverBuilder = facadeBuilder;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
			return getQuads(state, side, rand, EmptyModelData.INSTANCE);
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromIModelData(BlockState state, Direction side, Random rand,
				IModelData data) {
			if (side != null) {
				return Collections.emptyList();
			}
			if (quads == null) {
				quads = new ArrayList<>();
				quads.addAll(this.coverBuilder.buildFacadeItemQuads(this.textureStack, Direction.NORTH));
				quads = Collections.unmodifiableList(quads);
			}
			return quads;
		}

		@Override
		public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
			BaseModel.handlePerspective(cameraTransformType, mat);
			return this;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean usesBlockLight() {
			return BaseModel.usesBlockLight();
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return null;
		}

	}
}
