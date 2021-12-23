package theking530.staticpower.tileentities.powered.tumbler;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
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
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.utilities.MetricConverter;

public class GuiTumbler extends StaticPowerTileEntityGui<ContainerTumbler, TileEntityTumbler> {
	private GuiInfoTab infoTab;

	public GuiTumbler(ContainerTumbler container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GrinderProgressBar(79, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
		infoTab.addLine("desc1", new TextComponent("Grinds items into their base components."));
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

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);

		MetricConverter metric = new MetricConverter(getTileEntity().getCurrentSpeed());
		String rpmText = metric.getValueAsString(true) + " RPM";
		drawEmptySlot(stack, 123 - (Minecraft.getInstance().font.width(rpmText) / 2), 40, Minecraft.getInstance().font.width(rpmText) + 4, 11);
		Minecraft.getInstance().font.drawShadow(stack, rpmText, 125 - (Minecraft.getInstance().font.width(rpmText) / 2), 42,
				Color.EIGHT_BIT_WHITE.encodeInInteger());
	}
}
