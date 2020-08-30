package theking530.staticpower.tileentities.powered.heatsink;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiHeatSink extends StaticPowerTileEntityGui<ContainerHeatSink, TileEntityHeatSink> {
	protected GuiInfoTab infoTab;

	public GuiHeatSink(ContainerHeatSink container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 146);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 46));
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().cableComponent, 64, 16, 48, 44));
		
		getTabManager().registerTab(new GuiInfoTab(80));
		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
	}

	@Override
	protected void drawForegroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(partialTicks, mouseX, mouseY);

	}
}
