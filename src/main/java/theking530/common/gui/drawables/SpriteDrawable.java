package theking530.common.gui.drawables;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import theking530.common.utilities.Color;
import theking530.common.utilities.Vector2D;

public class SpriteDrawable implements IDrawable {
	private final ResourceLocation sprite;
	private Vector2D size;
	private Color tint;

	public SpriteDrawable(ResourceLocation sprite, int width, int height) {
		this.sprite = sprite;
		this.size = new Vector2D(width, height);
		this.tint = Color.WHITE;
	}

	public void setTint(Color tint) {
		this.tint = tint;
	}

	@SuppressWarnings("deprecation")
	public TextureAtlasSprite getSpriteTexture() {
		return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(sprite);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(int x, int y) {
		if (sprite != null) {
			// Turn on the blending.
			GL11.glEnable(GL11.GL_BLEND);

			// Allocate the rendering utilities/buffers.
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder tes = tessellator.getBuffer();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

			// Get the actual texture.
			TextureAtlasSprite spriteTexture = getSpriteTexture();

			if (spriteTexture.getName().toString().equals("minecraft:missingno")) {
				// Bind the texture atlas.
				Minecraft.getInstance().getTextureManager().bindTexture(sprite);
				// Draw the sprite.
				vertexbuffer.pos(x, y + size.getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(0.0f, 1.0f).endVertex();
				vertexbuffer.pos(x, y, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(0.0f, 0.0f).endVertex();
				vertexbuffer.pos(x + size.getX(), y, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(1.0f, 0.0f).endVertex();
				vertexbuffer.pos(x + size.getX(), y + size.getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(1.0f, 1.0f).endVertex();
				tessellator.draw();

			} else {
				Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
				// Draw the sprite.
				vertexbuffer.pos(x, y + size.getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(spriteTexture.getMinU(), spriteTexture.getMaxV()).endVertex();
				vertexbuffer.pos(x, y, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(spriteTexture.getMinU(), spriteTexture.getMinV()).endVertex();
				vertexbuffer.pos(x + size.getX(), y, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(spriteTexture.getMaxU(), spriteTexture.getMinV()).endVertex();
				vertexbuffer.pos(x + size.getX(), y + size.getY(), 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(spriteTexture.getMaxU(), spriteTexture.getMaxV()).endVertex();
				tessellator.draw();
			}

			// Turn off blending.
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
