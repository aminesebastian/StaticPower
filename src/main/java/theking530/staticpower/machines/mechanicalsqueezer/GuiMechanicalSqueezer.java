	package theking530.staticpower.machines.mechanicalsqueezer;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.utils.GuiTextures;

public class GuiMechanicalSqueezer extends GuiContainer{
	
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	
	private TileEntityMechanicalSqueezer cSqueezer;
	private GuiFluidBarFromTank FLUIDBAR;
	private static ContainerMechanicalSqueezer CONTAINER;
	
	public GuiMechanicalSqueezer(InventoryPlayer invPlayer, TileEntityMechanicalSqueezer teCropSqueezer) {
		super(CONTAINER = new ContainerMechanicalSqueezer(invPlayer, teCropSqueezer));
		cSqueezer = teCropSqueezer;
		FLUIDBAR = new GuiFluidBarFromTank(teCropSqueezer.TANK);
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teCropSqueezer);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teCropSqueezer);
		this.xSize = 195;
		this.ySize = 166;		
	}
	public void updateScreen() {
		SIDE_TAB.updateTab(width+38, height, xSize, ySize, fontRenderer, cSqueezer);
		REDSTONE_TAB.updateTab(width+38, height, xSize, ySize, fontRenderer, cSqueezer);
		CONTAINER.moveUpgradeSlots(100, 100);
		if(SIDE_TAB.GROWTH_STATE == 1){
			REDSTONE_TAB.RED_TAB.GROWTH_STATE = 2;
		}
		if(REDSTONE_TAB.GROWTH_STATE == 1) {
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}
	}
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		int var1 = (this.width - this.xSize) / 2;
		int var2 = (this.height - this.ySize) / 2;
		if(par1 >= 30 + var1 && par2 >= 8 + var2 && par1 <= 46 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRenderer); 
		} 
		this.renderHoveredToolTip(par1, par2);
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.cSqueezer.getName());	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 + 18, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 27, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.MSQUEEZER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = cSqueezer.getProgressScaled(15);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.MSQUEEZER_GUI);
		drawTexturedModalRect(guiLeft + 107, guiTop + 34, 198, 71, 14, 1+j1);
			
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		FLUIDBAR.drawFluidBar(guiLeft + 30, guiTop + 68, 16, 60, this.zLevel);
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


