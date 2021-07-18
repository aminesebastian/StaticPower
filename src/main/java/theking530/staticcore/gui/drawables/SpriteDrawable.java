package theking530.staticcore.gui.drawables;

import org.lwjgl.opengl.GL11;

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
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector4D;

@OnlyIn(Dist.CLIENT)
public class SpriteDrawable implements IDrawable {
	private ResourceLocation sprite;
	private Vector2D size;
	private Vector4D uv;
	private Color tint;

	public SpriteDrawable(ResourceLocation sprite, float width, float height) {
		this.sprite = sprite;
		this.size = new Vector2D(width, height);
		this.uv = new Vector4D(0, 0, 1, 1);
		this.tint = Color.WHITE;
	}

	public void setTint(Color tint) {
		this.tint = tint;
	}

	public Color getTint() {
		return tint;
	}

	public Vector2D getSize() {
		return size;
	}

	@Override
	public void setSize(float width, float height) {
		size.setX(width);
		size.setY(height);
	}

	public Vector4D getUV() {
		return uv;
	}

	public void setUV(float minU, float minV, float maxU, float maxV) {
		uv.setX(minU);
		uv.setY(minV);
		uv.setZ(maxU);
		uv.setW(maxV);
	}

	@SuppressWarnings("deprecation")
	public TextureAtlasSprite getSpriteTexture() {
		return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(sprite);
	}

	public void setSprite(ResourceLocation sprite) {
		this.sprite = sprite;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(float x, float y, float z) {
		if (sprite != null) {
			// Allocate the rendering utilities/buffers.
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder tes = tessellator.getBuffer();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

			// Get the actual texture.
			TextureAtlasSprite spriteTexture = getSpriteTexture();

			// Turn on the blending just in case its not on.
			GlStateManager.enableBlend();
			GL11.glDisable(GL11.GL_CULL_FACE);

			if (spriteTexture.getName().toString().equals("minecraft:missingno")) {
				// Bind the texture.
				Minecraft.getInstance().getTextureManager().bindTexture(sprite);
				// Draw the sprite.
				vertexbuffer.pos(x, y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(uv.getX(), uv.getW()).endVertex();
				vertexbuffer.pos(x, y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(uv.getX(), uv.getY()).endVertex();
				vertexbuffer.pos(x + size.getX(), y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(uv.getZ(), uv.getY()).endVertex();
				vertexbuffer.pos(x + size.getX(), y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(uv.getZ(), uv.getW()).endVertex();
				tessellator.draw();

			} else {
				// TO-DO: Implement UV here.
				Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
				// Draw the sprite.
				vertexbuffer.pos(x, y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(spriteTexture.getMinU(), spriteTexture.getMaxV())
						.endVertex();
				vertexbuffer.pos(x, y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(spriteTexture.getMinU(), spriteTexture.getMinV()).endVertex();
				vertexbuffer.pos(x + size.getX(), y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(spriteTexture.getMaxU(), spriteTexture.getMinV())
						.endVertex();
				vertexbuffer.pos(x + size.getX(), y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
						.tex(spriteTexture.getMaxU(), spriteTexture.getMaxV()).endVertex();
				tessellator.draw();
			}
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
	}
}
