package theking530.api.gui.widgets.valuebars;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.IGuiWidget;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.tileentities.TileEntityMachine;

public class GuiPowerBarFromEnergyStorage implements IGuiWidget {

	private TileEntityMachine machine;

	private StaticPowerContainerGui<?> owningGui;
	private boolean isVisible;

	private int xPosition;
	private int yPosition;
	private int xSize;
	private int ySize;

	public GuiPowerBarFromEnergyStorage(TileEntityMachine machine, int xPosition, int yPosition, int xSize, int ySize) {
		this.machine = machine;
		isVisible = true;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public GuiPowerBarFromEnergyStorage(TileEntityMachine machine) {
		this.machine = machine;
		isVisible = true;
	}

	@Override
	public void setOwningGui(StaticPowerContainerGui<?> owningGui) {
		this.owningGui = owningGui;
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
		GuiPowerBarUtilities.drawPowerBar(owningGui.getGuiLeft() + xPosition, owningGui.getGuiTop() + yPosition, xSize, ySize, 0.0f, machine.getEnergyStorage().getEnergyStored(),
				machine.getEnergyStorage().getMaxEnergyStored());
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
		if (machine.processingTime == 0) {
			tooltips.addAll(
					GuiPowerBarUtilities.getTooltip(machine.getEnergyStorage().getEnergyStored(), machine.getEnergyStorage().getMaxEnergyStored(), machine.getEnergyStorage().getMaxReceive(), 0));
		}
		tooltips.addAll(GuiPowerBarUtilities.getTooltip(machine.getEnergyStorage().getEnergyStored(), machine.getEnergyStorage().getMaxEnergyStored(), machine.getEnergyStorage().getMaxReceive(),
				machine.getProcessingEnergy() / machine.processingTime));
	}

	@Override
	public void mouseHover(int mouseX, int mouseY) {

	}
}
