package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

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
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;
import theking530.staticpower.items.backpack.Backpack;
import theking530.staticpower.items.backpack.Backpack.BackpackMode;
import theking530.staticpower.utilities.ModelUtilities;

public class BackpackItemModel implements BakedModel {
	private final BakedModel backpackModel;
	private final Map<BackpackMode, BakedModel> models;

	public BackpackItemModel(BakedModel backpackModel) {
		this.backpackModel = backpackModel;
		models = new HashMap<>();
		models.put(BackpackMode.DEFAULT, backpackModel);
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				BackpackMode mode = ((Backpack) stack.getItem()).getMode(stack);
				if (!models.containsKey(mode)) {
					BackpackModeItemModel newModel = new BackpackModeItemModel(backpackModel, mode);
					models.put(mode, newModel);
				}
				return models.get(mode);
			}
		};
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return backpackModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return backpackModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return backpackModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return backpackModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return backpackModel.isCustomRenderer();
	}

	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return backpackModel.getParticleIcon();
	}

	private class BackpackModeItemModel extends AbstractBakedModel {
		private final BackpackMode mode;
		private List<BakedQuad> quads = null;

		protected BackpackModeItemModel(BakedModel baseModel, BackpackMode mode) {
			super(baseModel);
			this.mode = mode;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
			return getQuads(state, side, rand, EmptyModelData.INSTANCE);
		}

		@Override
		@SuppressWarnings("deprecation")
		protected List<BakedQuad> getBakedQuadsFromIModelData(BlockState state, Direction side, Random rand, IModelData data) {
			if (side != null) {
				return Collections.emptyList();
			}

			if (quads == null) {
				quads = new ArrayList<BakedQuad>();
				quads.addAll(BaseModel.getQuads(state, side, rand, data));

				if (mode != BackpackMode.DEFAULT) {
					TextureAtlas blocksTexture = ForgeModelBakery.instance().getSpriteMap().getAtlas(TextureAtlas.LOCATION_BLOCKS);
					TextureAtlasSprite sideSprite = null;
					if (mode == BackpackMode.REFIL) {
						sideSprite = blocksTexture.getSprite(StaticPowerSprites.ITEM_ICON_REFILL);
					} else if (mode == BackpackMode.LOCKED) {
						sideSprite = blocksTexture.getSprite(StaticPowerSprites.ITEM_ICON_LOCKED);
					} else if (mode == BackpackMode.RELOAD) {
						sideSprite = blocksTexture.getSprite(StaticPowerSprites.ITEM_ICON_RELOAD);
					}

					BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
					BlockElementFace blockPartFace = new BlockElementFace(null, 1, sideSprite.getName().toString(), blockFaceUV);

					quads.add(FaceBaker.bakeQuad(new Vector3f(8.0f, 0.0f, 0.0f), new Vector3f(16.0f, 8.0f, 8.51f), blockPartFace, sideSprite, Direction.SOUTH,
							ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));

					quads.add(FaceBaker.bakeQuad(new Vector3f(8.0f, 0.0f, 7.499f), new Vector3f(16.0f, 8.0f, 16.0f), blockPartFace, sideSprite, Direction.NORTH,
							ModelUtilities.IDENTITY, null, false, new ResourceLocation("dummy_name")));
				}
			}
			return quads;
		}
	}
}
