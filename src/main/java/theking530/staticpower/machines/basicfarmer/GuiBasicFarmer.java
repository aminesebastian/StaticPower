package theking530.staticpower.machines.basicfarmer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.GuiPowerBar;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.utils.GUIUtilities;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.WorldUtilities;

public class GuiBasicFarmer extends GuiContainer{
	
	public GuiPowerBarFromEnergyStorage POWER_BAR;
	public GuiSideConfigTab SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, ModBlocks.PoweredFurnace);
	public GuiRedstoneTab REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop);
	
	private TileEntityBasicFarmer FARMER;
	public GuiBasicFarmer(InventoryPlayer invPlayer, TileEntityBasicFarmer teFarmer) {
		super(new ContainerBasicFarmer(invPlayer, teFarmer));
		FARMER = teFarmer;
		POWER_BAR = new GuiPowerBarFromEnergyStorage(teFarmer);
		
		this.xSize = 176;
		this.ySize = 172;
		
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, FARMER);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, FARMER);
		if(SIDE_TAB.GROWTH_STATE == 1){
			REDSTONE_TAB.RED_TAB.GROWTH_STATE = 2;
		}
		if(REDSTONE_TAB.GROWTH_STATE == 1) {
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}
	}	
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
        if(par1 >= 8 + var1 && par2 >= 8 + var2 && par1 <= 14 + var1 && par2 <= 68 + var2) {
        	drawHoveringText(POWER_BAR.drawText(), par1, par2, fontRendererObj); 
        }
	}	

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.FARMER.getName());
	
		//this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2 + 10, 6,4210752 );
		//this.fontRendererObj.drawString(I18n.format("container.inventory"), 30, this.ySize - 96 + 3, 4210752);
		
		float scale = 0.7F;
		GL11.glScalef(scale, scale, scale);
		
		String radius = "Radius: " + FARMER.RANGE;	
		fontRendererObj.drawString(radius, xSize / 2 - 50, 117, GUIUtilities.getColor(20, 20, 20));
		
		GL11.glScalef(1/scale, 1/scale, 1/scale);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BASIC_FARMER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		//Tabs
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();		
		//Energy Bar
		POWER_BAR.drawPowerBar(guiLeft + 8, guiTop + 68, 6, 60, 1);
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


