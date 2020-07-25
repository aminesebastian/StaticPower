package theking530.common.gui.widgets;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theking530.common.utilities.Color;
import theking530.staticpower.StaticPower;

public class GuiDrawItem {
	protected Color tint;
	protected boolean shouldDraw;

	public GuiDrawItem() {
		shouldDraw = true;
	}

	public void setShouldDraw(boolean ShouldDraw) {
		shouldDraw = ShouldDraw;
	}

	public void drawItem(Item item, int guiLeft, int guiTop, int x, int y, float alpha) {
		if (shouldDraw) {
			drawItem(new ItemStack(item), guiLeft, guiTop, x, y, alpha);
		}
	}

	public void drawItem(ItemStack item, int guiLeft, int guiTop, int x, int y, float alpha) {
		if (shouldDraw) {
			if (item != null) {
				renderItemModelIntoGUI(item, guiLeft + x, guiTop + y, alpha);
			} else {
				StaticPower.LOGGER.error("Attempting to draw a null itemstack into a UI.");
			}
		}
	}

	@SuppressWarnings("deprecation")
	protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, float alpha) {
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f - alpha);
		renderer.renderItemAndEffectIntoGUI(stack, x, y);
		GL11.glEnable(GL11.GL_BLEND);
		// Render overlay to fake transparency
		GlStateManager.color4f(0.5429f, 0.5429f, 0.5429f, 1.0f - alpha);
		GlStateManager.disableTexture();
		GlStateManager.disableDepthTest();
		GlStateManager.enableBlend();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder tes = tessellator.getBuffer();
		tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		tes.pos(x, y + 16, renderer.zLevel).endVertex();
		tes.pos(x + 16, y + 16, renderer.zLevel).endVertex();
		tes.pos(x + 16, y, renderer.zLevel).endVertex();
		tes.pos(x, y, renderer.zLevel).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
