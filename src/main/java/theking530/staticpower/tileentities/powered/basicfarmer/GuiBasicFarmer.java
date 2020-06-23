package theking530.staticpower.tileentities.powered.basicfarmer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.gui.widgets.button.BaseButton;
import theking530.api.gui.widgets.button.TextButton;
import theking530.api.gui.widgets.tabs.BaseGuiTab;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.api.gui.widgets.tabs.GuiSideConfigTab;
import theking530.api.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.api.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.api.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiBasicFarmer extends StaticPowerTileEntityGui<ContainerBasicFarmer, TileEntityBasicFarmer> {

	private GuiInfoTab infoTab;
	@SuppressWarnings("unused")
	private TextButton drawPreviewButton;

	public GuiBasicFarmer(ContainerBasicFarmer container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 48));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60, MachineSideMode.Input, getTileEntity()));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		BaseGuiTab powerTab;
		getTabManager().registerTab(powerTab = new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()));
		getTabManager().setInitiallyOpenTab(powerTab);

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 100));

//		registerWidget(drawPreviewButton = new TextButton(118, 40, 20, 20, "D", this::buttonPressed));
//		drawPreviewButton.setTooltip(new TranslationTextComponent("Draw Preview"));
//		drawPreviewButton.setToggleable(true);
//		setOutputSlotSize(16);
//		drawPreviewButton.setToggled(getTileEntity().getShouldDrawRadiusPreview());

		setOutputSlotSize(16);
	}

	public void buttonPressed(BaseButton button) {
		getTileEntity().setShouldDrawRadiusPreview(button.isToggled());
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground(-30, 5, 28, 60);
		drawGenericBackground(-30, 70, 28, 64);
		drawGenericBackground();
		// drawPlayerInventorySlots();

		drawContainerSlots(container.inventorySlots, getTileEntity().ioSideConfiguration);

		infoTab.setText("Farmer", "Farms plants in a " + TextFormatting.YELLOW + getTileEntity().getRadius() + " block=radius.==Requires " + TextFormatting.DARK_AQUA + "water" + TextFormatting.RESET
				+ " to operate=but other fluids may yield=better growth results...==Current Growth Factor: " + TextFormatting.GOLD + getTileEntity().getGrowthBonus() + "%");
	}
}
