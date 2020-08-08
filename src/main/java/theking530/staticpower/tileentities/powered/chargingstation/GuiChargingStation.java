package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.GuiIslandWidget;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

public class GuiChargingStation extends StaticPowerTileEntityGui<ContainerChargingStation, TileEntityChargingStation> {

	public GuiChargingStation(ContainerChargingStation container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 42));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()), true);
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		registerWidget(new GuiIslandWidget(-25, 8, 30, 85));

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBehindItems(float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(partialTicks, mouseX, mouseY);
		this.itemRenderer.drawItem(Items.IRON_HELMET, guiLeft, guiTop, -19, 14, 0.3f);
		this.itemRenderer.drawItem(Items.IRON_CHESTPLATE, guiLeft, guiTop, -19, 33, 0.3f);
		this.itemRenderer.drawItem(Items.IRON_LEGGINGS, guiLeft, guiTop, -19, 52, 0.3f);
		this.itemRenderer.drawItem(Items.IRON_BOOTS, guiLeft, guiTop, -19, 71, 0.3f);
	}
}
