package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
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
	protected void drawButtonOverlay(PoseStack stack, int buttonLeft, int buttonTop) {
		GlStateManager._enableBlend();
		GL11.glDisable(GL11.GL_CULL_FACE);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder tes = tessellator.getBuilder();
		BufferBuilder vertexbuffer = tessellator.getBuilder();
		tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(getRegularTexture());
		if (getHoveredTexture() != null && (isClicked() || isHovered() || isToggled())) {
			sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(getHoveredTexture());
		}

		Minecraft.getInstance().getTextureManager().bindForSetup(TextureAtlas.LOCATION_BLOCKS);
		Color tint = Color.WHITE;
		vertexbuffer.vertex(buttonLeft, buttonTop + getSize().getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV1()).endVertex();
		vertexbuffer.vertex(buttonLeft, buttonTop, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0()).endVertex();
		vertexbuffer.vertex(buttonLeft + getSize().getX(), buttonTop, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU1(), sprite.getV0()).endVertex();
		vertexbuffer.vertex(buttonLeft + getSize().getX(), buttonTop + getSize().getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU1(), sprite.getV1()).endVertex();

		tessellator.end();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
