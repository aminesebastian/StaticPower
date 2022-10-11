package theking530.staticpower.blockentities.machines.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.progressbars.CentrifugeProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.components.control.RedstoneControlComponent;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiCentrifuge extends StaticPowerTileEntityGui<ContainerCentrifuge, BlockEntityCentrifuge> {
	private GuiInfoTab infoTab;

	public GuiCentrifuge(ContainerCentrifuge container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new CentrifugeProgressBar(79, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);

		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		infoTab.clear();
		infoTab.addLine("desc1", Component.literal("Separates items into their base components."));
		infoTab.addKeyValueTwoLiner("c_speed", Component.literal("Current Speed"),
				GuiTextUtilities.formatNumberAsString(getTileEntity().getCurrentSpeed()).append(Component.translatable("gui.staticpower.rpm")), ChatFormatting.YELLOW);
		infoTab.addKeyValueTwoLiner("max_speed", Component.literal("Max Speed"),
				GuiTextUtilities.formatNumberAsString(getTileEntity().getMaxSpeed()).append(Component.translatable("gui.staticpower.rpm")), ChatFormatting.RED);
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);

		String rpmText = getTileEntity().getCurrentSpeed() + " RPM";
		drawEmptySlot(stack, 123 - (Minecraft.getInstance().font.width(rpmText) / 2), 40, Minecraft.getInstance().font.width(rpmText) + 4, 11);
		Minecraft.getInstance().font.drawShadow(stack, rpmText, 125 - (Minecraft.getInstance().font.width(rpmText) / 2), 42, SDColor.EIGHT_BIT_WHITE.encodeInInteger());
	}
}
