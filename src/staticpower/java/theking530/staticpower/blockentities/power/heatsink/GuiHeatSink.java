package theking530.staticpower.blockentities.power.heatsink;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineHeatTab;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;

public class GuiHeatSink extends StaticCoreBlockEntityScreen<ContainerHeatSink, BlockEntityHeatSink> {
	protected GuiInfoTab infoTab;

	public GuiHeatSink(ContainerHeatSink container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 156);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage, 58, 18, 64, 40));

		getTabManager().registerTab(new GuiMachineHeatTab(getTileEntity().heatStorage), true);
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
	}

	@Override
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(null, partialTicks, mouseX, mouseY);

	}
}
