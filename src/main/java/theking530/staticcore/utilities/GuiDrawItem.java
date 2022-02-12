package theking530.staticcore.utilities;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;

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

	protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, float alpha) {
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		itemRenderer.renderAndDecorateItem(stack, x, y);

		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		GuiDrawUtilities.drawColoredRectangle(x, y, 16, 16, 0, new Color(0.5f, 0.5f, 0.5f, 1.0f - alpha));
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
	}
}
