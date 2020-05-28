package theking530.staticpower.tileentity.vacuumchest;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.BaseContainerGui;

public class GuiVacuumChest extends BaseContainerGui<ContainerVacuumChest> {

	private TileEntityVacuumChest vacuumChest;
	private GuiInfoTab infoTab;
	private GuiFluidBarFromTank fluidBar;

	public GuiVacuumChest(ContainerVacuumChest container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
		vacuumChest = container.getOwningTileEntity();

		registerWidget(fluidBar = new GuiFluidBarFromTank(vacuumChest.getTank(), 176, 72, 16, 60));

		if (vacuumChest.showTank()) {
			xSize = 200;
		}

		infoTab = new GuiInfoTab(100, 65);
		tabManager.registerTab(infoTab);
		// getTabManager().registerTab(new GuiRedstoneTab(100, 85, teVChest));
		tabManager.setInitiallyOpenTab(infoTab);
		setOutputSlotSize(16);
	}

	@Override
	protected void drawBackgroundExtras(float f, int i, int j) {
		drawGenericBackground();
		drawPlayerInventorySlots(guiLeft + 8, guiTop + ySize - 83);
		drawContainerSlots(vacuumChest, container.inventorySlots);

		DecimalFormat format = new DecimalFormat("##.###");
		String text = ("Vacuums items in a  =nearby radius. ==" + TextFormatting.RED + "Radius: " + TextFormatting.AQUA + format.format(vacuumChest.getRadius()) + " Blocks");
		String[] splitMsg = text.split("=");
		infoTab.setText(getContainerName().getString(), Arrays.asList(splitMsg));

		if (!vacuumChest.showTank()) {
			setGuiSizeTarget(176, 185);
			fluidBar.setVisible(false);
		} else {
			setGuiSizeTarget(200, 185);
			drawSlot(guiLeft + 176, guiTop + 12, 16, 60);
			fluidBar.setVisible(true);
		}
	}

	@Override
	protected ITextComponent getContainerName() {
		return new TranslationTextComponent("container.staticpower." + vacuumChest.getName());
	}
}
