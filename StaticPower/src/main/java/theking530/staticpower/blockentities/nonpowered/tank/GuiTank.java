package theking530.staticpower.blockentities.nonpowered.tank;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.init.ModItems;

public class GuiTank extends StaticCoreBlockEntityScreen<ContainerTank, BlockEntityTank> {
	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiTank(ContainerTank container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 72, 24, 28, 64));
		tabManager.registerTab(infoTab = new GuiInfoTab(getTitle(), 100));

		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiUpgradeTab(menu, getTileEntity().voidUpgradeInventory, new ItemDrawable(ModItems.VoidUpgrade.get())).setTabSide(TabSide.LEFT));
	}
}
