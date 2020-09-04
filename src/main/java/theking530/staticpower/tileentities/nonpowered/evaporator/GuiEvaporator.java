package theking530.staticpower.tileentities.nonpowered.evaporator;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiPassiveHeatTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiEvaporator extends StaticPowerTileEntityGui<ContainerEvaporator, TileEntityEvaporator> {
	private FluidProgressBar progressBar;

	public GuiEvaporator(ContainerEvaporator container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage.getStorage(), 8, 16, 16, 62));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().inputTankComponent, 50, 18, 20, 58, MachineSideMode.Input, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().outputTankComponent, 106, 18, 20, 58, MachineSideMode.Output, getTileEntity()));
		registerWidget(progressBar = (FluidProgressBar) new FluidProgressBar(75, 44, 26, 5).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(new GuiPassiveHeatTab(getTileEntity().heatStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().inputTankComponent).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		// If the recipe is non-null, render the fluid progress bar.
		if (getTileEntity().processingComponent.isProcessing()) {
			FluidStack fluid = getTileEntity().inputTankComponent.getFluid();
			progressBar.setFluidStack(fluid);
		} else {
			progressBar.setFluidStack(FluidStack.EMPTY);
		}
	}
}
