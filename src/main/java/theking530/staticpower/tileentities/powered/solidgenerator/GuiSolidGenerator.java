package theking530.staticpower.tileentities.powered.solidgenerator;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.progressbars.FireProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

public class GuiSolidGenerator extends StaticPowerTileEntityGui<ContainerSolidGenerator, TileEntitySolidGenerator> {

	public GuiSolidGenerator(ContainerSolidGenerator container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 70));
		registerWidget(new FireProgressBar(81, 58).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT),
				true);

		this.setInputSlotSize(24);
	}
}
