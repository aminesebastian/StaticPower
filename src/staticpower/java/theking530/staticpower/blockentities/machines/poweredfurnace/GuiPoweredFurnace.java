package theking530.staticpower.blockentities.machines.poweredfurnace;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.PowerSatisfactionWidget;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;

public class GuiPoweredFurnace extends StaticCoreBlockEntityScreen<ContainerPoweredFurnace, BlockEntityPoweredFurnace> {

	public GuiPoweredFurnace(ContainerPoweredFurnace container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 18, 16, 43));
		registerWidget(
				new ArrowProgressBar(75, 30).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(
				new FireProgressBar(50, 48).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(new PowerSatisfactionWidget(5, 0, 22, getTileEntity().processingComponent));

		getTabManager().registerTab(
				new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT),
				true);

		setOutputSlotSize(20);
	}
}
