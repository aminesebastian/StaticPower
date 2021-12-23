package theking530.staticcore.utilities;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
		if (shouldDraw && !item.isEmpty()) {
			renderItemModelIntoGUI(item, guiLeft + x, guiTop + y, alpha);
		}
	}

	@SuppressWarnings("deprecation")
	protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, float alpha) {
		// TO-DO: Fix this later!
//		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
//
//		GlStateManager._color4f(1.0f, 1.0f, 1.0f, 1.0f - alpha);
//		renderer.renderAndDecorateItem(stack, x, y);
//		GL11.glEnable(GL11.GL_BLEND);
//		// Render overlay to fake transparency
//		GlStateManager._color4f(0.5429f, 0.5429f, 0.5429f, 1.0f - alpha);
//		GlStateManager._disableTexture();
//		GlStateManager._disableDepthTest();
//		GlStateManager._enableBlend();
//
//		Tesselator tessellator = Tesselator.getInstance();
//		BufferBuilder tes = tessellator.getBuilder();
//		tes.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
//		tes.vertex(x, y + 16, renderer.blitOffset).endVertex();
//		tes.vertex(x + 16, y + 16, renderer.blitOffset).endVertex();
//		tes.vertex(x + 16, y, renderer.blitOffset).endVertex();
//		tes.vertex(x, y, renderer.blitOffset).endVertex();
//		tessellator.end();
//		GlStateManager._enableTexture();
//		GlStateManager._color4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
