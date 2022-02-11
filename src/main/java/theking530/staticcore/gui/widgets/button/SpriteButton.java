package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.SpriteDrawable;

@OnlyIn(Dist.CLIENT)
public class SpriteButton extends StandardButton {
	private ResourceLocation regularTexture;
	private ResourceLocation hoveredTexture;
	private SpriteDrawable drawable;

	public SpriteButton(int xPos, int yPos, int width, int height, ResourceLocation sprite, @Nullable ResourceLocation hoveredSprite, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.regularTexture = sprite;
		this.hoveredTexture = hoveredSprite;
		this.drawable = new SpriteDrawable(sprite, width, height);
	}

	public SpriteButton setRegularTexture(ResourceLocation regularTexture) {
		this.regularTexture = regularTexture;
		return this;
	}

	public SpriteButton setHoveredTexture(ResourceLocation hoveredTexture) {
		this.hoveredTexture = hoveredTexture;
		return this;
	}

	public ResourceLocation getRegularTexture() {
		return regularTexture;
	}

	public ResourceLocation getHoveredTexture() {
		return hoveredTexture;
	}

	@Override
	protected void drawButtonOverlay(PoseStack stack, int buttonLeft, int buttonTop) {
		if (getHoveredTexture() != null && (isClicked() || isHovered() || isToggled())) {
			drawable.setSprite(getHoveredTexture());
		} else {
			drawable.setSprite(getRegularTexture());
		}
		drawable.draw(buttonLeft, buttonTop);
	}
}
