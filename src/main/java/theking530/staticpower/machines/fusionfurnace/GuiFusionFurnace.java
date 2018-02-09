package theking530.staticpower.machines.fusionfurnace;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;

public class GuiFusionFurnace extends BaseGuiContainer {
	
	private TileEntityFusionFurnace fusionFurnace;
	
	public GuiFusionFurnace(InventoryPlayer invPlayer, TileEntityFusionFurnace teFurnace) {
		super(new ContainerFusionFurnace(invPlayer, teFurnace), 176, 166);
		fusionFurnace = teFurnace;
		registerWidget(new GuiPowerBarFromEnergyStorage(teFurnace, 8, 68, 16, 60));
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teFurnace));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teFurnace));
		
		
		GuiMachinePowerInfoTab powerInfoTab;
		getTabManager().registerTab(powerInfoTab = new GuiMachinePowerInfoTab(80, 80, teFurnace));
		powerInfoTab.setTabSide(TabSide.LEFT);	
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.fusionFurnace.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FUISION_FURNACE_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = fusionFurnace.getProgressScaled(17);
		drawTexturedModalRect(guiLeft + 76, guiTop + 35, 176, 69, 24, j1);	
	}	
}


