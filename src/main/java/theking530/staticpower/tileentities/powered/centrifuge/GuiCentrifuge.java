package theking530.staticpower.tileentities.powered.centrifuge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.widgets.progressbars.CentrifugeProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.common.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiCentrifuge extends StaticPowerTileEntityGui<ContainerCentrifuge, TileEntityCentrifuge> {
	private GuiInfoTab infoTab;

	public GuiCentrifuge(ContainerCentrifuge container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new CentrifugeProgressBar(79, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 60));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage, getTileEntity().processingComponent).setTabSide(TabSide.LEFT), true);

		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		String text = ("Separates items into=their base components. ==" + "Current Speed: " + TextFormatting.GREEN + getTileEntity().getCurrentSpeed() + "RPM");
		infoTab.setText(getTileEntity().getDisplayName().getFormattedText(), text);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);

		String rpmText = getTileEntity().getCurrentSpeed() + " RPM";
		drawSlot(guiLeft + 123 - (Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) / 2), guiTop + 40, Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) + 4, 11);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(rpmText, guiLeft + 125 - (Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) / 2), guiTop + 42,
				Color.EIGHT_BIT_WHITE.encodeInInteger());
	}
}
