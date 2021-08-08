package theking530.staticpower.tileentities.powered.vulcanizer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiVulcanizer extends StaticPowerTileEntityGui<ContainerVulcanizer, TileEntityVulcanizer> {
	private FluidProgressBar progressBar;

	public GuiVulcanizer(ContainerVulcanizer container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 50, 22, 20, 54, MachineSideMode.Input, getTileEntity()));
		registerWidget(progressBar = (FluidProgressBar) new FluidProgressBar(76, 41, 55, 5).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiFluidContainerTab(this.container, getTileEntity().fluidContainerComponent).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		super.updateData();
		// If the recipe is non-null, render the fluid progress bar.
		if (getTileEntity().processingComponent.isProcessing()) {
			FluidStack fluid = getTileEntity().fluidTankComponent.getFluid();
			progressBar.setFluidStack(fluid);
		} else {
			progressBar.setFluidStack(FluidStack.EMPTY);
		}
	}
}
