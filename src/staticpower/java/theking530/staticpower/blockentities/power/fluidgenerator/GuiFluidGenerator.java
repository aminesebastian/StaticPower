package theking530.staticpower.blockentities.power.fluidgenerator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.init.ModFluids;

public class GuiFluidGenerator extends StaticCoreBlockEntityScreen<ContainerFluidGenerator, BlockEntityFluidGenerator> {
	private FireProgressBar fireBar;

	public GuiFluidGenerator(ContainerFluidGenerator container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60,
				MachineSideMode.Input, getTileEntity()));
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 60));
		registerWidget(fireBar = new FireProgressBar(81, 52));
		fireBar.setMaxProgress(1);

		getTabManager().registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory));
		getTabManager().registerTab(
				new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT),
				true);
		getTabManager()
				.registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent,
				Items.BUCKET, ModFluids.Mash.getBucket()).setTabSide(TabSide.LEFT));
	}

	@Override
	public void updateData() {
		// We set the max progress high because the fire progress bar is inverted, so we
		// want to provide a LOW percentage.
		fireBar.setMaxProgress(getTileEntity().fluidTankComponent.getFluidAmount());
		if (getTileEntity().processingComponent.performedWorkLastTick()) {
			fireBar.setCurrentProgress(0);
		} else {
			fireBar.setCurrentProgress(1);
		}
	}
}
