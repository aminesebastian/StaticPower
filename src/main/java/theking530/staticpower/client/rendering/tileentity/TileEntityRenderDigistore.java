package theking530.staticpower.client.rendering.tileentity;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import theking530.api.utilities.Color;
import theking530.staticpower.client.rendering.StaticPowerRendererTextures;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;

public class TileEntityRenderDigistore extends StaticPowerTileEntitySpecialRenderer<TileEntityDigistore> {

	public TileEntityRenderDigistore(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		// if (!tileEntity.getStoredItem().isEmpty()) {

		int lightAbove = WorldRenderer.getCombinedLight(tileEntity.getWorld(), pos.up());
		drawItemInWorld(tileEntity, new ItemStack(Items.GRASS_BLOCK), new Vector3f(0.5f, 0.57f, 1.01f), partialTicks, matrixStack, buffer, lightAbove, combinedOverlay);
		// }
		//drawFillBar(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		//drawItemBackground(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		//drawIndicators(tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		drawTextInWorld(Integer.toString(tileEntity.getStoredAmount()), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3f(0.5f, 0.315f, 1.0f), 0.01f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
	}

	public void drawIndicators(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		float offset = 0.0f;
		if (tileEntity.isLocked()) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();

			// Minecraft.getInstance().getTextureManager().bindTexture(StaticPowerRendererTextures.DIGISTORE_LOCKED_INDICATOR);
			matrixStack.push();
			vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			vertexbuffer.pos(100.0, 100, 1.0).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex(); // .tex(1.0f, 0.0f).endVertex();
			vertexbuffer.pos(100.0, 0.0, 1.0).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex(); // .tex(1.0f, 1.0f).endVertex();
			vertexbuffer.pos(0.0, 0.0, 1.0).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex(); // .tex(0.0f, 1.0f).endVertex();
			vertexbuffer.pos(0.0, 100, 1.0).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex(); // .tex(0.0f, 0.0f).endVertex();
			tessellator.draw();
			offset += 0.11f;
			matrixStack.pop();
		}
		if (tileEntity.isVoidUpgradeInstalled()) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPushMatrix();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();

			Minecraft.getInstance().getTextureManager().bindTexture(StaticPowerRendererTextures.DIGISTORE_VOID_INDICATOR);

			vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glScaled(1, 1, -1);
			GL11.glTranslated(-1, 0, 0);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			vertexbuffer.pos(0.11 + offset, 0.99, 1.005).tex(1.0f, 1.0f).endVertex();
			vertexbuffer.pos(0.11 + offset, 0.89, 1.005).tex(1.0f, 0.0f).endVertex();
			vertexbuffer.pos(0.01 + offset, 0.89, 1.005).tex(0.0f, 0.0f).endVertex();
			vertexbuffer.pos(0.01 + offset, 0.99, 1.005).tex(0.0f, 1.0f).endVertex();
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_LIGHTING);
			offset += 0.11f;
		}
	}

	public void drawItemBackground(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		GL11.glRotated(180, 0, 1, 0);
		GL11.glScaled(1, 1, -1);
		GL11.glTranslated(-1, 0, 0);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		vertexbuffer.pos(0.75, 0.80, 1.001).color(0.2f, 0.3f, 0.4f, 1.0f).endVertex();
		vertexbuffer.pos(0.75, 0.35, 1.001).color(0.2f, 0.3f, 0.4f, 1.0f).endVertex();
		vertexbuffer.pos(0.25, 0.35, 1.001).color(0.2f, 0.3f, 0.4f, 1.0f).endVertex();
		vertexbuffer.pos(0.25, 0.80, 1.001).color(0.2f, 0.3f, 0.4f, 1.0f).endVertex();
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public void drawFillBar(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		float filledRatio = 1.0f; // barrel.getFilledRatio();
		matrixStack.push();

		// BG Portion
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		Minecraft.getInstance().getTextureManager().bindTexture(StaticPowerRendererTextures.DIGISTORE_FILL_BAR);

		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(.75, 0.203125, 1.0001).tex(0.0f, 0.0f).endVertex();
		vertexbuffer.pos(.75, 0.125, 1.0001).tex(0.0f, 1.0f).endVertex();
		vertexbuffer.pos(0.75 - filledRatio * 0.5, 0.125, 1.0001).tex(filledRatio, 1.0f).endVertex();
		vertexbuffer.pos(0.75 - filledRatio * 0.5, 0.203125, 1.0001).tex(filledRatio, 0.0f).endVertex();
		tessellator.draw();
		matrixStack.pop();
	}

	public void drawText(TileEntityDigistore tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
//		GlStateManager.pushMatrix();
//		GlStateManager.disableLighting();
//		GlStateManager.enablePolygonOffset();
//		GlStateManager.depthMask(false);
//		GlStateManager.enableBlend();
//		GlStateManager.doPolygonOffset(-1, -20);
//
//		double scale = 0.01;
//		GlStateManager.scale(scale, -scale, scale);
//		GlStateManager.translate(50, -30, 100);
//
//		String text = "" + barrel.getStoredAmount();
//		getFontRenderer().drawString(text, -getFontRenderer().getStringWidth(text) / 2, 0, (int) (255 * alpha) << 24 | 255 << 16 | 255 << 8 | 255);
//
//		GlStateManager.disableBlend();
//		GlStateManager.depthMask(true);
//		GlStateManager.disablePolygonOffset();
//		GlStateManager.enableLighting();
//
//		GlStateManager.popMatrix();
	}
}
