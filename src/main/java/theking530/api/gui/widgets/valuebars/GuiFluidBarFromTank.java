package theking530.api.gui.widgets.valuebars;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.IFluidTank;
import theking530.api.gui.widgets.AbstractGuiWidget;
import theking530.api.utilities.Vector2D;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable;

public class GuiFluidBarFromTank extends AbstractGuiWidget {

	private IFluidTank tank;
	private MachineSideMode mode;
	private ISideConfigurable sideConfigurable;

	public GuiFluidBarFromTank(IFluidTank tank, int xPosition, int yPosition, int xSize, int ySize, MachineSideMode mode, ISideConfigurable sideConfigurable) {
		super(xPosition, yPosition, xSize, ySize);
		this.tank = tank;
		this.mode = mode;
		this.sideConfigurable = sideConfigurable;
	}

	public GuiFluidBarFromTank(IFluidTank tank, int xPosition, int yPosition, int xSize, int ySize) {
		this(tank, xPosition, yPosition, xSize, ySize, null, null);
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		if (sideConfigurable != null && mode != null) {
			if (sideConfigurable.getSideWithModeCount(mode) > 0) {
				GuiFluidBarUtilities.drawFluidBar(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), 0.0f,
						getSize().getX(), getSize().getY(), mode, true);
				return;
			}
		}
		GuiFluidBarUtilities.drawFluidBar(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), ownerRelativePosition.getX(), ownerRelativePosition.getY() + getSize().getY(), 0.0f,
				getSize().getX(), getSize().getY(), true);
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiFluidBarUtilities.getTooltip(tank.getFluidAmount(), tank.getCapacity(), tank.getFluid()));
	}
}
