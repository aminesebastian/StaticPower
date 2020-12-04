package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class MiningDrillItemModel implements IBakedModel {
	private final IBakedModel emptyDrillModel;

	public MiningDrillItemModel(IBakedModel emptyDrillModel) {
		this.emptyDrillModel = emptyDrillModel;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
				return new MiningDrillWithAttachments(stack, emptyDrillModel);
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return emptyDrillModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return emptyDrillModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return emptyDrillModel.isGui3d();
	}

	@Override
	public boolean isSideLit() {
		return emptyDrillModel.isSideLit();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return emptyDrillModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return emptyDrillModel.getParticleTexture();
	}

	protected class MiningDrillWithAttachments extends AbstractBakedModel {
		private final ItemStack stack;

		public MiningDrillWithAttachments(ItemStack stack, IBakedModel emptyDrillModel) {
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
			AtomicBoolean drillBitEquipped = new AtomicBoolean(false);

			// Attempt to get the drill inventory.
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
				if (!handler.getStackInSlot(0).isEmpty()) {
					drillBitEquipped.set(true);
					IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(handler.getStackInSlot(0), Minecraft.getInstance().world, null);
					List<BakedQuad> drillBitQuads = itemModel.getQuads(state, side, rand, data);
					output.addAll(transformQuads(drillBitQuads, new Vector3f(0.3f, 0.3f, -0.001f), new Vector3f(0.55f, 0.55f, 1.1f), new Quaternion(0, 0, 135, true)));
				}
			});

			if (drillBitEquipped.get()) {
				// Add a mini drill.
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
				float topOffset = drillBitEquipped.get() ? 3.5f : 0.0f;
				float sideOffset = drillBitEquipped.get() ? 0.5f : 0.0f;

				// Get the atlas texture.
				AtlasTexture blocksTexture = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

				// Draw the durability background.
				TextureAtlasSprite blackSprite = blocksTexture.getSprite(StaticPowerSprites.BLACK_TEXTURE);
				BlockFaceUV durabilityBgUv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockPartFace durabilityPartFace = new BlockPartFace(null, -1, blackSprite.getName().toString(), durabilityBgUv);
				BlockPartRotation rotation = new BlockPartRotation(new Vector3f(0.0f, 0.0f, 0.0f), Direction.Axis.Z, 135, false);
				BakedQuad durabilityBackground = FaceBaker.bakeQuad(new Vector3f(-3.0f + sideOffset, -12.0f + topOffset, 8.5f), new Vector3f(2.0f - sideOffset, -11.35f + topOffset, 8.51f),
						durabilityPartFace, blackSprite, Direction.SOUTH, SimpleModelTransform.IDENTITY, rotation, false, new ResourceLocation("dummy_name"));
				output.add(durabilityBackground);

				// Draw the durability bar.
				float bitDurability = (float) EnergyHandlerItemStackUtilities.getStoredPower(stack) / EnergyHandlerItemStackUtilities.getCapacity(stack);
				float xUVCoord = bitDurability * 15.999f;
				TextureAtlasSprite durabilityTexture = blocksTexture.getSprite(StaticPowerSprites.TOOL_POWER_BAR);
				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { xUVCoord, 0.0f, xUVCoord, 16.0f }, 0);
				BlockPartFace durabilityBarFace = new BlockPartFace(null, -1, durabilityTexture.getName().toString(), blockFaceUV);

				BakedQuad durabilityBar = FaceBaker.bakeQuad(new Vector3f(-3.0f + sideOffset, -12.0f + topOffset, 8.5f),
						new Vector3f(-3.0f + (bitDurability * 5.0f) - sideOffset, -11.35f + topOffset, 8.511f), durabilityBarFace, durabilityTexture, Direction.SOUTH,
						SimpleModelTransform.IDENTITY, rotation, false, new ResourceLocation("dummy_name"));
				output.add(durabilityBar);
			} catch (Exception e) {
				// No nothing -- this is just for those edge cases where resources are reloaded.
			}

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
