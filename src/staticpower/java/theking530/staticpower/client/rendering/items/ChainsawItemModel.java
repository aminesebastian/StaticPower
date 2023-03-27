package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.client.models.AbstractBakedModel;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.client.utilities.BakedModelRenderingUtilities;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class ChainsawItemModel implements BakedModel {
	private final BakedModel emptyChainsawModel;

	public ChainsawItemModel(BakedModel emptyDrillModel) {
		this.emptyChainsawModel = emptyDrillModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				return new MiningDrillWithAttachments(stack, emptyChainsawModel, world != null);
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return emptyChainsawModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return emptyChainsawModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return emptyChainsawModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return emptyChainsawModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return emptyChainsawModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return emptyChainsawModel.getParticleIcon();
	}

	protected class MiningDrillWithAttachments extends AbstractBakedModel {
		private final ItemStack stack;
		private final boolean inWorld;

		public MiningDrillWithAttachments(ItemStack stack, BakedModel emptyDrillModel, boolean inWorld) {
			super(emptyDrillModel);
			this.stack = stack;
			this.inWorld = inWorld;
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromModelData(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderLayer) {
			if (side != null) {
				return Collections.emptyList();
			}

			List<BakedQuad> output = new ArrayList<BakedQuad>();
			AtomicBoolean bladeEquipped = new AtomicBoolean(false);

			// Attempt to get the chainsaw inventory.
			stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
				if (!handler.getStackInSlot(0).isEmpty()) {
					bladeEquipped.set(true);
					@SuppressWarnings("resource")
					BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(handler.getStackInSlot(0), Minecraft.getInstance().level, null, 0);
					List<BakedQuad> chainsawBladeQuads = itemModel.getQuads(state, side, rand, data, renderLayer);
					output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(0.25f, 0.28f, 0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternion(0, 0, 0, true)));
				}
			});

			if (bladeEquipped.get()) {
				// Add a mini chainsaw.
				List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data, renderLayer);
				output.addAll(transformQuads(baseQuads, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Quaternion(0, 0, 0, true)));
			} else {
				// Add the full drill.
				List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data, renderLayer);
				output.addAll(transformQuads(baseQuads, new Vector3f(0.15f, 0.15f, 0.0f), new Vector3f(1.3f, 1.3f, 1.0f), new Quaternion(0, 0, 0, true)));
			}

			// Draw the power bar.
			float storedPower = (float) (EnergyHandlerItemStackUtilities.getStoredPower(stack) / EnergyHandlerItemStackUtilities.getCapacity(stack));
			if (!inWorld) {
				if (bladeEquipped.get()) {
					output.addAll(
							BakedModelRenderingUtilities.getBakedQuadsForToolPowerBar(state, side, rand, data, storedPower, new Vector2D(2f, 4), new Vector2D(15, 5f), 0.0f, true));
				} else {
					output.addAll(
							BakedModelRenderingUtilities.getBakedQuadsForToolPowerBar(state, side, rand, data, storedPower, new Vector2D(2f, 2), new Vector2D(15, 3f), 0.0f, true));
				}
			}

			// Draw the on-item power bar.
			if (bladeEquipped.get()) {
				output.addAll(BakedModelRenderingUtilities.getBakedQuadsForToolPowerBar(state, side, rand, data, storedPower, new Vector2D(.75f, 3f), new Vector2D(4.75f, 4.5f),
						-45.0f, false));
			} else {
				output.addAll(BakedModelRenderingUtilities.getBakedQuadsForToolPowerBar(state, side, rand, data, storedPower, new Vector2D(.5f, 5.75f), new Vector2D(5.0f, 7.5f),
						-45.0f, false));
			}
			return output;
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
		public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
			BaseModel.getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
			return this;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			// If we have a chainsaw blade, return the particle texture for the blade.
			// Otherwise, return the particle texture for the base model.
			IItemHandler inv = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			if (inv != null && !inv.getStackInSlot(0).isEmpty()) {
				@SuppressWarnings("resource")
				BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(inv.getStackInSlot(0), Minecraft.getInstance().level, null, 0);
				return itemModel.getParticleIcon();
			}
			return BaseModel.getParticleIcon();
		}
	}
}
