package theking530.staticpower.client.rendering.items;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import theking530.staticpower.items.DigistorePatternCard;
import theking530.staticpower.items.DigistorePatternCard.EncodedDigistorePattern;

@SuppressWarnings("deprecation")
public class PatternCardItemModel implements IBakedModel {
	private final IBakedModel blankModel;
	private final IBakedModel encodedModel;

	public PatternCardItemModel(IBakedModel blankModel, IBakedModel encodedModel) {
		this.blankModel = blankModel;
		this.encodedModel = encodedModel;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
				// Make sure we have a valid portable battery.
				if (!(stack.getItem() instanceof DigistorePatternCard)) {
					return originalModel;
				}

				if (DigistorePatternCard.hasPattern(stack)) {
					if (Screen.hasControlDown()) {
						// Try to get the pattern and make sure it has an output.
						EncodedDigistorePattern pattern = EncodedDigistorePattern.readFromPatternCard(stack);
						if (pattern != null && pattern.outputs.length > 0) {
							// Get the baked model for the recipe output.
							return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(pattern.outputs[0], world, entity);
						}
					}
					return encodedModel;
				}
				return blankModel;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return blankModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return blankModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return blankModel.isGui3d();
	}

	@Override
	public boolean func_230044_c_() {
		return blankModel.func_230044_c_();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return blankModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return blankModel.getParticleTexture();
	}
}
