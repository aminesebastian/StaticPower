package theking530.staticcore.gui.drawables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
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

	public ResourceLocation getSpriteTexture() {
		return sprite;
	}

	public void setSprite(ResourceLocation sprite) {
		this.sprite = sprite;
	}

	@Override
	public void draw(float x, float y, float z) {
		if (sprite != null) {
			// Check to see if this is a REAL sprite, or just a texture. We could also just
			// check for a file extension, but this seems safer.
			@SuppressWarnings("deprecation")
			TextureAtlasSprite atlasSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(sprite);
			if (atlasSprite.getName().toString().equals("minecraft:missingno")) {
				GuiDrawUtilities.drawTexturedModalRect(sprite, null, x, y, z, size.getX(), size.getY(), getUV().getX(), getUV().getY(), getUV().getZ(), getUV().getW(), tint);
			} else {
				GuiDrawUtilities.drawTexturedModalSprite(sprite, null, x, y, z, size.getX(), size.getY(), getUV().getX(), getUV().getY(), getUV().getZ(), getUV().getW(), tint);
			}
		}
	}
}
