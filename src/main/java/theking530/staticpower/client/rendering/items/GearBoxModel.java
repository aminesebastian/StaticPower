package theking530.staticpower.client.rendering.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticpower.client.rendering.blocks.AbstractBakedModel;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class GearBoxModel implements BakedModel {
	private final ItemStack baseGearItem;
	private GeneratedGearBoxModel generatedModel;

	public GearBoxModel(Item baseGearItem) {
		this.baseGearItem = new ItemStack(baseGearItem);
	}

	@SuppressWarnings("resource")
	private BakedModel getBaseModel() {
		return Minecraft.getInstance().getItemRenderer().getModel(baseGearItem, Minecraft.getInstance().level, null, 0);
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int x) {
				if (generatedModel == null) {
					generatedModel = new GeneratedGearBoxModel(originalModel);
				}
				return generatedModel;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return getBaseModel().getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return getBaseModel().useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return getBaseModel().isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return getBaseModel().usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return getBaseModel().isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return getBaseModel().getParticleIcon();
	}

	protected class GeneratedGearBoxModel extends AbstractBakedModel {

		public GeneratedGearBoxModel(BakedModel baseGearModel) {
			super(baseGearModel);
		}

		@Override
		protected List<BakedQuad> getBakedQuadsFromModelData(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderLayer) {
			if (side != null) {
				return Collections.emptyList();
			}

			List<BakedQuad> output = new ArrayList<BakedQuad>();

			BakedModel itemModel = getBaseModel();
			List<BakedQuad> chainsawBladeQuads = itemModel.getQuads(state, side, rand, data, renderLayer);
			output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(-0.21f, -0.22f, 0f), new Vector3f(0.58f, 0.58f, 1.0f), new Quaternion(0, 0, 0, true)));
			output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(0.21f, -0.02f, 0f), new Vector3f(0.58f, 0.58f, 1.0f), new Quaternion(0, 0, 0, true)));
			output.addAll(transformQuads(chainsawBladeQuads, new Vector3f(-0.13f, 0.22f, 0.001f), new Vector3f(0.58f, 0.58f, 1.0f), new Quaternion(0, 0, 0, true)));

			return output;
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
			return BaseModel.getParticleIcon();
		}
	}
}
