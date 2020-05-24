package theking530.staticpower.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentity.TileEntityBase;

public abstract class StaticPowerTileEntitySpecialRenderer<T extends TileEntityBase> extends TileEntitySpecialRenderer<T> {
	@Override
	public void render(T tileentity, double translationX, double translationY, double translationZ, float f, int dest, float alpha) {
		EnumFacing facing = tileentity.getFacingDirection();

		GL11.glPushMatrix();
		GL11.glTranslated(translationX, translationY, translationZ);
		if (facing == EnumFacing.WEST) {
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(0, 0, -1);
		}
		if (facing == EnumFacing.NORTH) {
			GL11.glRotated(180, 0, 1, 0);
			GL11.glTranslated(-1, 0, -1);
		}
		if (facing == EnumFacing.EAST) {
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-1, 0, 0);
		}

		// Draw the tile entity.
		try {
			renderTileEntityBase(tileentity, translationX, translationY, translationZ, f, dest, alpha);
		}catch(Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to draw tile entity base: %1$s.",  tileentity, e);
		}


		GL11.glTranslated(-translationX, -translationY, -translationZ);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	public void renderTileEntityBase(T tileentity, double translationX, double translationY, double translationZ, float f, int dest, float alpha) {

	}

	public void drawItemInWorld(ItemStack item, double translationX, double translationY, double translationZ, float alpha) {
		GlStateManager.enableTexture2D();

		GlStateManager.pushMatrix();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableLighting();
		double scale = 0.02;
		GlStateManager.scale(scale, -scale, scale / 10);
		GlStateManager.translate(17, -37, 404);

		final int lightmapCoords = 15728881;
		final int skyLight = lightmapCoords % 65536;
		final int blockLight = lightmapCoords / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, skyLight, blockLight);

		try {
			Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(item, 0, 0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.popMatrix();
	}
}
