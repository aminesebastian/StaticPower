package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.api.attributes.ItemAttributeRegistry;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.staticcore.client.models.AbstractBakedModel;
import theking530.staticpower.items.tools.sword.Blade;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class BladeItemModel implements BakedModel {
	private final BakedModel baseBladeModel;

	public BladeItemModel(BakedModel emptyDrillModel) {
		this.baseBladeModel = emptyDrillModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				return new BladeWithLayers(stack, baseBladeModel);
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return baseBladeModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return baseBladeModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseBladeModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return baseBladeModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return baseBladeModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return baseBladeModel.getParticleIcon();
	}

	protected class BladeWithLayers extends AbstractBakedModel {
		private final ItemStack stack;

		public BladeWithLayers(ItemStack stack, BakedModel baseBladeModel) {
			super(baseBladeModel);
			this.stack = stack;
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromModelData(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderLayer) {
			// If the side is null, do nothing.
			if (side != null) {
				return Collections.emptyList();
			}

			// Allocate the output and add the base model.
			List<BakedQuad> output = new ArrayList<BakedQuad>();
			output.addAll(BaseModel.getQuads(state, side, rand, data, renderLayer));

			// Attempt to get the attributable capability. Return early if it fails.
			IAttributable attributable = stack.getCapability(CapabilityAttributable.CAPABILITY_ATTRIBUTABLE).orElse(null);
			if (attributable == null) {
				return output;
			}

			// Get the drill bit item.
			Blade bit = (Blade) stack.getItem();

			// Add all the quads.
			List<BakedQuad> layers = ItemAttributeRegistry.get(bit).getOrderedRenderQuads(stack, attributable, state, side, rand, data);
			output.addAll(layers);

			return output;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
			BaseModel.getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
			return this;
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
			return BaseModel.getParticleIcon();
		}
	}
}
