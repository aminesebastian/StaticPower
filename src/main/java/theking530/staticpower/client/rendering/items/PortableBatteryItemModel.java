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
import net.minecraft.client.renderer.color.IItemColor;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.PortableBattery;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class PortableBatteryItemModel implements IBakedModel {
	private final Int2ObjectMap<PortableBatteryModel> cache = new Int2ObjectArrayMap<>();
	private final IBakedModel baseModel;

	public PortableBatteryItemModel(IBakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
				// Make sure we have a valid portable battery.
				if (!(stack.getItem() instanceof PortableBattery)) {
					return originalModel;
				}

				// Get the energy handler.
				IEnergyStorage handler = EnergyHandlerItemStackUtilities.getEnergyContainer(stack).orElse(null);
				if (handler == null) {
					return originalModel;
				}

				// Get the power ratio.
				float ratio = (float) handler.getEnergyStored() / (float) handler.getMaxEnergyStored();
				int intRatio = (int) (ratio * 50);

				// Hash the unique info about this model.
				int hash = Objects.hash(stack.getItem().getRegistryName() + ((PortableBattery) stack.getItem()).tier.toString() + intRatio);

				// Check to see if we need to cache this model, if we do, do it.
				PortableBatteryModel model = PortableBatteryItemModel.this.cache.get(hash);
				if (model == null) {
					model = new PortableBatteryModel(baseModel, ratio, ((PortableBattery) stack.getItem()).tier == StaticPowerTiers.CREATIVE);
					PortableBatteryItemModel.this.cache.put(hash, model);
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

	private class PortableBatteryModel extends AbstractBakedModel {
		private final float filledRatio;
		private final IBakedModel baseModel;
		private List<BakedQuad> quads = null;
		private boolean creative;

		protected PortableBatteryModel(IBakedModel baseModel, float filledRatio, boolean creative) {
			super(baseModel);
			this.filledRatio = filledRatio;
			this.baseModel = baseModel;
			this.creative = creative;
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
				TextureAtlasSprite sideSprite = blocksTexture.getSprite(creative ? StaticPowerSprites.PORTABLE_CREATIVE_BATTERY_FILL_BAR : StaticPowerSprites.PORTABLE_BATTERY_FILL_BAR);

				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockPartFace blockPartFace = new BlockPartFace(null, 1, sideSprite.getName().toString(), blockFaceUV);

				BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(16.0f, filledRatio * 16.0f, 8.51f), blockPartFace, sideSprite, Direction.SOUTH,
						SimpleModelTransform.IDENTITY, null, false, new ResourceLocation("dummy_name"));

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

	public static class CapsuleColorProvider implements IItemColor {
		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex != 1) {
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
