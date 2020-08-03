package theking530.staticpower.tileentities.powered.basicfarmer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.widgets.button.SpriteButton;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.button.StandardButton.MouseButton;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiBasicFarmer extends StaticPowerTileEntityGui<ContainerBasicFarmer, TileEntityBasicFarmer> {

	private GuiInfoTab infoTab;
	private SpriteButton drawPreviewButton;

	public GuiBasicFarmer(ContainerBasicFarmer container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 48));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60, MachineSideMode.Input, getTileEntity()));

		registerWidget(drawPreviewButton = new SpriteButton(132, 61, 12, 12, StaticPowerSprites.RANGE_ICON, null, this::buttonPressed));
		drawPreviewButton.setTooltip(new StringTextComponent("Preview Range"));
		drawPreviewButton.setToggleable(true);
		drawPreviewButton.setToggled(getTileEntity().getShouldDrawRadiusPreview());

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 100));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));


		getTabManager().registerTab(new GuiMachinePowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiFluidContainerTab(this.container, getTileEntity().fluidContainerInventory).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(this.container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));
		
		setOutputSlotSize(16);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == drawPreviewButton) {
			getTileEntity().setShouldDrawRadiusPreview(drawPreviewButton.isToggled());
		}
	}

	@Override
	public void updateData() {
		super.updateData();
		infoTab.setText("Farmer", "Farms plants in a " + TextFormatting.YELLOW + getTileEntity().getRadius() + " block=radius.==Requires " + TextFormatting.DARK_AQUA + "water" + TextFormatting.RESET
				+ " to operate=but other fluids may yield=better growth results...==Current Growth Factor: " + TextFormatting.GOLD + getTileEntity().getGrowthBonus() * 100.0f + "%");
	}
}
