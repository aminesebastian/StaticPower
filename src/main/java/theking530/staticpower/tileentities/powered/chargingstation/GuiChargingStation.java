package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
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

public class GuiChargingStation extends StaticPowerTileEntityGui<ContainerChargingStation, TileEntityChargingStation> {

	public GuiChargingStation(ContainerChargingStation container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 42));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		BaseGuiTab powerTab;
		getTabManager().registerTab(powerTab = new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT));
		getTabManager().setInitiallyOpenTab(powerTab);

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawPlayerInventorySlots();
		drawContainerSlots(container.inventorySlots, getTileEntity().ioSideConfiguration);

		drawGenericBackground(-30, 8, 28, 85);
		drawSlot(guiLeft - 24, guiTop + 14, 16, 16);
		drawSlot(guiLeft - 24, guiTop + 33, 16, 16);
		drawSlot(guiLeft - 24, guiTop + 52, 16, 16);
		drawSlot(guiLeft - 24, guiTop + 71, 16, 16);
	}
}
