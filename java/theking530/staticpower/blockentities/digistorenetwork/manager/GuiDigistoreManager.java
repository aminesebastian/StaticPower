package theking530.staticpower.blockentities.digistorenetwork.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiDigistoreManager extends StaticPowerTileEntityGui<ContainerDigistoreManager, TileEntityDigistoreManager> {

	public GuiDigistoreManager(ContainerDigistoreManager container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().energyStorage, 8, 8, 16, 52));

		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}
}
