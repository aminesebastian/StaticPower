package theking530.staticpower.client.rendering.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.utilities.ModelUtilities;

@OnlyIn(Dist.CLIENT)
public class BatteryBlockedBakedModel extends DefaultMachineBakedModel {
	public BatteryBlockedBakedModel(BakedModel baseModel) {
		super(baseModel, true);
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				return new TankWithFluid(stack, BaseModel);
			}
		};
	}

	protected class TankWithFluid extends AbstractBakedModel {
		private final ItemStack stack;

		public TankWithFluid(ItemStack stack, BakedModel baseBladeModel) {
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

			if (!stack.hasTag() || !stack.getTag().contains("SerializableNbt")) {
				return output;
			}

			CompoundTag powerTag = stack.getTag().getCompound("SerializableNbt").getCompound("MainEnergyStorage");
			CompoundTag powerStorageTag = powerTag.getCompound("storage");
			float capacity = powerStorageTag.getFloat("capacity");
			float stored = powerStorageTag.getFloat("storedPower");
			float filledPercent = stored / capacity;

			TextureAtlasSprite sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BATTERY_BLOCK_BAR);
			for (Direction dir : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST }) {
				output.add(createSideQuad(dir, sideSprite, filledPercent));
			}

			return output;
		}

		private BakedQuad createSideQuad(Direction side, TextureAtlasSprite fluidSprite, float filledPercent) {
			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, filledPercent * 16.0f }, 0);
			BlockElementFace blockPartFace = new BlockElementFace(null, 1, fluidSprite.getName().toString(), blockFaceUV);

			if (side.getAxis() == Axis.X) {
				return FaceBaker.bakeQuad(new Vector3f(0.25f, 3.85f, 3.85f), new Vector3f(15.75f, 12.35f * filledPercent, 12.15f), blockPartFace, fluidSprite, side,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name"));
			} else {
				return FaceBaker.bakeQuad(new Vector3f(3.85f, 3.85f, 0.25f), new Vector3f(12.15f, 12.35f * filledPercent, 15.75f), blockPartFace, fluidSprite, side,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name"));
			}
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean usesBlockLight() {
			return BaseModel.usesBlockLight();
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@SuppressWarnings("deprecation")
		@Override
		public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
			BaseModel.getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
			return this;
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