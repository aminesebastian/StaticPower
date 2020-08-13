package theking530.staticpower.tileentities.nonpowered.distillery;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiPassiveHeatTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.common.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.common.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiDistillery extends StaticPowerTileEntityGui<ContainerDistillery, TileEntityDistillery> {

	public GuiDistillery(ContainerDistillery container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage.getStorage(), 8, 16, 16, 62));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().inputTankComponent, 50, 18, 20, 58, MachineSideMode.Input, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().outputTankComponent, 106, 18, 20, 58, MachineSideMode.Output, getTileEntity()));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		getTabManager().registerTab(new GuiPassiveHeatTab(getTileEntity().heatStorage).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		// Draw the progress bar bg.
		drawSlot(guiLeft + 75, guiTop + 36, 26, 5);

		// If the recipe is non-null, render the fluid progress bar.
		if (getTileEntity().processingComponent.isProcessing()) {
			int progress = getTileEntity().processingComponent.getProgressScaled(26);
			FluidStack fluid = getTileEntity().inputTankComponent.getFluid();
			GuiFluidBarUtilities.drawFluidBar(fluid, 1000, 1000, guiLeft + 75, guiTop + 41, 1, progress, 5, false);
		}
	}
}
