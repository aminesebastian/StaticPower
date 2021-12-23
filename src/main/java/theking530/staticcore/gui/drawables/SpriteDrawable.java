package theking530.staticcore.gui.drawables;

import org.lwjgl.opengl.GL11;

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
		return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(sprite);
	}

	public void setSprite(ResourceLocation sprite) {
		this.sprite = sprite;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(float x, float y, float z) {
		if (sprite != null) {
			// Allocate the rendering utilities/buffers.
			Tesselator tessellator = Tesselator.getInstance();
			BufferBuilder tes = tessellator.getBuilder();
			BufferBuilder vertexbuffer = tessellator.getBuilder();
			tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

			// Get the actual texture.
			TextureAtlasSprite spriteTexture = getSpriteTexture();

			// Turn on the blending just in case its not on.
			GlStateManager._enableBlend();
			GL11.glDisable(GL11.GL_CULL_FACE);

			if (spriteTexture.getName().toString().equals("minecraft:missingno")) {
				// Bind the texture.
				Minecraft.getInstance().getTextureManager().bindForSetup(sprite);
				// Draw the sprite.
				vertexbuffer.vertex(x, y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(uv.getX(), uv.getW()).endVertex();
				vertexbuffer.vertex(x, y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(uv.getX(), uv.getY()).endVertex();
				vertexbuffer.vertex(x + size.getX(), y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(uv.getZ(), uv.getY()).endVertex();
				vertexbuffer.vertex(x + size.getX(), y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(uv.getZ(), uv.getW()).endVertex();
				tessellator.end();

			} else {
				// TO-DO: Implement UV here.
				Minecraft.getInstance().getTextureManager().bindForSetup(TextureAtlas.LOCATION_BLOCKS);
				// Draw the sprite.
				vertexbuffer.vertex(x, y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(spriteTexture.getU0(), spriteTexture.getV1())
						.endVertex();
				vertexbuffer.vertex(x, y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(spriteTexture.getU0(), spriteTexture.getV0()).endVertex();
				vertexbuffer.vertex(x + size.getX(), y, z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(spriteTexture.getU1(), spriteTexture.getV0())
						.endVertex();
				vertexbuffer.vertex(x + size.getX(), y + size.getY(), z).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
						.uv(spriteTexture.getU1(), spriteTexture.getV1()).endVertex();
				tessellator.end();
			}
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
	}
}
