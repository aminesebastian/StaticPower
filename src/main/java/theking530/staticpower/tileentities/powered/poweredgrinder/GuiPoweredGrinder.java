package theking530.staticpower.tileentities.powered.poweredgrinder;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import theking530.staticcore.gui.widgets.progressbars.GrinderProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPoweredGrinder extends StaticPowerTileEntityGui<ContainerPoweredGrinder, TileEntityPoweredGrinder> {
	private GuiInfoTab infoTab;

	public GuiPoweredGrinder(ContainerPoweredGrinder container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GrinderProgressBar(79, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
		infoTab.addLine("desc", new TextComponent("Grinds items into their base components."));
		infoTab.addLineBreak();
		infoTab.addKeyValueTwoLiner("bonus", new TextComponent("Add. Bonus Chance"), GuiTextUtilities.formatNumberAsString(getTileEntity().getBonusChance() - 1.0f).append("%"),
				ChatFormatting.GREEN);

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		infoTab.addKeyValueTwoLiner("bonus", new TextComponent("Add. Bonus Chance"), GuiTextUtilities.formatNumberAsString(getTileEntity().getBonusChance() - 1.0f).append("%"),
				ChatFormatting.GREEN);
	}
}
