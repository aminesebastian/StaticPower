package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiChargingStation extends StaticPowerTileEntityGui<ContainerChargingStation, TileEntityChargingStation> {

	public GuiChargingStation(ContainerChargingStation container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 42));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiUpgradeTab(this.container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBehindItems(float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(partialTicks, mouseX, mouseY);
		this.itemRenderer.drawItem(Items.IRON_HELMET, guiLeft, guiTop, 152, 8, 0.3f);
		this.itemRenderer.drawItem(Items.IRON_CHESTPLATE, guiLeft, guiTop, 152, 26, 0.3f);
		this.itemRenderer.drawItem(Items.IRON_LEGGINGS, guiLeft, guiTop, 152, 44, 0.3f);
		this.itemRenderer.drawItem(Items.IRON_BOOTS, guiLeft, guiTop, 152, 62, 0.3f);
	}
}
