package theking530.staticpower.tileentities.powered.squeezer;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiSqueezer extends StaticPowerTileEntityGui<ContainerSqueezer, TileEntitySqueezer> {
	private FluidProgressBar progressBar;

	public GuiSqueezer(ContainerSqueezer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);

	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 108, 18, 16, 58, MachineSideMode.Output, getTileEntity()));
		registerWidget(progressBar = (FluidProgressBar) new FluidProgressBar(74, 32, 28, 5).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent, Items.BUCKET, ModFluids.Mash.getBucket()).setTabSide(TabSide.LEFT));
	}

	@Override
	public void updateData() {
		// Get the recipe.
		Optional<SqueezerRecipe> currentRecipe = getTileEntity().processingComponent.getCurrentProcessingRecipe();

		// Update the progress bar.
		if (currentRecipe.isPresent() && currentRecipe.get().hasOutputFluid()) {
			progressBar.setFluidStack(currentRecipe.get().getOutputFluid());
		} else {
			progressBar.setFluidStack(FluidStack.EMPTY);
		}
	}
}
