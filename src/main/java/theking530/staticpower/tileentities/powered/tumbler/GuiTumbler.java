package theking530.staticpower.tileentities.powered.tumbler;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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

	public GuiTumbler(ContainerTumbler container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GrinderProgressBar(79, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
		infoTab.addLine("desc1", new StringTextComponent("Grinds items into their base components."));
		infoTab.addLineBreak();
		infoTab.addKeyValueTwoLiner("bonus", new StringTextComponent("Add. Bonus Chance"), GuiTextUtilities.formatNumberAsString(getTileEntity().getBonusChance() - 1.0f).appendString("%"),
				TextFormatting.GREEN);

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		infoTab.addKeyValueTwoLiner("bonus", new StringTextComponent("Add. Bonus Chance"), GuiTextUtilities.formatNumberAsString(getTileEntity().getBonusChance() - 1.0f).appendString("%"),
				TextFormatting.GREEN);
	}

	@Override
	protected void drawBackgroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);

		MetricConverter metric = new MetricConverter(getTileEntity().getCurrentSpeed());
		String rpmText = metric.getValueAsString(true) + " RPM";
		drawEmptySlot(stack, 123 - (Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) / 2), 40, Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) + 4, 11);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(stack, rpmText, 125 - (Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) / 2), 42,
				Color.EIGHT_BIT_WHITE.encodeInInteger());
	}
}
