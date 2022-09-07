package theking530.staticpower.tileentities.powered.chargingstation;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiChargingStation extends StaticPowerTileEntityGui<ContainerChargingStation, TileEntityChargingStation> {

	public GuiChargingStation(ContainerChargingStation container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 176);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 50));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);
		GuiDrawUtilities.drawItem(stack, new ItemStack(Items.IRON_HELMET), 152, 8, 0, 0.3f);
		GuiDrawUtilities.drawItem(stack, new ItemStack(Items.IRON_CHESTPLATE), 152, 26, 0, 0.3f);
		GuiDrawUtilities.drawItem(stack, new ItemStack(Items.IRON_LEGGINGS), 152, 44, 0, 0.3f);
		GuiDrawUtilities.drawItem(stack, new ItemStack(Items.IRON_BOOTS), 152, 62, 0, 0.3f);
	}
}
