package theking530.staticpower.tileentities.powered.poweredgrinder;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.gui.widgets.progressbars.GrinderProgressBar;
import theking530.api.gui.widgets.tabs.BaseGuiTab;
import theking530.api.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.api.gui.widgets.tabs.GuiSideConfigTab;
import theking530.api.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.api.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;

public class GuiPoweredGrinder extends StaticPowerTileEntityGui<ContainerPoweredGrinder, TileEntityPoweredGrinder> {
	private GuiInfoTab infoTab;

	public GuiPoweredGrinder(ContainerPoweredGrinder container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 54));
		registerWidget(new GrinderProgressBar(79, 38, 18, 17).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 60));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		BaseGuiTab powerTab;
		getTabManager().registerTab(powerTab = new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT));
		getTabManager().setInitiallyOpenTab(powerTab);

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawContainerSlots(container.inventorySlots, getTileEntity().ioSideConfiguration);
		drawPlayerInventorySlots();

		String text = ("Grinds items into=their base components. ==" + "Bonus Chance: " + TextFormatting.GREEN + 100.0f + "%");
		infoTab.setText(getTileEntity().getDisplayName().getFormattedText(), text);
	}
}
