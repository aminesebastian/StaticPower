package theking530.staticcore.gui.widgets.valuebars;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.power.IStaticVoltHandler;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class GuiPowerBarFromEnergyStorage extends AbstractGuiWidget {

	private IStaticVoltHandler energyStorage;

	public GuiPowerBarFromEnergyStorage(IStaticVoltHandler energyStorage, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.energyStorage = energyStorage;
	}

	@Override
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		GuiPowerBarUtilities.drawPowerBar(matrix, getPosition().getX(), getPosition().getY() + getSize().getY(), getSize().getX(), getSize().getY(), 0.0f, energyStorage.getStoredPower(),
				energyStorage.getCapacity());
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiPowerBarUtilities.getTooltip(energyStorage.getStoredPower(), energyStorage.getCapacity(), energyStorage.getMaxReceive()));
	}
}
