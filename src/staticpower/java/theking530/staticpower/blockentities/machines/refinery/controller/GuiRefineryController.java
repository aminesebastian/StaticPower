package theking530.staticpower.blockentities.machines.refinery.controller;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.gui.widgets.MultiblockStatusWidget;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineHeatTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;

public class GuiRefineryController
		extends StaticCoreBlockEntityScreen<ContainerRefineryController, BlockEntityRefineryController> {
	private FluidProgressBar fluidBar1;
	private FluidProgressBar fluidBar2;
	private FluidProgressBar fluidBar3;
	private GuiInfoTab infoTab;
	private MultiblockStatusWidget multiblockStatusWidget;

	public GuiRefineryController(ContainerRefineryController container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
		registerWidget(multiblockStatusWidget = new MultiblockStatusWidget(this.getXSize() - 18, 5, 12, 12)
				.setShouldRenderWhenWellFormed(true));
		updateNotificationWidget();
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 22, 16, 54));
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage, 27, 22, 4, 54));

		registerWidget(new GuiFluidBarFromTank(getTileEntity().getInputTank(0), 38, 22, 16, 54, MachineSideMode.Input2,
				getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getInputTank(1), 58, 22, 16, 54, MachineSideMode.Input3,
				getTileEntity()));

		registerWidget(new GuiFluidBarFromTank(getTileEntity().getOutputTank(0), 108, 22, 16, 54,
				MachineSideMode.Output, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getOutputTank(1), 128, 22, 16, 54,
				MachineSideMode.Output2, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getOutputTank(2), 148, 22, 16, 54,
				MachineSideMode.Output3, getTileEntity()));

		registerWidget(
				new ArrowProgressBar(80, 42).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(fluidBar1 = new FluidProgressBar(79, 61, 24, 4)
				.bindToMachineProcessingComponent(getTileEntity().processingComponent).setDisplayErrorIcon(false));
		registerWidget(fluidBar2 = new FluidProgressBar(79, 67, 24, 4)
				.bindToMachineProcessingComponent(getTileEntity().processingComponent).setDisplayErrorIcon(false));
		registerWidget(fluidBar3 = new FluidProgressBar(79, 73, 24, 4)
				.bindToMachineProcessingComponent(getTileEntity().processingComponent).setDisplayErrorIcon(false));

		getTabManager().registerTab(infoTab = new GuiInfoTab("Statistics", 110), true);
		getTabManager().registerTab(new GuiMachineHeatTab(getTileEntity().heatStorage));
		getTabManager().registerTab(
				new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT),
				true);
		getTabManager()
				.registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));
	}

	@Override
	public void updateData() {
		// Get the recipe.
		Optional<RefineryRecipe> recipe = getTileEntity().processingComponent.getProcessingRecipe();

		// Update the progress bar.
		if (recipe.isPresent()) {
			if (!recipe.get().getFluidOutput1().isEmpty()) {
				fluidBar1.setFluidStack(recipe.get().getFluidOutput1());
			} else {
				fluidBar1.setFluidStack(FluidStack.EMPTY);
			}
			if (!recipe.get().getFluidOutput2().isEmpty()) {
				fluidBar2.setFluidStack(recipe.get().getFluidOutput2());
			} else {
				fluidBar1.setFluidStack(FluidStack.EMPTY);
			}
			if (!recipe.get().getFluidOutput3().isEmpty()) {
				fluidBar3.setFluidStack(recipe.get().getFluidOutput3());
			} else {
				fluidBar1.setFluidStack(FluidStack.EMPTY);
			}
		}

		// Update the production data.
		infoTab.addKeyValueTwoLiner("productivity", Component.literal("Productivity"), GuiTextUtilities
				.formatNumberAsString(getTileEntity().getProductivity() * 100).append("% (")
				.append(ChatFormatting.DARK_AQUA
						+ GuiTextUtilities.formatNumberAsString(getTileEntity().getBoilers().size()).getString())
				.append(" Boilers" + ChatFormatting.RESET + ")"), ChatFormatting.GREEN);
		infoTab.addKeyValueTwoLiner("heat_gen", Component.literal("Heat Generation"),
				GuiTextUtilities.formatHeatRateToString(getTileEntity().getHeatGeneration()), ChatFormatting.RED);

		updateNotificationWidget();
	}

	private void updateNotificationWidget() {
		multiblockStatusWidget.setStatus(getTileEntity().getMultiBlockStatus());
	}
}
