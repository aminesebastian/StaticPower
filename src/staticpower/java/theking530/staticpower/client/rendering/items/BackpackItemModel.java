package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticcore.client.models.AbstractBakedModel;
import theking530.staticcore.utilities.ModelUtilities;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.items.backpack.Backpack;
import theking530.staticpower.items.backpack.Backpack.BackpackMode;

public class BackpackItemModel implements BakedModel {
	private final BakedModel backpackModel;
	private final BakedModel openModel;

	private final Map<BackpackMode, BakedModel> openModels;
	private final Map<BackpackMode, BakedModel> closedModels;

	public BackpackItemModel(BakedModel backpackModel, BakedModel openModel) {
		this.backpackModel = backpackModel;
		this.openModel = openModel;

		openModels = new HashMap<>();
		openModels.put(BackpackMode.DEFAULT, openModel);

		closedModels = new HashMap<>();
		closedModels.put(BackpackMode.DEFAULT, backpackModel);
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				Backpack backpack = ((Backpack) stack.getItem());
				BackpackMode mode = backpack.getMode(stack);
				boolean isOpen = backpack.isOpened(stack);

				if (isOpen) {
					if (!openModels.containsKey(mode)) {
						openModels.put(mode, new BackpackModeItemModel(openModel, mode));
					}
					return openModels.get(mode);
				} else {
					if (!closedModels.containsKey(mode)) {
						closedModels.put(mode, new BackpackModeItemModel(backpackModel, mode));
					}
					return closedModels.get(mode);
				}
			}
		};
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
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

		@SuppressWarnings("deprecation")
		@Override
		public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
			BaseModel.getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
			return this;
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromModelData(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderLayer) {
			if (side != null) {
				return Collections.emptyList();
			}

			if (quads == null) {
				quads = new ArrayList<BakedQuad>();
				quads.addAll(BaseModel.getQuads(state, side, rand, data, renderLayer));

				if (mode != BackpackMode.DEFAULT) {
					TextureAtlasSprite sideSprite = null;
					if (mode == BackpackMode.REFIL) {
						sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.ITEM_ICON_REFILL);
					} else if (mode == BackpackMode.LOCKED) {
						sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.ITEM_ICON_LOCKED);
					} else if (mode == BackpackMode.RELOAD) {
						sideSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.ITEM_ICON_RELOAD);
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
