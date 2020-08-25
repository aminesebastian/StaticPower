package theking530.common.gui.widgets.valuebars;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.energy.StaticVoltHandler;

public class GuiPowerBarFromVoltHandler extends AbstractGuiWidget {

	private StaticVoltHandler energyStorage;

	public GuiPowerBarFromVoltHandler(StaticVoltHandler energyStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.energyStorage = energyStorage;
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		GuiPowerBarUtilities.drawPowerBar(ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f, energyStorage.getStoredPower(), energyStorage.getCapacity());
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		String currentCharge = energyStorage.getStoredPower() + " sC";
		String maxCharge = energyStorage.getCapacity() + " sC";
		
		tooltips.add(new StringTextComponent(currentCharge + "/" + maxCharge));
	}
}
