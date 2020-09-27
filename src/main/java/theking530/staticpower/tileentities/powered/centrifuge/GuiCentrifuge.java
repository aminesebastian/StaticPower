package theking530.staticpower.tileentities.powered.centrifuge;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.widgets.progressbars.CentrifugeProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiCentrifuge extends StaticPowerTileEntityGui<ContainerCentrifuge, TileEntityCentrifuge> {
	private GuiInfoTab infoTab;

	public GuiCentrifuge(ContainerCentrifuge container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new CentrifugeProgressBar(79, 38)
				.bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100));
		getTabManager().registerTab(
				new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		getTabManager().registerTab(
				new GuiMachinePowerInfoTab(getTileEntity().energyStorage, getTileEntity().processingComponent)
						.setTabSide(TabSide.LEFT),
				true);

		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		infoTab.clear();
		infoTab.addLine(new StringTextComponent("Separates items into their base components."));
		infoTab.addKeyValueTwoLiner(new StringTextComponent("Current Speed"),
				GuiTextUtilities.formatNumberAsString(getTileEntity().getCurrentSpeed())
						.append(new TranslationTextComponent("gui.staticpower.rpm")),
				TextFormatting.YELLOW);
		infoTab.addKeyValueTwoLiner(new StringTextComponent("Max Speed"),
				GuiTextUtilities.formatNumberAsString(getTileEntity().getMaxSpeed())
						.append(new TranslationTextComponent("gui.staticpower.rpm")),
				TextFormatting.RED);
	}

	@Override
	protected void drawBackgroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);

		String rpmText = getTileEntity().getCurrentSpeed() + " RPM";
		drawEmptySlot(guiLeft + 123 - (Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) / 2), guiTop + 40,
				Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) + 4, 11);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(stack, rpmText,
				guiLeft + 125 - (Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) / 2), guiTop + 42,
				Color.EIGHT_BIT_WHITE.encodeInInteger());
	}
}
