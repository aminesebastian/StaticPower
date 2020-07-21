package theking530.staticpower.tileentities.powered.squeezer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.common.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiSqueezer extends StaticPowerTileEntityGui<ContainerSqueezer, TileEntitySqueezer> {

	public GuiSqueezer(ContainerSqueezer container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 108, 18, 16, 58, MachineSideMode.Output, getTileEntity()));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		BaseGuiTab powerTab;
		getTabManager().registerTab(powerTab = new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT));
		getTabManager().setInitiallyOpenTab(powerTab);

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		// Draw the progress bar bg.
		drawSlot(guiLeft + 74, guiTop + 34, 28, 5);

		// Get the recipe.
		SqueezerRecipe recipe = getTileEntity().getRecipe(getTileEntity().internalInventory.getStackInSlot(0)).orElse(null);

		// If the recipe is non-null, render the fluid progress bar.
		if (recipe != null && recipe.hasOutputFluid()) {
			int progress = getTileEntity().processingComponent.getProgressScaled(28);
			FluidStack fluid = recipe.getOutputFluid();
			GuiFluidBarUtilities.drawFluidBar(fluid, 1000, 1000, guiLeft + 74, guiTop + 39, 1, progress, 5, false);
		}
	}
}
