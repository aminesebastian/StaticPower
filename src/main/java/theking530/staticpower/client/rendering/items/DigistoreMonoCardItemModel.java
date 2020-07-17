package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.IDigistoreInventory;

@SuppressWarnings("deprecation")
public class DigistoreMonoCardItemModel implements IBakedModel {
	private final Int2ObjectMap<DigistoreMonoCardModel> cache = new Int2ObjectArrayMap<>();
	private final IBakedModel baseModel;

	public DigistoreMonoCardItemModel(IBakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
				if (!(stack.getItem() instanceof DigistoreMonoCard)) {
					return originalModel;
				}

				IDigistoreInventory inv = DigistoreCard.getInventory(stack);
				float ratio = (float) inv.getTotalContainedCount() / inv.getItemCapacity();
				int intRatio = (int) (ratio * 20);

				int hash = Objects.hash(stack.getItem().getRegistryName(), intRatio);
				DigistoreMonoCardModel model = DigistoreMonoCardItemModel.this.cache.get(hash);
				if (model == null) {
					model = new DigistoreMonoCardModel(baseModel, ratio);
					DigistoreMonoCardItemModel.this.cache.put(hash, model);
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
	public boolean isAmbientOcclusion() {
		return baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean func_230044_c_() {
		return baseModel.func_230044_c_();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return baseModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseModel.getParticleTexture();
	}

	private class DigistoreMonoCardModel extends AbstractBakedModel {
		private final float filledRatio;
		private final IBakedModel baseModel;
		private List<BakedQuad> quads = null;

		protected DigistoreMonoCardModel(IBakedModel baseModel, float filledRatio) {
			super(baseModel);
			this.filledRatio = filledRatio;
			this.baseModel = baseModel;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
			return getQuads(state, side, rand, EmptyModelData.INSTANCE);
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromIModelData(BlockState state, Direction side, Random rand, IModelData data) {
			if (side != null) {
				return Collections.emptyList();
			}

			if (quads == null) {
				quads = new ArrayList<BakedQuad>();
				quads.addAll(baseModel.getQuads(state, side, rand, data));

				AtlasTexture blocksTexture = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
				TextureAtlasSprite sideSprite;

				if (filledRatio < 1.0f) {
					sideSprite = blocksTexture.getSprite(StaticPowerSprites.DIGISTORE_FILL_BAR);
				} else {
					sideSprite = blocksTexture.getSprite(StaticPowerSprites.DIGISTORE_FILL_BAR_FULL);
				}

				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), blockFaceUV);
				BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(3.5f, 4.0f, 0.0f), new Vector3f(3.75f + (filledRatio * 6.0f), 5.3f, 16.0f), blockPartFace, sideSprite, Direction.SOUTH, SimpleModelTransform.IDENTITY, null, false,
						new ResourceLocation("dummy_name"));
				quads.add(newQuad);
			}
			return quads;
		}

		@Override
		public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
			BaseModel.handlePerspective(cameraTransformType, mat);
			return this;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean func_230044_c_() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.EMPTY;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return null;
		}

	}
}
