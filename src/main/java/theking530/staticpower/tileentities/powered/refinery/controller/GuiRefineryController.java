package theking530.staticpower.tileentities.powered.refinery.controller;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiRefineryController extends StaticPowerTileEntityGui<ContainerRefineryController, TileEntityRefineryController> {
	private FluidProgressBar fluidBar1;
	private FluidProgressBar fluidBar2;
	private FluidProgressBar fluidBar3;

	public GuiRefineryController(ContainerRefineryController container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 21, 16, 56));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getInputTank(0), 32, 22, 16, 54, MachineSideMode.Input2, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getInputTank(1), 54, 22, 16, 54, MachineSideMode.Input3, getTileEntity()));

		registerWidget(new GuiFluidBarFromTank(getTileEntity().getOutputTank(0), 104, 22, 16, 54, MachineSideMode.Output, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getOutputTank(1), 126, 22, 16, 54, MachineSideMode.Output2, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getOutputTank(2), 148, 22, 16, 54, MachineSideMode.Output3, getTileEntity()));

		registerWidget(new ArrowProgressBar(76, 42).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(fluidBar1 = new FluidProgressBar(75, 61, 24, 4).bindToMachineProcessingComponent(getTileEntity().processingComponent).setDisplayErrorIcon(false));
		registerWidget(fluidBar2 = new FluidProgressBar(75, 67, 24, 4).bindToMachineProcessingComponent(getTileEntity().processingComponent).setDisplayErrorIcon(false));
		registerWidget(fluidBar3 = new FluidProgressBar(75, 73, 24, 4).bindToMachineProcessingComponent(getTileEntity().processingComponent).setDisplayErrorIcon(false));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));
	}

	@Override
	public void updateData() {
		// Get the recipe.
		Optional<RefineryRecipe> recipe = getTileEntity().processingComponent.getCurrentProcessingRecipe();

		// Update the progress bar.
		if (recipe.isPresent()) {
			if (!recipe.get().getFluidOutput1().isEmpty()) {
				fluidBar1.setFluidStack(recipe.get().getFluidOutput1());
				fluidBar1.setVisible(true);
			} else {
				fluidBar1.setVisible(false);
			}
			if (!recipe.get().getFluidOutput2().isEmpty()) {
				fluidBar2.setFluidStack(recipe.get().getFluidOutput2());
				fluidBar2.setVisible(true);
			} else {
				fluidBar2.setVisible(false);
			}
			if (!recipe.get().getFluidOutput3().isEmpty()) {
				fluidBar3.setFluidStack(recipe.get().getFluidOutput3());
				fluidBar3.setVisible(true);
			} else {
				fluidBar3.setVisible(false);
			}
		} else {
			fluidBar1.setVisible(false);
			fluidBar2.setVisible(false);
			fluidBar3.setVisible(false);
		}
	}
}
