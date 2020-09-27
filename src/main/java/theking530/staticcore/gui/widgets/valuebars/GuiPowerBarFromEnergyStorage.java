package theking530.staticcore.gui.widgets.valuebars;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.power.StaticVoltHandler;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class GuiPowerBarFromEnergyStorage extends AbstractGuiWidget {

	private StaticVoltHandler energyStorage;

	public GuiPowerBarFromEnergyStorage(StaticVoltHandler energyStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.energyStorage = energyStorage;
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		GuiPowerBarUtilities.drawPowerBar(ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f, energyStorage.getStoredPower(),
				energyStorage.getCapacity());
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiPowerBarUtilities.getTooltip(energyStorage.getStoredPower(), energyStorage.getCapacity(), energyStorage.getMaxReceive()));
	}
}
