package theking530.staticpower.machines.poweredfurnace;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.widgets.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.utils.GuiTextures;

public class GuiPoweredFurnace extends BaseGuiContainer {
	
	public GuiPowerBarFromEnergyStorage POWER_BAR;
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	
	private TileEntityPoweredFurnace Smelter;
	public GuiPoweredFurnace(InventoryPlayer invPlayer, TileEntityPoweredFurnace teSmelter) {
		super(new ContainerPoweredFurnace(invPlayer, teSmelter), 176, 166);
		Smelter = teSmelter;
		POWER_BAR = new GuiPowerBarFromEnergyStorage(teSmelter);
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 100, teSmelter));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teSmelter));	
	}
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    	
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;

		
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
        if(par1 >= 8 + var1 && par2 >= 8 + var2 && par1 <= 24 + var1 && par2 <= 62 + var2) {
        	drawHoveringText(POWER_BAR.drawText(), par1, par2, fontRenderer);  
        }
		this.renderHoveredToolTip(par1, par2);
	}	

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.Smelter.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
		//this.fontRenderer.drawString(I18n.format("container.inventory"), 26, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FURNACE_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		//Progress Bar
		int j1 = Smelter.getProgressScaled(24);
		drawTexturedModalRect(guiLeft + 73, guiTop + 32, 176, 69, j1, 14);	
		//Tabs		
        getTabManager().drawTabs(guiLeft+175, guiTop+10, width, height, f);
		//Flames
		if(Smelter.isProcessing()) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FURNACE_GUI);
			drawTexturedModalRect(guiLeft + 51, guiTop + 50, 176, 55, 14, 14);	
		}	
		//Energy Bar
		POWER_BAR.drawPowerBar(guiLeft + 8, guiTop + 62, 16, 54, 1, f);
	}
}


