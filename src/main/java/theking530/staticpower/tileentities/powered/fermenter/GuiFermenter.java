package theking530.staticpower.tileentities.powered.fermenter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.gui.widgets.GuiIslandWidget;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.common.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiFermenter extends StaticPowerTileEntityGui<ContainerFermenter, TileEntityFermenter> {

	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiFermenter(ContainerFermenter container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 48));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60, MachineSideMode.Output, getTileEntity()));

		registerWidget(new GuiIslandWidget(-30, 5, 28, 60));
		registerWidget(new GuiIslandWidget(-30, 70, 28, 64));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 100));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		getTabManager().registerTab(new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()), true);

		this.setOutputSlotSize(20);
	}

	@Override
	public void updateData() {

	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		this.drawSlot(guiLeft + 97, guiTop + 40, 48, 5);
		if (!getTileEntity().internalInventory.getStackInSlot(0).isEmpty()) {
			int progress = getTileEntity().processingComponent.getProgressScaled(48);
			FluidStack fluid = getTileEntity().getRecipe(getTileEntity().internalInventory.getStackInSlot(0)).get().getOutputFluidStack();
			GuiFluidBarUtilities.drawFluidBar(fluid, 1000, 1000, guiLeft + 97, guiTop + 45, 1, progress, 5, false);
		}
	}
}
