package theking530.staticpower.machines.poweredgrinder;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.utils.GuiTextures;

public class GuiPoweredGrinder extends BaseGuiContainer{
	
	private GuiPowerBarFromEnergyStorage POWERBAR;
	
	private TileEntityPoweredGrinder Grinder;
	public GuiPoweredGrinder(InventoryPlayer invPlayer, TileEntityPoweredGrinder teGrinder) {
		super(new ContainerPoweredGrinder(invPlayer, teGrinder), 176, 166);
		Grinder = teGrinder;
		POWERBAR = new GuiPowerBarFromEnergyStorage(teGrinder);

		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teGrinder));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teGrinder));
	}	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
		
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
        if(mouseX >= 8 + var1 && mouseY >= 8 + var2 && mouseX <= 24 + var1 && mouseY <= 62 + var2) {
        	drawHoveringText(POWERBAR.drawText(), mouseX, mouseY, fontRenderer); 
        }
		drawRect(guiLeft + 82, guiTop + 38, 176, 69, 3394815);	
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String NAME = I18n.format(this.Grinder.getName());
		this.fontRenderer.drawString(NAME, this.xSize / 2 - this.fontRenderer.getStringWidth(NAME) / 2, 6, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.GRINDER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = Grinder.getProgressScaled(17);
		drawTexturedModalRect(guiLeft + 76, guiTop + 38, 176, 69, 24, j1);	
		POWERBAR.drawPowerBar(guiLeft + 8, guiTop + 62, 16, 54, zLevel, f);
		
		
        getTabManager().drawTabs(guiLeft+175, guiTop+10, width, height, f);
	}	
}


