package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.utilities.ModelUtilities;

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
				return new MiningDrillWithAttachments(stack, emptyChainsawModel);
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
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

		public MiningDrillWithAttachments(ItemStack stack, BakedModel emptyDrillModel) {
			super(emptyDrillModel);
			this.stack = stack;
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

			List<BakedQuad> output = new ArrayList<BakedQuad>();
			AtomicBoolean chainsawEquipped = new AtomicBoolean(false);

			// Attempt to get the chainsaw inventory.
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
				if (!handler.getStackInSlot(0).isEmpty()) {
					chainsawEquipped.set(true);
					BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(handler.getStackInSlot(0), Minecraft.getInstance().level, null, 0);
					List<BakedQuad> chainsawBladeQuads = itemModel.getQuads(state, side, rand, data);
					output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(0.25f, 0.28f, 0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternion(0, 0, 0, true)));
				}
			});

			if (chainsawEquipped.get()) {
				// Add a mini chainsaw.
				List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);
				output.addAll(transformQuads(baseQuads, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Quaternion(0, 0, 0, true)));
			} else {
				// Add the full drill.
				List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);
				output.addAll(transformQuads(baseQuads, new Vector3f(0.15f, 0.15f, 0.0f), new Vector3f(1.3f, 1.3f, 1.0f), new Quaternion(0, 0, 0, true)));
			}

			// Draw the power bar.
			try {
				// Top Offset
				float topOffset = chainsawEquipped.get() ? 3.5f : 0.0f;
				float sideOffset = chainsawEquipped.get() ? 0.5f : 0.0f;

				// Get the atlas texture.
				TextureAtlas blocksTexture = ForgeModelBakery.instance().getSpriteMap().getAtlas(TextureAtlas.LOCATION_BLOCKS);

				// Draw the durability background.
				TextureAtlasSprite blackSprite = blocksTexture.getSprite(StaticPowerSprites.BLACK_TEXTURE);
				BlockFaceUV durabilityBgUv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockElementFace durabilityPartFace = new BlockElementFace(null, -1, blackSprite.getName().toString(), durabilityBgUv);
				BlockElementRotation rotation = new BlockElementRotation(new Vector3f(0.0f, 0.0f, 0.0f), Direction.Axis.Z, 135, false);
				BakedQuad durabilityBackground = FaceBaker.bakeQuad(new Vector3f(-3.0f + sideOffset, -15.15f + topOffset, 8.5f),
						new Vector3f(2.5f - sideOffset, -14.7f + topOffset, 8.51f), durabilityPartFace, blackSprite, Direction.SOUTH, ModelUtilities.IDENTITY, rotation, false,
						new ResourceLocation("dummy_name"));
				output.add(durabilityBackground);

				// Draw the durability bar.
				float bitDurability = (float) (EnergyHandlerItemStackUtilities.getStoredPower(stack) / EnergyHandlerItemStackUtilities.getCapacity(stack));
				float xUVCoord = bitDurability * 15.999f;
				TextureAtlasSprite durabilityTexture = blocksTexture.getSprite(StaticPowerSprites.TOOL_POWER_BAR);
				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { xUVCoord, 0.0f, xUVCoord, 16.0f }, 0);
				BlockElementFace durabilityBarFace = new BlockElementFace(null, -1, durabilityTexture.getName().toString(), blockFaceUV);

				BakedQuad durabilityBar = FaceBaker.bakeQuad(new Vector3f(-3.0f + sideOffset, -15.15f + topOffset, 8.5f),
						new Vector3f(-3.0f + (bitDurability * 5.5f) - sideOffset, -14.7f + topOffset, 8.511f), durabilityBarFace, durabilityTexture, Direction.SOUTH,
						ModelUtilities.IDENTITY, rotation, false, new ResourceLocation("dummy_name"));
				output.add(durabilityBar);
			} catch (Exception e) {
				// No nothing -- this is just for those edge cases where resources are reloaded.
			}

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
			// If we have a chainsaw blade, return the particle texture for the blade.
			// Otherwise, return the particle texture for the base model.
			IItemHandler inv = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inv != null && !inv.getStackInSlot(0).isEmpty()) {
				BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(inv.getStackInSlot(0), Minecraft.getInstance().level, null, 0);
				return itemModel.getParticleIcon();
			}
			return BaseModel.getParticleIcon();
		}
	}
}
