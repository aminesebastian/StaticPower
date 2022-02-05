package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
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
public class ChainsawBladeItemModel implements BakedModel {
	private final BakedModel baseChainsawBlade;

	public ChainsawBladeItemModel(BakedModel emptyDrillModel) {
		this.baseChainsawBlade = emptyDrillModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world,
					@Nullable LivingEntity livingEntity, int x) {
				return new ChainsawBladeWithLayers(stack, baseChainsawBlade);
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return baseChainsawBlade.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return baseChainsawBlade.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseChainsawBlade.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return baseChainsawBlade.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return baseChainsawBlade.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return baseChainsawBlade.getParticleIcon();
	}

	protected class ChainsawBladeWithLayers extends AbstractBakedModel {
		private final ItemStack stack;

		public ChainsawBladeWithLayers(ItemStack stack, BakedModel baseChainsawBladeModel) {
			super(baseChainsawBladeModel);
			this.stack = stack;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
			return getQuads(state, side, rand, EmptyModelData.INSTANCE);
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromIModelData(BlockState state, Direction side, Random rand,
				IModelData data) {
			// If the side is null, do nothing.
			if (side != null) {
				return Collections.emptyList();
			}

			// Allocate the output and add the base model.
			List<BakedQuad> output = new ArrayList<BakedQuad>();
			output.addAll(BaseModel.getQuads(state, side, rand, data));

			// Attempt to get the attributable capability. Return early if it fails.
			IAttributable attributable = stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY)
					.orElse(null);
			if (attributable == null) {
				return output;
			}

			// Get the drill bit item.
			ChainsawBlade bit = (ChainsawBlade) stack.getItem();

			// Add all the quads.
			List<BakedQuad> layers = bit.getRenderLayers().getOrderedRenderQuads(stack, attributable, state, side, rand,
					data);
			output.addAll(layers);

			return output;
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
			// If we have a drill bit, return the particle texture for the drill bit.
			// Otherwise, return the particle texture for the base model.
			IItemHandler inv = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inv != null && !inv.getStackInSlot(0).isEmpty()) {
				BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(inv.getStackInSlot(0),
						Minecraft.getInstance().level, null, 0);
				return itemModel.getParticleIcon();
			}
			return BaseModel.getParticleIcon();
		}
	}
}
