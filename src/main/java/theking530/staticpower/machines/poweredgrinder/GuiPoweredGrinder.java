package theking530.staticpower.machines.poweredgrinder;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.widgets.CustomGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.utils.GuiTextures;

public class GuiPoweredGrinder extends CustomGuiContainer{
	
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	private GuiPowerBarFromEnergyStorage POWERBAR;
	
	private TileEntityPoweredGrinder Grinder;
	public GuiPoweredGrinder(InventoryPlayer invPlayer, TileEntityPoweredGrinder teGrinder) {
		super(new ContainerPoweredGrinder(invPlayer, teGrinder));
		Grinder = teGrinder;
		POWERBAR = new GuiPowerBarFromEnergyStorage(teGrinder);
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teGrinder);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teGrinder);
		this.xSize = 176;
		this.ySize = 166;
		
	}
	public void updateScreen() {
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRenderer, Grinder);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRenderer, Grinder);
		if(SIDE_TAB.GROWTH_STATE == 1){
			REDSTONE_TAB.RED_TAB.GROWTH_STATE = 2;
		}
		if(REDSTONE_TAB.GROWTH_STATE == 1) {
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}
	}	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
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
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.GRINDER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = Grinder.getProgressScaled(17);
		drawTexturedModalRect(guiLeft + 76, guiTop + 38, 176, 69, 24, j1);	
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		POWERBAR.drawPowerBar(guiLeft + 8, guiTop + 62, 16, 54, zLevel, f);
	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    REDSTONE_TAB.mouseInteraction(x, y, button);
	    SIDE_TAB.mouseInteraction(x, y, button);
	}	
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		SIDE_TAB.mouseDrag(x, y, button, time);
	}	
}


