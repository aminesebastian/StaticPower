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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class GearBoxModel implements IBakedModel {
	private final ItemStack baseGearItem;
	private GeneratedGearBoxModel generatedModel;

	public GearBoxModel(Item baseGearItem) {
		this.baseGearItem = new ItemStack(baseGearItem);
	}

	private IBakedModel getBaseModel() {
		return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(baseGearItem, Minecraft.getInstance().world, null);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
				if (generatedModel == null) {
					generatedModel = new GeneratedGearBoxModel(originalModel);
				}
				return generatedModel;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return getBaseModel().getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return getBaseModel().isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return getBaseModel().isGui3d();
	}

	@Override
	public boolean isSideLit() {
		return getBaseModel().isSideLit();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return getBaseModel().isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getBaseModel().getParticleTexture();
	}

	protected class GeneratedGearBoxModel extends AbstractBakedModel {

		public GeneratedGearBoxModel(IBakedModel baseGearModel) {
			super(baseGearModel);
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

			IBakedModel itemModel = getBaseModel();
			List<BakedQuad> chainsawBladeQuads = itemModel.getQuads(state, side, rand, data);
			output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(-0.21f, -0.22f, 0f), new Vector3f(0.58f, 0.58f, 1.0f), new Quaternion(0, 0, 0, true)));
			output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(0.21f, -0.02f, 0f), new Vector3f(0.58f, 0.58f, 1.0f), new Quaternion(0, 0, 0, true)));
			output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(-0.13f, 0.22f, 0.001f), new Vector3f(0.58f, 0.58f, 1.0f), new Quaternion(0, 0, 0, true)));

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
			return BaseModel.getParticleTexture();
		}
	}
}
