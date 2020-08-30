package theking530.staticpower.tileentities.powered.crucible;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiCrucible extends StaticPowerTileEntityGui<ContainerCrucible, TileEntityCrucible> {

	public GuiCrucible(ContainerCrucible container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 42));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawPlayerInventorySlots();
		drawContainerSlots(container.inventorySlots, getTileEntity().ioSideConfiguration);

		drawGenericBackground(-30, 8, 28, 85);
		drawEmptySlot(guiLeft - 24, guiTop + 14, 16, 16);
		drawEmptySlot(guiLeft - 24, guiTop + 33, 16, 16);
		drawEmptySlot(guiLeft - 24, guiTop + 52, 16, 16);
		drawEmptySlot(guiLeft - 24, guiTop + 71, 16, 16);
	}
}
