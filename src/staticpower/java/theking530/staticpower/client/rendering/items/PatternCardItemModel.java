package theking530.staticpower.client.rendering.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern;
import theking530.staticpower.items.DigistorePatternCard;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class PatternCardItemModel implements BakedModel {
	private final BakedModel blankModel;
	private final BakedModel encodedModel;

	public PatternCardItemModel(BakedModel blankModel, BakedModel encodedModel) {
		this.blankModel = blankModel;
		this.encodedModel = encodedModel;
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world,
					@Nullable LivingEntity livingEntity, int x) {
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
							return Minecraft.getInstance().getItemRenderer().getModel(pattern.getOutput(), world,
									livingEntity, x);
						}
					}
					return encodedModel;
				}
				return blankModel;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return blankModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return blankModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return blankModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return blankModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return blankModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return blankModel.getParticleIcon();
	}
}
