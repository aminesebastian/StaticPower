package theking530.staticcore.gui.widgets.valuebars;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;

public class GuiHeatBarFromHeatStorage extends AbstractGuiWidget {

	private IHeatStorage heatStorage;

	public GuiHeatBarFromHeatStorage(IHeatStorage heatStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.heatStorage = heatStorage;
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		GuiHeatBarUtilities.drawHeatBar(ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f, heatStorage.getCurrentHeat(),
				heatStorage.getMaximumHeat());
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiHeatBarUtilities.getTooltip(heatStorage.getCurrentHeat(), heatStorage.getMaximumHeat(), heatStorage.getConductivity()));
	}
}