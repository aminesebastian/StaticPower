package theking530.staticpower.blockentities.machines.caster;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.blockentities.components.control.RedstoneControlComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;

public class GuiCaster extends StaticPowerTileEntityGui<ContainerCaster, BlockEntityCaster> {
	private GuiInfoTab infoTab;
	private FluidProgressBar progressBar;

	public GuiCaster(ContainerCaster container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 180);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 54));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 34, 22, 20, 54, MachineSideMode.Input, getTileEntity()));
		registerWidget(new ArrowProgressBar(59, 34).bindToMachineProcessingComponent(getTileEntity().processingComponent).setAnimationLastUntil(0.5f));
		registerWidget(progressBar = (FluidProgressBar) new FluidProgressBar(108, 40, 36, 5).bindToMachineProcessingComponent(getTileEntity().processingComponent)
				.setAnimationStartAfter(0.5f));

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
		infoTab.addLine("desc1", Component.literal("The former transforms items into other items=by shaping them against molds."));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
	}

	@Override
	public void updateData() {
		// Get the recipe.
		Optional<CastingRecipe> currentRecipe = getTileEntity().processingComponent.getCurrentRecipe();

		// Update the progress bar.
		if (currentRecipe.isPresent()) {
			progressBar.setFluidStack(getTileEntity().processingComponent.getProcessingMaterials().getInputFluid(0).fluid());
		} else {
			progressBar.setFluidStack(FluidStack.EMPTY);
		}
	}
}
