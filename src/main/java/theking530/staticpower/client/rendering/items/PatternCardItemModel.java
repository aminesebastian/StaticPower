package theking530.staticpower.client.rendering.items;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern;
import theking530.staticpower.items.DigistorePatternCard;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
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
			public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world,
					@Nullable LivingEntity livingEntity) {
				// Make sure we have a valid portable battery.
				if (!(stack.getItem() instanceof DigistorePatternCard)) {
					return originalModel;
				}

				if (DigistorePatternCard.hasPattern(stack)) {
					if (Screen.hasControlDown()) {
						// Try to get the pattern and make sure it has an output.
						EncodedDigistorePattern pattern = EncodedDigistorePattern.readFromPatternCard(stack);
						if (pattern != null && !pattern.getOutput().isEmpty()) {
							// Get the baked model for the recipe output.
							return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(pattern.getOutput(), world, livingEntity);
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
	public boolean isSideLit() {
		return blankModel.isSideLit();
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
