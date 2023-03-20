package theking530.staticpower.blockentities.machines.treefarmer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.ProcessingComponentStateWidget;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;

public class GuiTreeFarmer extends StaticCoreBlockEntityScreen<ContainerTreeFarmer, BlockEntityTreeFarm> {

	private GuiInfoTab infoTab;
	private SpriteButton drawPreviewButton;

	public GuiTreeFarmer(ContainerTreeFarmer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 174);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 46));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60, MachineSideMode.Input, getTileEntity()));
		registerWidget(new ProcessingComponentStateWidget(getTileEntity().processingComponent, 82, 74, 9, 9));

		getTabManager().registerTab(infoTab = new GuiInfoTab(125));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiAxeTab(this.menu, getTileEntity()).setTabSide(TabSide.LEFT));

		registerWidget(drawPreviewButton = new SpriteButton(153, 71, 10, 10, StaticCoreSprites.RANGE_ICON, null, this::buttonPressed));
		drawPreviewButton.setTooltip(Component.literal("Preview Range"));
		drawPreviewButton.setToggleable(true);
		drawPreviewButton.setToggled(getTileEntity().getShouldDrawRadiusPreview());

		setOutputSlotSize(16);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == drawPreviewButton) {
			getTileEntity().setShouldDrawRadiusPreview(drawPreviewButton.isToggled());
		}
	}

	@Override
	public void updateData() {
		infoTab.clear();
		infoTab.addLine("desc1",
				Component.literal("Farms trees in a " + ChatFormatting.YELLOW + ((getTileEntity().getRadius() * 2) + 1) + "x" + ((getTileEntity().getRadius() * 2) + 1) + " radius."));
		infoTab.addLine("desc2",
				Component.literal("Requires " + ChatFormatting.DARK_AQUA + "water" + ChatFormatting.RESET + " to operate but other fluids may yield better growth results..."));
		infoTab.addKeyValueTwoLiner("growth", Component.literal("Current Growth Factor"),
				GuiTextUtilities.formatNumberAsString(getTileEntity().getGrowthBonus() * 100).append("%"), ChatFormatting.GOLD);
	}
}
