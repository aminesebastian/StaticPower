package theking530.staticpower.machines.fusionfurnace;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;

public class GuiFusionFurnace extends BaseGuiContainer {
	
	public GuiPowerBarFromEnergyStorage POWER_BAR;
	
	private TileEntityFusionFurnace FUSION_FURNACE;
	public GuiFusionFurnace(InventoryPlayer invPlayer, TileEntityFusionFurnace teFurnace) {
		super(new ContainerFusionFurnace(invPlayer, teFurnace), 176, 166);
		FUSION_FURNACE = teFurnace;
		POWER_BAR = new GuiPowerBarFromEnergyStorage(teFurnace);
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teFurnace));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teFurnace));
		
		this.xSize = 176;
		this.ySize = 166;
		
	}
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);

    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
        if(par1 >= 8 + var1 && par2 >= 8 + var2 && par1 <= 24 + var1 && par2 <= 68 + var2) {
        	int j1 = FUSION_FURNACE.energyStorage.getEnergyStored();
        	int k1 = FUSION_FURNACE.energyStorage.getMaxEnergyStored();
        	int i1 = FUSION_FURNACE.energyStorage.getMaxReceive();
        	String text = ("Max: " + i1 + " RF/t" + "=" + NumberFormat.getNumberInstance(Locale.US).format(j1)  + "/" + NumberFormat.getNumberInstance(Locale.US).format(k1) + " " + "RF");
        	String[] splitMsg = text.split("=");
        	List<String> temp = Arrays.asList(splitMsg);
        	drawHoveringText(temp, par1, par2, fontRenderer); 
        }

		drawRect(guiLeft + 82, guiTop + 38, 176, 69, 3394815);
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.FUSION_FURNACE.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FUISION_FURNACE_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = FUSION_FURNACE.getProgressScaled(17);
		drawTexturedModalRect(guiLeft + 76, guiTop + 35, 176, 69, 24, j1);	

		POWER_BAR.drawPowerBar(guiLeft + 8, guiTop + 68, 16, 60, 1, f);;
	}	
}


