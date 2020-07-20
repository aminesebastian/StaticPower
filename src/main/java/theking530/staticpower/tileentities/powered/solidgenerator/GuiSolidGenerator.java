package theking530.staticpower.tileentities.powered.solidgenerator;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;

public class GuiSolidGenerator extends StaticPowerTileEntityGui<ContainerSolidGenerator, TileEntitySolidGenerator> {

	public GuiSolidGenerator(ContainerSolidGenerator container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 70));
		registerWidget(new ArrowProgressBar(59, 32).setFlipped(true).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		BaseGuiTab powerTab;
		getTabManager().registerTab(powerTab = new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()));
		powerTab.setTabSide(TabSide.LEFT);
		getTabManager().setInitiallyOpenTab(powerTab);

		this.setInputSlotSize(24);
	}
}
