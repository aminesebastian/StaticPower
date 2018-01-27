package theking530.staticpower.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.Vec3i;
import theking530.staticpower.client.gui.widgets.GuiTabManager;
import theking530.staticpower.utils.GUIUtilities;

public abstract class BaseGuiContainer extends GuiContainer {

	private GuiTabManager tabManager;
	
	public BaseGuiContainer(Container inventorySlotsIn, int guiXSize, int guiYSize) {
		super(inventorySlotsIn);
		xSize = guiXSize;
		ySize = guiYSize;
		tabManager = new GuiTabManager(this);
	}
	@Override
	protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);
	
	public GuiTabManager getTabManager() {
		return tabManager;
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
		this.renderHoveredToolTip(par1, par2);
	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    tabManager.handleMouseInteraction(x, y, button);
	}	
	@Override
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		tabManager.handleMouseClickMove(x, y, button, time);
	}
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
        super.keyTyped(par1, par2);
	    tabManager.handleKeyboardInteraction(par1, par2);
    }
    public void drawCustomBackground(Vec3i startColor, Vec3i endColor) {
        this.drawGradientRect(0, 0, this.width, this.height, GUIUtilities.getColor(startColor.getX(), startColor.getY(), startColor.getZ()), GUIUtilities.getColor(endColor.getX(), endColor.getY(), endColor.getZ()));
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
    }
	@Override
    public void drawDefaultBackground() {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
    }
	@Override
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
