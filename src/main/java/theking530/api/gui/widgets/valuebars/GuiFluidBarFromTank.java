package theking530.api.gui.widgets.valuebars;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.IFluidTank;
import theking530.api.gui.widgets.IGuiWidget;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.tileentities.utilities.SideModeList.Mode;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable;

public class GuiFluidBarFromTank implements IGuiWidget {

	IFluidTank tank;
	private boolean isVisible;
	private int xPosition;
	private int yPosition;
	private int xSize;
	private int ySize;
	private StaticPowerContainerGui<?> owningGui;

	private Mode mode;
	private ISideConfigurable sideConfigurable;

	public GuiFluidBarFromTank(IFluidTank tank, int xPosition, int yPosition, int xSize, int ySize, Mode mode, ISideConfigurable sideConfigurable) {
		this.tank = tank;
		this.isVisible = true;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.xSize = xSize;
		this.ySize = ySize;
		this.mode = mode;
		this.sideConfigurable = sideConfigurable;
	}

	public GuiFluidBarFromTank(IFluidTank tank, int xPosition, int yPosition, int xSize, int ySize) {
		this(tank, xPosition, yPosition, xSize, ySize, null, null);
	}

	public GuiFluidBarFromTank(IFluidTank tank) {
		this(tank, 0, 0, 0, 0);
	}

	@Override
	public void setOwningGui(StaticPowerContainerGui<?> owningGui) {
		this.owningGui = owningGui;
	}

	public List<ITextComponent> drawText() {
		return GuiFluidBarUtilities.getTooltip(tank.getFluidAmount(), tank.getCapacity(), tank.getFluid());
	}

	public void drawFluidBar(int xpos, int ypos, int width, int height, float zLevel) {

	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public IGuiWidget setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	@Override
	public IGuiWidget setPosition(int xPos, int yPos) {
		xPosition = xPos;
		yPosition = yPos;
		return this;
	}

	@Override
	public IGuiWidget setSize(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		return this;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (sideConfigurable != null && mode != null) {
			if (sideConfigurable.getSideWithModeCount(mode) > 0) {
				GuiFluidBarUtilities.drawFluidBar(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), owningGui.getGuiLeft() + xPosition, owningGui.getGuiTop() + yPosition, 0.0f, xSize, ySize,
						mode, true);
				return;
			}
		}
		GuiFluidBarUtilities.drawFluidBar(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), owningGui.getGuiLeft() + xPosition, owningGui.getGuiTop() + yPosition, 0.0f, xSize, ySize, true);
	}

	@Override
	public void renderForeground(int mouseX, int mouseY, float partialTicks) {
	}

	@Override
	public boolean shouldDrawTooltip(int mouseX, int mouseY) {
		if (mouseX >= owningGui.getGuiLeft() + xPosition && mouseY <= owningGui.getGuiTop() + yPosition && mouseX <= owningGui.getGuiLeft() + xPosition + xSize
				&& mouseY >= owningGui.getGuiTop() + yPosition - ySize) {
			return true;
		}
		return false;
	}

	@Override
	public void getTooltips(List<ITextComponent> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiFluidBarUtilities.getTooltip(tank.getFluidAmount(), tank.getCapacity(), tank.getFluid()));
	}

	@Override
	public void mouseHover(int mouseX, int mouseY) {
	}
}
