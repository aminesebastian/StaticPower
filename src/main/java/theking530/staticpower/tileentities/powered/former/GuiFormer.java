package theking530.staticpower.tileentities.powered.former;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiFormer extends StaticPowerTileEntityGui<ContainerFormer, TileEntityFormer> {
	private GuiInfoTab infoTab;

	public GuiFormer(ContainerFormer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 54));
		registerWidget(new ArrowProgressBar(82, 34).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
		infoTab.addLine("desc1", new TextComponent("The former transforms items into other items=by shaping them against molds."));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
	}
}
