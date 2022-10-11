package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

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
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.utilities.ModelUtilities;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class DigistoreMonoCardItemModel implements BakedModel {
	private final Int2ObjectMap<DigistoreMonoCardModel> cache = new Int2ObjectArrayMap<>();
	private final BakedModel baseModel;

	public DigistoreMonoCardItemModel(BakedModel baseModel) {
		this.baseModel = baseModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				if (!(stack.getItem() instanceof DigistoreMonoCard)) {
					return originalModel;
				}

				IDigistoreInventory inv = DigistoreCard.getInventory(stack);
				float ratio = (float) inv.getTotalContainedCount() / inv.getItemCapacity();
				int intRatio = (int) (ratio * 20);

				int hash = Objects.hash(ForgeRegistries.ITEMS.getKey(stack.getItem()), intRatio);
				DigistoreMonoCardModel model = DigistoreMonoCardItemModel.this.cache.get(hash);
				if (model == null) {
					model = new DigistoreMonoCardModel(baseModel, ratio);
					DigistoreMonoCardItemModel.this.cache.put(hash, model);
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

	private class DigistoreMonoCardModel extends AbstractBakedModel {
		private final float filledRatio;
		private List<BakedQuad> quads = null;

		protected DigistoreMonoCardModel(BakedModel baseModel, float filledRatio) {
			super(baseModel);
			this.filledRatio = filledRatio;
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromModelData(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderLayer) {
			if (side != null) {
				return Collections.emptyList();
			}

			if (quads == null) {
				quads = new ArrayList<BakedQuad>();
				quads.addAll(BaseModel.getQuads(state, side, rand, data, renderLayer));

				TextureAtlasSprite sideSprite;
				if (filledRatio < 1.0f) {
					sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.DIGISTORE_FILL_BAR);
				} else {
					sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.DIGISTORE_FILL_BAR_FULL);
				}

				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockElementFace blockPartFace = new BlockElementFace(null, -1, sideSprite.getName().toString(), blockFaceUV);

				BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(3.5f, 4.0f, 0.0f), new Vector3f(3.5f + (filledRatio * 9.0f), 5.4f, 16.0f), blockPartFace, sideSprite,
						Direction.SOUTH, ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name"));
				quads.add(newQuad);
			}
			return quads;
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
			return null;
		}

	}
}
