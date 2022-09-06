package theking530.staticpower.tileentities.testing.furnace;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromPowerStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiTestFurnace extends StaticPowerTileEntityGui<ContainerTestFurnace, BlockEntityTestFurnace> {

	public GuiTestFurnace(ContainerTestFurnace container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromPowerStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new ArrowProgressBar(75, 30).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(new FireProgressBar(50, 48).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		// getTabManager().registerTab(new
		// GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT),
		// true);

		setOutputSlotSize(20);
	}
}
