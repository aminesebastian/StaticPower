package theking530.staticpower.tileentities.powered.treefarmer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiTreeFarmer extends StaticPowerTileEntityGui<ContainerTreeFarmer, TileEntityTreeFarm> {

	private GuiInfoTab infoTab;
	private SpriteButton drawPreviewButton;

	public GuiTreeFarmer(ContainerTreeFarmer container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 46));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60, MachineSideMode.Input, getTileEntity()));
		registerWidget(new ProcessingComponentStateWidget(getTileEntity().processingComponent, 82, 74, 9, 9));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage, getTileEntity().processingComponent).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiFluidContainerTab(this.container, getTileEntity().fluidContainerComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(this.container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiAxeTab(this.container, getTileEntity()).setTabSide(TabSide.LEFT));

		registerWidget(drawPreviewButton = new SpriteButton(153, 71, 10, 10, StaticPowerSprites.RANGE_ICON, null, this::buttonPressed));
		drawPreviewButton.setTooltip(new StringTextComponent("Preview Range"));
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
		infoTab.addLine(new StringTextComponent("Farms trees in a " + TextFormatting.YELLOW + ((getTileEntity().getRadius() * 2) + 1) + "x" + ((getTileEntity().getRadius() * 2) + 1) + " radius."));
		infoTab.addLine(new StringTextComponent("Requires " + TextFormatting.DARK_AQUA + "water" + TextFormatting.RESET + " to operate but other fluids may yield better growth results..."));
		infoTab.addKeyValueTwoLiner(new StringTextComponent("Current Growth Factor"), GuiTextUtilities.formatNumberAsString(getTileEntity().getGrowthBonus() * 100).appendText("%"),
				TextFormatting.GOLD);
	}
}
