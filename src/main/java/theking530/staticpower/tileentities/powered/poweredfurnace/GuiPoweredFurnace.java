package theking530.staticpower.tileentities.powered.poweredfurnace;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
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

public class GuiPoweredFurnace extends StaticPowerTileEntityGui<ContainerPoweredFurnace, TileEntityPoweredFurnace> {

	public GuiPoweredFurnace(ContainerPoweredFurnace container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new ArrowProgressBar(75, 30).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(new FireProgressBar(51, 48).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT),
				true);

		setOutputSlotSize(20);
	}
}
