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
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;

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
			output.addAll(BaseModel.getQuads(state, side, rand, data));

			// Attempt to get the drill inventory.
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
				if (!handler.getStackInSlot(0).isEmpty()) {
					IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(handler.getStackInSlot(0), Minecraft.getInstance().world, null);
					List<BakedQuad> drillBitQuads = itemModel.getQuads(state, side, rand, data);
					output.addAll(transformQuads(drillBitQuads, new Vector3f(0.3f, 0.3f, -0.001f), new Vector3f(0.55f, 0.55f, 1.1f), new Quaternion(0, 0, 135, true)));
				}
			});

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
			return null;
		}
	}
}
