package theking530.staticpower.machines.chargingstation;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;

public class GuiChargingStation extends BaseGuiContainer {
		
	private TileEntityChargingStation chargingStation;
	
	public GuiChargingStation(InventoryPlayer invPlayer, TileEntityChargingStation teCharging) {
		super(new ContainerChargingStation(invPlayer, teCharging), 197, 176);
		
		chargingStation = teCharging;		
		registerWidget(new GuiPowerBarFromEnergyStorage(teCharging, 8, 68, 16, 60));
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teCharging));
		getTabManager().registerTab(new GuiSideConfigTab(80, 80, teCharging));
		
		GuiMachinePowerInfoTab powerInfoTab;
		getTabManager().registerTab(powerInfoTab = new GuiMachinePowerInfoTab(80, 80, teCharging));
		powerInfoTab.setTabSide(TabSide.LEFT);	
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.chargingStation.getName());	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.CHARGING_STATION_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		//Progress Bar
		int j1 = chargingStation.getProgressScaled(24);
		drawTexturedModalRect(guiLeft + 73, guiTop + 32, 176, 69, j1, 14);	
	}
}


