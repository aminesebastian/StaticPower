package theking530.staticpower.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import api.gui.IInteractableGui;
import api.gui.button.BaseButton;
import api.gui.button.ButtonManager;
import api.gui.tab.GuiTabManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.Vec3i;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.assists.utilities.GuiUtilities;

public abstract class BaseGuiContainer extends GuiContainer implements IInteractableGui {

	private GuiTabManager tabManager;
	private ButtonManager buttonManager;
	
	private static final float genericBackgroundPixel = 1.0f/9.0f;
	public BaseGuiContainer(Container inventorySlotsIn, int guiXSize, int guiYSize) {
		super(inventorySlotsIn);
		xSize = guiXSize;
		ySize = guiYSize;
		tabManager = new GuiTabManager(this);
		buttonManager = new ButtonManager(this);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawExtra(partialTicks, mouseX, mouseY);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
		drawTabs(partialTicks);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
		drawButtons(mouseX, mouseY);
	}
	protected void drawButtons(int mouseX, int mouseY) {
		buttonManager.drawButtons(mouseX, mouseY);
	}
	protected void drawTabs(float partialTicks) {
		getTabManager().drawTabs(guiLeft+xSize-1, guiTop+10, xSize, ySize, partialTicks);
	}
	protected abstract void drawExtra(float partialTicks, int mouseX, int mouseY);
	
	public GuiTabManager getTabManager() {
		return tabManager;
	}
	public ButtonManager getButtonManager() {
		return buttonManager;
	}
	public void drawGenericBackground()  {
		drawGenericBackground(xSize, ySize);
	}
	public void drawGenericBackground(int width, int height) {		

		//MainBG
		Gui.drawRect(guiLeft+3, guiTop+3, guiLeft+width-3, guiTop+height-3, GuiUtilities.getColor(198, 198, 198));
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.GENERIC_GUI);
		GL11.glEnable(GL11.GL_BLEND);
		//Corners
		drawTexturedGenericRect(guiLeft, guiTop, 4, 4, 0.0f, 0.0f, 4*genericBackgroundPixel, 4*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft+width-5, guiTop, 5, 4, 4*genericBackgroundPixel, 0.0f, 9*genericBackgroundPixel, 4*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft, guiTop+height-5, 4, 5, 0.0f, 4*genericBackgroundPixel, 4*genericBackgroundPixel, 9*genericBackgroundPixel);	
		drawTexturedGenericRect(guiLeft+width-4, guiTop+height-4, 4, 4, 5*genericBackgroundPixel, 5*genericBackgroundPixel, 9*genericBackgroundPixel, 9*genericBackgroundPixel);

		//Sides
		drawTexturedGenericRect(guiLeft+4, guiTop, width-7, 3, 3*genericBackgroundPixel, 0.0f, 4*genericBackgroundPixel, 3*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft, guiTop+4, 3, height-8, 0.0f, 3*genericBackgroundPixel, 3*genericBackgroundPixel, 4*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft+4, guiTop+height-3, width-8, 3, 4*genericBackgroundPixel, 6*genericBackgroundPixel, 5*genericBackgroundPixel, 9*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft+width-3, guiTop+4, 3, height-8, 6*genericBackgroundPixel, 3*genericBackgroundPixel, 9*genericBackgroundPixel, 4*genericBackgroundPixel);
		GL11.glDisable(GL11.GL_BLEND);
	}
	public void drawPlayerInventorySlots() {
		drawPlayerInventorySlots(guiLeft+(xSize-162)/2+1, guiTop+ySize-83);
	}
	public void drawPlayerInventorySlots(int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				drawSlot(xPos + j * 18, yPos + 1 + i * 18, 16, 16);
			}
		}
		for (int i = 0; i < 9; i++) {
			drawSlot(xPos + i * 18, yPos+59, 16, 16);
		}
	}
	public void drawSlot(int xPos, int yPos, int width, int height) {
		Gui.drawRect(xPos-1, yPos-1, xPos, yPos+height, GuiUtilities.getColor(55, 55, 55));
		Gui.drawRect(xPos, yPos-1, xPos+width, yPos, GuiUtilities.getColor(55, 55, 55));
		Gui.drawRect(xPos+width, yPos-1, xPos+width+1, yPos, GuiUtilities.getColor(139, 139, 139));
		
		Gui.drawRect(xPos-1, yPos+height, xPos, yPos+height+1, GuiUtilities.getColor(139, 139, 139));
		Gui.drawRect(xPos, yPos+height, xPos+width+1, yPos+height+1, GuiUtilities.getColor(255, 255, 255));
		Gui.drawRect(xPos+width, yPos, xPos+width+1, yPos+height+1, GuiUtilities.getColor(255, 255, 255));
		
		Gui.drawRect(xPos, yPos, xPos+width, yPos+height, GuiUtilities.getColor(139, 139, 139));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    tabManager.handleMouseInteraction(x, y, button);
	    buttonManager.handleMouseInteraction(x, y, button);
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
        this.drawGradientRect(0, 0, this.width, this.height, GuiUtilities.getColor(startColor.getX(), startColor.getY(), startColor.getZ()), GuiUtilities.getColor(endColor.getX(), endColor.getY(), endColor.getZ()));
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
    public void drawTexturedGenericRect(int xCoord, int yCoord, int width, int height, double minU, double minV, double maxU, double maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(xCoord, yCoord+height, this.zLevel).tex(minU, maxV).endVertex();
        bufferbuilder.pos(xCoord+width, yCoord+height, this.zLevel).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(xCoord+width, yCoord, this.zLevel).tex(maxU, minV).endVertex();
        bufferbuilder.pos(xCoord, yCoord, this.zLevel).tex(minU, minV).endVertex();
        tessellator.draw();
    }
	@Override
	public void buttonPressed(BaseButton button) {
		
	}
}
