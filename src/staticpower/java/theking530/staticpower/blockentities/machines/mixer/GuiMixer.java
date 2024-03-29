package theking530.staticpower.blockentities.machines.mixer;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.client.gui.widgets.MixerProgressBar;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;

public class GuiMixer extends StaticCoreBlockEntityScreen<ContainerMixer, BlockEntityMixer> {
	private FluidProgressBar progressBar;

	public GuiMixer(ContainerMixer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 179);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidInput1, 32, 22, 20, 54, MachineSideMode.Input2, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidInput2, 84, 22, 20, 54, MachineSideMode.Input3, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidOutput, 148, 22, 20, 54, MachineSideMode.Output, getTileEntity()));
		registerWidget(new MixerProgressBar(58, 41).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(progressBar = (FluidProgressBar) new FluidProgressBar(110, 43, 32, 8).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidOutput).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));
	}

	@Override
	public void updateData() {
		// If the recipe is non-null, render the fluid progress bar.
		Optional<MixerRecipe> currentRecipe = getTileEntity().processingComponent.getProcessingRecipe();
		if (currentRecipe.isPresent()) {
			progressBar.setFluidStack(currentRecipe.get().getOutput());
		} else {
			progressBar.setFluidStack(FluidStack.EMPTY);
		}
	}
}
