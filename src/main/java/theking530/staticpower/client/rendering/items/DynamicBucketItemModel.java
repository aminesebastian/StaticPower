package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.items.StaticPowerFluidBucket;
import theking530.staticpower.utilities.ModelUtilities;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class DynamicBucketItemModel implements BakedModel {
	private final Int2ObjectMap<DynamicBucketModel> cache = new Int2ObjectArrayMap<>();
	private final BakedModel baseModel;

	public DynamicBucketItemModel(BakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				// Make sure we have a valid fluid capsule.
				if (!(stack.getItem() instanceof StaticPowerFluidBucket)) {
					return originalModel;
				}

				// Get the fluid handler.
				IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);
				if (handler == null) {
					return originalModel;
				}

				// Hash the unique info about this model.
				int hash = Objects.hash(stack.getItem().getRegistryName());

				// Check to see if we need to cache this model, if we do, do it.
				DynamicBucketModel model = DynamicBucketItemModel.this.cache.get(hash);
				if (model == null) {
					model = new DynamicBucketModel(baseModel);
					DynamicBucketItemModel.this.cache.put(hash, model);
				}
				model = new DynamicBucketModel(baseModel);
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
	public boolean doesHandlePerspectives() {
		return false;
	}

	@Override
	public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack) {
		return baseModel.handlePerspective(cameraTransformType, poseStack);
	}

	@Override
	public @Nonnull IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		return baseModel.getModelData(world, pos, state, tileData);
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

	private class DynamicBucketModel extends AbstractBakedModel {
		private final BakedModel baseModel;
		private List<BakedQuad> quads = null;

		protected DynamicBucketModel(BakedModel baseModel) {
			super(baseModel);
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
				DynamicBucketModel model = new DynamicBucketModel();
				
				quads = new ArrayList<BakedQuad>();
				quads.addAll(baseModel.getQuads(state, side, rand, data));

				// Draw the fluid.
				TextureAtlasSprite sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.CONCRETE_BUCKET_TOP_LAYER);
				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockElementFace blockPartFace = new BlockElementFace(null, 2, sideSprite.getName().toString(), blockFaceUV);
				BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(16.0f, 16.0f, 8.51f), blockPartFace, sideSprite, Direction.SOUTH, ModelUtilities.IDENTITY, null,
						false, new ResourceLocation("dummy_name"));
				quads.add(newQuad);
				newQuad = FaceBaker.bakeQuad(new Vector3f(0.0f, 0.0f, 7.49f), new Vector3f(16.0f, 16.0f, 8.51f), blockPartFace, sideSprite, Direction.NORTH, ModelUtilities.IDENTITY, null, false,
						new ResourceLocation("dummy_name"));
				quads.add(newQuad);

			}
			return quads;
		}

		@Override
		public boolean doesHandlePerspectives() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean usesBlockLight() {
			return baseModel.usesBlockLight();
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

	public static class DyanmicBucketColorProvider implements ItemColor {
		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex != 2) {
				return -1;
			}

			// Get the fluid handler.
			IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);
			if (handler == null || handler.getFluidInTank(0).isEmpty()) {
				return -1;
			} else {
				FluidAttributes attributes = handler.getFluidInTank(0).getFluid().getAttributes();
				return attributes.getColor(handler.getFluidInTank(0));
			}
		}
	}
}
