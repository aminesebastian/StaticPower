package theking530.staticpower.tileentities.powered.fluidgenerator;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.progressbars.FireProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiFluidGenerator extends StaticPowerTileEntityGui<ContainerFluidGenerator, TileEntityFluidGenerator> {
	private FireProgressBar fireBar;

	public GuiFluidGenerator(ContainerFluidGenerator container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60, MachineSideMode.Input, getTileEntity()));
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 70));
		registerWidget(fireBar = new FireProgressBar(81, 58));
		fireBar.setMaxProgress(1);
		
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT),
				true);
		getTabManager().registerTab(new GuiFluidContainerTab(this.container, getTileEntity().fluidContainerComponent, Items.BUCKET, ModFluids.Mash.getBucket()).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(this.container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));
	}

	@Override
	public void updateData() {
		if (getTileEntity().processingComponent.getIsOnBlockState()) {
			fireBar.setCurrentProgress(0);
		} else {
			fireBar.setCurrentProgress(1);
		}
	}
}
