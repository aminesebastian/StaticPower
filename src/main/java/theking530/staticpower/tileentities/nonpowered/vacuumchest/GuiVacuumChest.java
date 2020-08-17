package theking530.staticpower.tileentities.nonpowered.vacuumchest;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiVacuumChest extends StaticPowerTileEntityGui<ContainerVacuumChest, TileEntityVacuumChest> {

	private GuiInfoTab infoTab;
	private GuiFluidBarFromTank fluidBar;
	private GuiMachineFluidTab fluidTab;
	private GuiFluidContainerTab fluidContainerTab;

	public GuiVacuumChest(ContainerVacuumChest container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 65));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		registerWidget(fluidBar = new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 176, 20, 16, 74, MachineSideMode.Output, getTileEntity()));

		fluidTab = (GuiMachineFluidTab) new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT);
		fluidContainerTab = (GuiFluidContainerTab) new GuiFluidContainerTab(this.container, getTileEntity().fluidContainerComponent, Items.BUCKET, ModFluids.Mash.getBucket()).setTabSide(TabSide.LEFT);
		setOutputSlotSize(16);

		if (getTileEntity().showTank()) {
			xSize = 200;
			xSizeTarget = 200;
		} else {
			fluidBar.setVisible(false);
		}
	}

	@Override
	public void updateData() {
		DecimalFormat format = new DecimalFormat("##.###");
		String text = ("Vacuums items in a  =nearby radius. ==" + TextFormatting.RED + "Radius: " + TextFormatting.AQUA + format.format(getTileEntity().getRadius()) + " Blocks");
		String[] splitMsg = text.split("=");
		infoTab.setText(getTitle().getFormattedText(), Arrays.asList(splitMsg));

		if (!getTileEntity().showTank()) {
			setGuiSizeTarget(176, 185);
			fluidBar.setVisible(false);
			getTabManager().removeTab(fluidTab);
			getTabManager().removeTab(fluidContainerTab);
		} else {
			setGuiSizeTarget(200, 185);
			fluidBar.setVisible(true);
			getTabManager().registerTab(fluidTab);
			getTabManager().registerTab(fluidContainerTab);
		}
	}
}
