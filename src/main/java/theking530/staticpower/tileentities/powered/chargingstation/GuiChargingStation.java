package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.tabs.GuiRedstoneTab;
import theking530.api.gui.widgets.tabs.GuiSideConfigTab;
import theking530.api.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiChargingStation extends StaticPowerTileEntityGui<ContainerChargingStation, TileEntityChargingStation> {

	public GuiChargingStation(ContainerChargingStation container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity(), 8, 8, 16, 42));

		getTabManager().registerTab(new GuiRedstoneTab(100, 85, getTileEntity()));
		getTabManager().registerTab(new GuiSideConfigTab(80, 80, false, getTileEntity()));
		// getTabManager().registerTab(new GuiMachinePowerInfoTab(80, 80,
		// chargingStation).setTabSide(TabSide.LEFT).setOffsets(-31, 0));

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawPlayerInventorySlots();
		drawContainerSlots(getTileEntity(), container.inventorySlots);

		drawGenericBackground(-30, 8, 28, 85);
		drawSlot(guiLeft - 24, guiTop + 14, 16, 16);
		drawSlot(guiLeft - 24, guiTop + 33, 16, 16);
		drawSlot(guiLeft - 24, guiTop + 52, 16, 16);
		drawSlot(guiLeft - 24, guiTop + 71, 16, 16);
	}
}
