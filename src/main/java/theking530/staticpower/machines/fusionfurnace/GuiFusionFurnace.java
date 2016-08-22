package theking530.staticpower.machines.fusionfurnace;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.CustomGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiPowerBar;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.utils.GuiTextures;

public class GuiFusionFurnace extends CustomGuiContainer{
	
	public GuiPowerBarFromEnergyStorage POWER_BAR;
	public GuiSideConfigTab SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, ModBlocks.FusionFurnace);
	public GuiRedstoneTab REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop);
	
	private TileEntityFusionFurnace FUSION_FURNACE;
	public GuiFusionFurnace(InventoryPlayer invPlayer, TileEntityFusionFurnace teFurnace) {
		super(new ContainerFusionFurnace(invPlayer, teFurnace));
		FUSION_FURNACE = teFurnace;
		POWER_BAR = new GuiPowerBarFromEnergyStorage(teFurnace);
		
		this.xSize = 176;
		this.ySize = 166;
		
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, FUSION_FURNACE);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, FUSION_FURNACE);
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
        if(par1 >= 8 + var1 && par2 >= 8 + var2 && par1 <= 24 + var1 && par2 <= 68 + var2) {
        	int j1 = FUSION_FURNACE.STORAGE.getEnergyStored();
        	int k1 = FUSION_FURNACE.STORAGE.getMaxEnergyStored();
        	int i1 = FUSION_FURNACE.STORAGE.getMaxReceive();
        	String text = ("Max: " + i1 + " RF/t" + "=" + NumberFormat.getNumberInstance(Locale.US).format(j1)  + "/" + NumberFormat.getNumberInstance(Locale.US).format(k1) + " " + "RF");
        	String[] splitMsg = text.split("=");
        	List temp = Arrays.asList(splitMsg);
        	drawHoveringText(temp, par1, par2, fontRendererObj); 
        }

		drawRect(guiLeft + 82, guiTop + 38, 176, 69, 3394815);

	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.FUSION_FURNACE.hasCustomName() ? this.FUSION_FURNACE.getName() : I18n.format(this.FUSION_FURNACE.getName());
	
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6,4210752 );
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FUISION_FURNACE_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = FUSION_FURNACE.getProgressScaled(17);
		drawTexturedModalRect(guiLeft + 76, guiTop + 35, 176, 69, 24, j1);	
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		POWER_BAR.drawPowerBar(guiLeft + 8, guiTop + 68, 16, 60, 1);
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


