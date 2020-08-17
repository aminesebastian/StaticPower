package theking530.staticpower.tileentities.powered.fermenter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.common.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
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

		getTabManager().registerTab(infoTab = new GuiInfoTab(70, 70));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage, getTileEntity().processingComponent).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiFluidContainerTab(this.container, getTileEntity().fluidContainerComponent, Items.BUCKET, ModFluids.Mash.getBucket()).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(this.container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

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
			FluidStack fluid = getTileEntity().processingComponent.getRecipe(getTileEntity().getMatchParameters(RecipeProcessingLocation.INTERNAL)).get().getOutputFluidStack();
			GuiFluidBarUtilities.drawFluidBar(fluid, 1000, 1000, guiLeft + 97, guiTop + 45, 1, progress, 5, false);
		}
	}
}
