package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public class SpriteButton extends StandardButton {
	private ResourceLocation regularTexture;
	private ResourceLocation hoveredTexture;

	public SpriteButton(int xPos, int yPos, int width, int height, ResourceLocation sprite, @Nullable ResourceLocation hoveredSprite, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.regularTexture = sprite;
		this.hoveredTexture = hoveredSprite;
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

	@SuppressWarnings("deprecation")
	@Override
	protected void drawButtonOverlay(MatrixStack stack, int buttonLeft, int buttonTop) {
		GlStateManager.enableBlend();
		GL11.glDisable(GL11.GL_CULL_FACE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder tes = tessellator.getBuffer();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

		TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(getRegularTexture());
		if (getHoveredTexture() != null && (isClicked() || isHovered() || isToggled())) {
			sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(getHoveredTexture());
		}

		Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		Color tint = Color.WHITE;
		vertexbuffer.pos(buttonLeft, buttonTop + getSize().getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
		vertexbuffer.pos(buttonLeft, buttonTop, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
		vertexbuffer.pos(buttonLeft + getSize().getX(), buttonTop, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
		vertexbuffer.pos(buttonLeft + getSize().getX(), buttonTop + getSize().getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();

		tessellator.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
