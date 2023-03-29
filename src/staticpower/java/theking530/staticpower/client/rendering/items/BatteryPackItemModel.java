package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.api.energy.item.ItemStackStaticPowerEnergyCapability;
import theking530.staticcore.client.models.AbstractBakedModel;
import theking530.staticcore.utilities.ModelUtilities;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.BatteryPack;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class BatteryPackItemModel implements BakedModel {
	private final Int2ObjectMap<PortableBatteryModel> cache = new Int2ObjectArrayMap<>();
	private final BakedModel baseModel;

	public BatteryPackItemModel(BakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				// Make sure we have a valid portable battery.
				if (!(stack.getItem() instanceof BatteryPack)) {
					return originalModel;
				}

				// Get the energy handler.
				ItemStackStaticPowerEnergyCapability handler = EnergyHandlerItemStackUtilities.getEnergyContainer(stack).orElse(null);
				if (handler == null) {
					return originalModel;
				}

				// Get the power ratio.
				float ratio = (float) handler.getStoredPower() / (float) handler.getCapacity();
				int intRatio = (int) (ratio * 50);

				// Hash the unique info about this model.
				int hash = Objects.hash(ForgeRegistries.ITEMS.getKey(stack.getItem()) + ((BatteryPack) stack.getItem()).tier.toString() + intRatio);

				// Check to see if we need to cache this model, if we do, do it.
				PortableBatteryModel model = BatteryPackItemModel.this.cache.get(hash);
				if (model == null) {
					model = new PortableBatteryModel(baseModel, ratio, ((BatteryPack) stack.getItem()).tier == StaticPowerTiers.CREATIVE);
					BatteryPackItemModel.this.cache.put(hash, model);
				}
				return model;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
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

	private class PortableBatteryModel extends AbstractBakedModel {
		private final float filledRatio;
		private final BakedModel baseModel;
		private List<BakedQuad> quads = null;
		private boolean creative;

		protected PortableBatteryModel(BakedModel baseModel, float filledRatio, boolean creative) {
			super(baseModel);
			this.filledRatio = filledRatio;
			this.baseModel = baseModel;
			this.creative = creative;
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromModelData(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderLayer) {
			if (side != null) {
				return Collections.emptyList();
			}

			if (quads == null) {
				quads = new ArrayList<BakedQuad>();
				quads.addAll(baseModel.getQuads(state, side, rand, data, renderLayer));

				TextureAtlasSprite sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
						.apply(creative ? StaticPowerSprites.PORTABLE_CREATIVE_BATTERY_PACK_FILL_BAR : StaticPowerSprites.PORTABLE_BATTERY_PACK_FILL_BAR);

				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 4.95f, 16.0f, 5.0f + filledRatio * 5.0f }, 0);
				BlockElementFace blockPartFace = new BlockElementFace(null, 1, sideSprite.getName().toString(), blockFaceUV);

				quads.add(FaceBaker.bakeQuad(new Vector3f(0.0f, 4.95f, 0.0f), new Vector3f(16.0f, 11.5f, 8.501f), blockPartFace, sideSprite, Direction.SOUTH,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));
				quads.add(FaceBaker.bakeQuad(new Vector3f(0.0f, 4.95f, 7.499f), new Vector3f(16.0f, 11.5f, 16.0f), blockPartFace, sideSprite, Direction.NORTH,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));
			}
			return quads;
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
}
