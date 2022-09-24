package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
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
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.api.energy.item.ItemStackStaticPowerEnergyCapability;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.PortableBattery;
import theking530.staticpower.utilities.ModelUtilities;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class PortableBatteryItemModel implements BakedModel {
	private final Int2ObjectMap<PortableBatteryModel> cache = new Int2ObjectArrayMap<>();
	private final BakedModel baseModel;

	public PortableBatteryItemModel(BakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				// Make sure we have a valid portable battery.
				if (!(stack.getItem() instanceof PortableBattery)) {
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
				int hash = Objects.hash(stack.getItem().getRegistryName() + ((PortableBattery) stack.getItem()).tier.toString() + intRatio);

				// Check to see if we need to cache this model, if we do, do it.
				PortableBatteryModel model = PortableBatteryItemModel.this.cache.get(hash);
				if (model == null) {
					model = new PortableBatteryModel(baseModel, ratio, ((PortableBattery) stack.getItem()).tier == StaticPowerTiers.CREATIVE);
					PortableBatteryItemModel.this.cache.put(hash, model);
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

	private class PortableBatteryModel extends AbstractBakedModel {
		private final float filledRatio;
		private List<BakedQuad> quads = null;
		private boolean creative;

		protected PortableBatteryModel(BakedModel baseModel, float filledRatio, boolean creative) {
			super(baseModel);
			this.filledRatio = filledRatio;
			this.creative = creative;
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
				TextureAtlasSprite sideSprite = blocksTexture
						.getSprite(creative ? StaticPowerSprites.PORTABLE_CREATIVE_BATTERY_FILL_BAR : StaticPowerSprites.PORTABLE_BATTERY_FILL_BAR);

				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockElementFace blockPartFace = new BlockElementFace(null, 1, sideSprite.getName().toString(), blockFaceUV);

				quads.add(FaceBaker.bakeQuad(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(16.0f, filledRatio * 16.0f, 8.51f), blockPartFace, sideSprite, Direction.SOUTH,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));

				quads.add(FaceBaker.bakeQuad(new Vector3f(0.0f, 0.0f, 7.499f), new Vector3f(16.0f, filledRatio * 16.0f, 16.0f), blockPartFace, sideSprite, Direction.NORTH,
						ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));
			}
			return quads;
		}
	}
}
