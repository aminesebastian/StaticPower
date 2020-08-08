package theking530.staticpower.tileentities.powered.pump;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

public class GuiPump extends StaticPowerTileEntityGui<ContainerPump, TileEntityPump> {
	private GuiInfoTab infoTab;

	public GuiPump(ContainerPump container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 160);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 10, 16, 44));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 71, 20, 32, 52));
		registerWidget(new ArrowProgressBar(106, 20));
		tabManager.registerTab(infoTab = new GuiInfoTab(100, 65));

		getTabManager().registerTab(new GuiSideConfigTab(true, getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT),
				true);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);
		infoTab.setText(getTitle().getFormattedText(), "test");
	}
}
