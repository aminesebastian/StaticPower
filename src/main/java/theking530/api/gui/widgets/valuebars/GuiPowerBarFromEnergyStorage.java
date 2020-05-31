package theking530.api.gui.widgets.valuebars;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.AbstractGuiWidget;
import theking530.api.utilities.Vector2D;
import theking530.staticpower.tileentities.TileEntityMachine;

public class GuiPowerBarFromEnergyStorage extends AbstractGuiWidget {

	private TileEntityMachine machine;

	public GuiPowerBarFromEnergyStorage(TileEntityMachine machine, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.machine = machine;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		GuiPowerBarUtilities.drawPowerBar(ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f, machine.getEnergyStorage().getEnergyStored(),
				machine.getEnergyStorage().getMaxEnergyStored());
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		if (machine.processingTime == 0) {
			tooltips.addAll(
					GuiPowerBarUtilities.getTooltip(machine.getEnergyStorage().getEnergyStored(), machine.getEnergyStorage().getMaxEnergyStored(), machine.getEnergyStorage().getMaxReceive(), 0));
		}
		tooltips.addAll(GuiPowerBarUtilities.getTooltip(machine.getEnergyStorage().getEnergyStored(), machine.getEnergyStorage().getMaxEnergyStored(), machine.getEnergyStorage().getMaxReceive(),
				machine.getProcessingEnergy() / machine.processingTime));
	}
}
