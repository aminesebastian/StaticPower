package theking530.api.gui.widgets.valuebars;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.AbstractGuiWidget;
import theking530.api.utilities.Vector2D;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public class GuiPowerBarFromEnergyStorage extends AbstractGuiWidget {

	private EnergyStorageComponent energyStorage;

	public GuiPowerBarFromEnergyStorage(EnergyStorageComponent energyStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.energyStorage = energyStorage;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		GuiPowerBarUtilities.drawPowerBar(ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f, energyStorage.getEnergyStored(),
				energyStorage.getMaxEnergyStored());
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiPowerBarUtilities.getTooltip(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored(), energyStorage.getMaxReceive()));
	}
}
