package theking530.staticpower.tileentities.nonpowered.vacuumchest;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.tabs.GuiRedstoneTab;
import theking530.api.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiVacuumChest extends StaticPowerTileEntityGui<ContainerVacuumChest, TileEntityVacuumChest> {

	private GuiInfoTab infoTab;
	private GuiFluidBarFromTank fluidBar;

	public GuiVacuumChest(ContainerVacuumChest container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		registerWidget(fluidBar = new GuiFluidBarFromTank(getTileEntity().getTank(), 176, 72, 16, 60));
		tabManager.registerTab(infoTab = new GuiInfoTab(100, 65));

		if (getTileEntity().showTank()) {
			xSize = 200;
		}

		getTabManager().registerTab(new GuiRedstoneTab(100, 85, getTileEntity()));
		setOutputSlotSize(16);
	}

	@Override
	protected void drawBackgroundExtras(float f, int i, int j) {
		drawGenericBackground();
		drawPlayerInventorySlots(guiLeft + 8, guiTop + ySize - 83);
		drawContainerSlots(getTileEntity(), container.inventorySlots);

		DecimalFormat format = new DecimalFormat("##.###");
		String text = ("Vacuums items in a  =nearby radius. ==" + TextFormatting.RED + "Radius: " + TextFormatting.AQUA + format.format(getTileEntity().getRadius()) + " Blocks");
		String[] splitMsg = text.split("=");
		infoTab.setText(getTitle().getFormattedText(), Arrays.asList(splitMsg));

		if (!getTileEntity().showTank()) {
			setGuiSizeTarget(176, 185);
			fluidBar.setVisible(false);
		} else {
			setGuiSizeTarget(200, 185);
			drawSlot(guiLeft + 176, guiTop + 12, 16, 60);
			fluidBar.setVisible(true);
		}
	}
}
