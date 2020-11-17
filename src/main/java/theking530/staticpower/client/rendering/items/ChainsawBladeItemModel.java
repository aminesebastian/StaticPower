package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.items.tools.chainsaw.ChainsawBlade;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class ChainsawBladeItemModel implements IBakedModel {
	private final IBakedModel baseChainsawBlade;

	public ChainsawBladeItemModel(IBakedModel emptyDrillModel) {
		this.baseChainsawBlade = emptyDrillModel;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
				return new ChainsawBladeWithLayers(stack, baseChainsawBlade);
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return baseChainsawBlade.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return baseChainsawBlade.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseChainsawBlade.isGui3d();
	}

	@Override
	public boolean isSideLit() {
		return baseChainsawBlade.isSideLit();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return baseChainsawBlade.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseChainsawBlade.getParticleTexture();
	}

	protected class ChainsawBladeWithLayers extends AbstractBakedModel {
		private final ItemStack stack;

		public ChainsawBladeWithLayers(ItemStack stack, IBakedModel baseChainsawBladeModel) {
			super(baseChainsawBladeModel);
			this.stack = stack;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
			return getQuads(state, side, rand, EmptyModelData.INSTANCE);
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromIModelData(BlockState state, Direction side, Random rand, IModelData data) {
			// If the side is null, do nothing.
			if (side != null) {
				return Collections.emptyList();
			}

			// Allocate the output and add the base model.
			List<BakedQuad> output = new ArrayList<BakedQuad>();
			output.addAll(BaseModel.getQuads(state, side, rand, data));

			// Attempt to get the attributable capability. Return early if it fails.
			IAttributable attributable = stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
			if (attributable == null) {
				return output;
			}

			// Get the drill bit item.
			ChainsawBlade bit = (ChainsawBlade) stack.getItem();

			// Add all the quads.
			List<BakedQuad> layers = bit.getRenderLayers().getOrderedRenderQuads(stack, attributable, state, side, rand, data);
			output.addAll(layers);
			
			return output;
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
		public boolean isSideLit() {
			return BaseModel.isSideLit();
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
			// If we have a drill bit, return the particle texture for the drill bit.
			// Otherwise, return the particle texture for the base model.
			IItemHandler inv = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inv != null && !inv.getStackInSlot(0).isEmpty()) {
				IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(inv.getStackInSlot(0), Minecraft.getInstance().world, null);
				return itemModel.getParticleTexture();
			}
			return BaseModel.getParticleTexture();
		}
	}
}
