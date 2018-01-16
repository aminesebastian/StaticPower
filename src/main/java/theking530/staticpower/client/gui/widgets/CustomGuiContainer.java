package theking530.staticpower.client.gui.widgets;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class CustomGuiContainer extends GuiContainer{

	public CustomGuiContainer(Container container) { 
		super(container);
	}
    protected void drawItemStack(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        FontRenderer font = null;
        if (p_146982_1_ != null) font = p_146982_1_.getItem().getFontRenderer(p_146982_1_);
        if (font == null) font = fontRenderer;
        itemRender.renderItemAndEffectIntoGUI(p_146982_1_, p_146982_2_, p_146982_3_);
        itemRender.renderItemOverlayIntoGUI(font, p_146982_1_, p_146982_2_, p_146982_3_ - 0, p_146982_4_);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {		
	}

}
