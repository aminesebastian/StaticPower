package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.utilities.ModelUtilities;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class ThermometerItemModel implements BakedModel {
	private final Map<Integer, ThermometerItemSubModel> cache = new HashMap<>();
	private final BakedModel baseModel;

	public ThermometerItemModel(BakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@SuppressWarnings("resource")
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				BlockPos playerPosition = Minecraft.getInstance().player.getOnPos();
				int biomeTemperature = HeatStorageUtilities.getBiomeAmbientTemperature(Minecraft.getInstance().level, playerPosition);
				biomeTemperature = CapabilityHeatable.convertMilliHeatToHeat(biomeTemperature - IHeatStorage.MINIMUM_TEMPERATURE);
				float ratio = SDMath.clamp((biomeTemperature) / 473.0f, 0, 1);
				int mapEntry = (int) Math.floor(ratio * 200);
				// Check to see if we need to cache this model, if we do, do it.
				ThermometerItemSubModel model = ThermometerItemModel.this.cache.get(mapEntry);
				if (model == null) {
					model = new ThermometerItemSubModel(baseModel, ratio);
					ThermometerItemModel.this.cache.put(mapEntry, model);
				}
				return model;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return baseModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return baseModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return baseModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return baseModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return baseModel.getParticleIcon();
	}

	private class ThermometerItemSubModel extends AbstractBakedModel {
		private final float filledRatio;
		private final BakedModel baseModel;
		private List<BakedQuad> quads = null;

		protected ThermometerItemSubModel(BakedModel baseModel, float filledRatio) {
			super(baseModel);
			this.filledRatio = filledRatio;
			this.baseModel = baseModel;
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

			if (quads == null) {
				quads = new ArrayList<BakedQuad>();
				quads.addAll(baseModel.getQuads(state, side, rand, data));

				TextureAtlas blocksTexture = ForgeModelBakery.instance().getSpriteMap().getAtlas(TextureAtlas.LOCATION_BLOCKS);
				TextureAtlasSprite sideSprite = blocksTexture.getSprite(StaticPowerSprites.THERMOMETER_FILL_BAR);

				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, (1 - filledRatio) * 16.0f, 16.0f, 16.0f }, 0);
				BlockElementFace blockPartFace = new BlockElementFace(null, 1, sideSprite.getName().toString(), blockFaceUV);

				quads.add(FaceBaker.bakeQuad(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(16.0f, filledRatio * 16.0f, 8.51f), blockPartFace, sideSprite, Direction.SOUTH,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));

				quads.add(FaceBaker.bakeQuad(new Vector3f(0.0f, 0.0f, 7.499f), new Vector3f(16.0f, filledRatio * 16.0f, 16.0f), blockPartFace, sideSprite, Direction.NORTH,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));
			}
			return quads;
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
			return baseModel.usesBlockLight();
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
			return null;
		}

	}

	public static class CapsuleColorProvider implements ItemColor {
		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex != 1) {
				return -1;
			}

			// Get the fluid handler.
			IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);
			if (handler == null || handler.getFluidInTank(0).isEmpty()) {
				return -1;
			} else {
				FluidAttributes attributes = handler.getFluidInTank(0).getFluid().getAttributes();
				return attributes.getColor(handler.getFluidInTank(0));
			}
		}
	}
}