package theking530.api.gui.widgets.valuebars;

import java.util.List;
import java.util.Optional;

import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.AbstractGuiWidget;
import theking530.api.utilities.Vector2D;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class GuiPowerBarFromEnergyStorage extends AbstractGuiWidget {

	private TileEntityMachine machine;

	public GuiPowerBarFromEnergyStorage(TileEntityMachine machine, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.machine = machine;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		Optional<EnergyStorageComponent> energyComponent = ComponentUtilities.getComponent(EnergyStorageComponent.class, machine);
		if (energyComponent.isPresent()) {
			GuiPowerBarUtilities.drawPowerBar(ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f,
					energyComponent.get().getEnergyStored(), energyComponent.get().getMaxEnergyStored());
		}

	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		Optional<EnergyStorageComponent> energyComponent = ComponentUtilities.getComponent(EnergyStorageComponent.class, machine);
		if (energyComponent.isPresent()) {
			if (machine.processingTime == 0) {
				tooltips.addAll(GuiPowerBarUtilities.getTooltip(energyComponent.get().getEnergyStored(), energyComponent.get().getMaxEnergyStored(), energyComponent.get().getMaxReceive(), 0));
			}
			tooltips.addAll(GuiPowerBarUtilities.getTooltip(energyComponent.get().getEnergyStored(), energyComponent.get().getMaxEnergyStored(), energyComponent.get().getMaxReceive(),
					machine.getProcessingEnergy() / machine.processingTime));
		}
	}
}
