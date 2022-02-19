package theking530.staticcore.utilities;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

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

	public void drawItem(PoseStack pose, Item item, int guiLeft, int guiTop, int x, int y, float alpha) {
		if (shouldDraw) {
			drawItem(pose,new ItemStack(item), guiLeft, guiTop, x, y, alpha);
		}
	}

	public void drawItem(PoseStack pose,ItemStack item, int guiLeft, int guiTop, int x, int y, float alpha) {
		if (shouldDraw && !item.isEmpty()) {
			renderItemModelIntoGUI(pose, item, guiLeft + x, guiTop + y, alpha);
		}
	}

	protected void renderItemModelIntoGUI(PoseStack pose,ItemStack stack, int x, int y, float alpha) {
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		itemRenderer.renderAndDecorateItem(stack, x, y);

		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		GuiDrawUtilities.drawRectangle(pose, 16, 16,x,y, 0, new Color(0.5f, 0.5f, 0.5f, 1.0f - alpha));
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
	}
}
