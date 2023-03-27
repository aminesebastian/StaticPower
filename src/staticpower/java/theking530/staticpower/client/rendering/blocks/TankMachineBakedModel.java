package theking530.staticpower.client.rendering.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.client.models.AbstractBakedModel;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.ModelUtilities;

@OnlyIn(Dist.CLIENT)
public class TankMachineBakedModel extends DefaultMachineBakedModel {

	public TankMachineBakedModel(BakedModel baseModel) {
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

			CompoundTag tankTag = stack.getTag().getCompound("SerializableNbt").getCompound("FluidTank");
			CompoundTag fluidTank = tankTag.getCompound("fluidStorage");
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTank.getCompound("tank"));
			float capacity = fluidTank.getInt("capacity");
			float filledPercent = fluid.getAmount() / capacity;

			TextureAtlasSprite sideSprite = GuiDrawUtilities.getStillFluidSprite(fluid);

			for (Direction dir : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST }) {
				output.add(createSideQuad(dir, sideSprite, filledPercent));
			}

			output.add(createTopBottomQuad(Direction.UP, sideSprite, filledPercent));
			output.add(createTopBottomQuad(Direction.DOWN, sideSprite, filledPercent));

			return output;
		}

		private BakedQuad createSideQuad(Direction side, TextureAtlasSprite fluidSprite, float filledPercent) {
			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, filledPercent * 16.0f }, 0);
			BlockElementFace blockPartFace = new BlockElementFace(null, 1, fluidSprite.getName().toString(), blockFaceUV);
			return FaceBaker.bakeQuad(new Vector3f(2.01f, 2.01f, 2.01f), new Vector3f(13.99f, filledPercent * 13.99f, 13.99f), blockPartFace, fluidSprite, side,
					ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name"));
		}

		private BakedQuad createTopBottomQuad(Direction side, TextureAtlasSprite fluidSprite, float filledPercent) {
			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
			BlockElementFace blockPartFace = new BlockElementFace(null, 1, fluidSprite.getName().toString(), blockFaceUV);
			return FaceBaker.bakeQuad(new Vector3f(2.01f, 2.01f, 2.01f), new Vector3f(13.99f, filledPercent * 13.99f, 13.99f), blockPartFace, fluidSprite, side,
					ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name"));
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
