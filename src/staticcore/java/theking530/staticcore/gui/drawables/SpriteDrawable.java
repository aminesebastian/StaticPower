package theking530.staticcore.gui.drawables;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticcore.utilities.math.Vector4D;

@OnlyIn(Dist.CLIENT)
public class SpriteDrawable implements IDrawable {
	private ResourceLocation sprite;
	private Vector2D size;
	private Vector4D uv;
	private SDColor tint;

	public SpriteDrawable(ResourceLocation sprite, float width, float height) {
		this.sprite = sprite;
		this.size = new Vector2D(width, height);
		this.uv = new Vector4D(0, 0, 1, 1);
		this.tint = SDColor.WHITE;
	}

	public void setTint(SDColor tint) {
		this.tint = tint;
	}

	public SDColor getTint() {
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
	public void draw(PoseStack pose, float x, float y, float z) {
		if (sprite != null) {
			// Check to see if this is a REAL sprite, or just a texture. We could also just
			// check for a file extension, but this seems safer.
			TextureAtlasSprite atlasSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(sprite);
			if (atlasSprite.getName().toString().equals("minecraft:missingno")) {
				GuiDrawUtilities.drawTexture(pose, sprite, size.getX(), size.getY(), x, y, z, getUV().getX(), getUV().getY(), getUV().getZ(), getUV().getW(), tint);
			} else {
				GuiDrawUtilities.drawSprite(pose, sprite, size.getX(), size.getY(), x, y, z, getUV().getX(), getUV().getY(), getUV().getZ(), getUV().getW(), tint);
			}
		}
	}
}
