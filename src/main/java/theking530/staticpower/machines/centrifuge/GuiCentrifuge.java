package theking530.staticpower.machines.centrifuge;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.utilities.GuiUtilities;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.progressbars.GrinderProgressBar;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;

public class GuiCentrifuge extends BaseGuiContainer{

	private TileCentrifuge centrifuge;
	private GuiInfoTab infoTab;

	public GuiCentrifuge(InventoryPlayer invPlayer, TileCentrifuge tileEntityCentrifuge) {
		super(new ContainerCentrifuge(invPlayer, tileEntityCentrifuge), 176, 166);
		centrifuge = tileEntityCentrifuge;
		registerWidget(new GuiPowerBarFromEnergyStorage(tileEntityCentrifuge, 8, 62, 16, 54));
		registerWidget(new GrinderProgressBar(tileEntityCentrifuge, 80, 39, 16, 14));
		
		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 70));
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, tileEntityCentrifuge));
		getTabManager().registerTab(new GuiSideConfigTab(80, 80, false, tileEntityCentrifuge));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(80, 80, tileEntityCentrifuge).setTabSide(TabSide.LEFT));

		setOutputSlotSize(16);
	}	
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String formerName = I18n.format(this.centrifuge.getName());
		this.fontRenderer.drawString(formerName, this.xSize / 2 - this.fontRenderer.getStringWidth(formerName) / 2, 6, 4210752);
	}
	@Override
	protected void drawExtra(float f, int i, int j) {
		drawGenericBackground();
		drawContainerSlots(centrifuge, inventorySlots.inventorySlots);
		drawPlayerInventorySlots();		
		
		int barXPos = 60;
		int barYPos = 30;
		int barWidth = 2;
		int barHeight = 16;
		
		int barFill = (int) (16 * centrifuge.getMultiplier());
		drawSlot(guiLeft+barXPos, guiTop+barYPos, barWidth, barHeight);
		Gui.drawRect(guiLeft+barXPos, guiTop+barYPos+(barHeight-barFill), guiLeft+barXPos+barWidth, guiTop+barYPos+barHeight, GuiUtilities.getColor(175, 0, 0, 255));
		
		String formerInfo = "The centrifuge seperates=items into other items=by spinning them in=a separation chamber.=Current Speed: " + centrifuge.getRotationSpeed() + " RPM";
		infoTab.setText("Centrifuge", formerInfo);
	}	
}


