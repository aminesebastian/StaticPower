package theking530.staticpower.client.gui.widgets.valuebars;

import java.util.List;

import api.gui.IGuiWidget;
import net.minecraftforge.fluids.IFluidTank;
import theking530.staticpower.client.gui.BaseGuiContainer;

public class GuiFluidBarFromTank implements IGuiWidget{
	
	IFluidTank tank;
	private boolean isVisible;
	private int xPosition;
	private int yPosition;
	private int xSize;
	private int ySize;
	private BaseGuiContainer owningGui;
	
	public GuiFluidBarFromTank(IFluidTank tank, int xPosition, int yPosition, int xSize, int ySize) {
		this.tank = tank;
		this.isVisible = true;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.xSize = xSize;
		this.ySize = ySize;
	}
	public GuiFluidBarFromTank(IFluidTank tank) {
		this.tank = tank;
		this.isVisible = true;
	}
	
	@Override
	public void setOwningGui(BaseGuiContainer owningGui) {
		this.owningGui = owningGui;		
	}
	public List<String> drawText() {
		return GuiFluidBar.drawText(tank.getFluidAmount(), tank.getCapacity(), tank.getFluid());
	}
	public void drawFluidBar(int xpos, int ypos, int width, int height, float zLevel) {  
		GuiFluidBar.drawFluidBar(tank.getFluid(),tank.getCapacity(), tank.getFluidAmount(),xpos, ypos, zLevel, width, height);
	}
	@Override
	public boolean isVisible() {
		return isVisible;
	}
	@Override
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	@Override
	public void setPosition(int xPos, int yPos) {
		xPosition = xPos;
		yPosition = yPos;		
	}
	@Override
	public void setSize(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;		
	}
	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		GuiFluidBar.drawFluidBar(tank.getFluid(),tank.getCapacity(), tank.getFluidAmount(), owningGui.getGuiLeft()+xPosition, owningGui.getGuiTop()+yPosition, 0.0f, xSize, ySize);
	}
	@Override
	public void renderForeground(int mouseX, int mouseY, float partialTicks) {}
	
	@Override
	public boolean shouldDrawTooltip(int mouseX, int mouseY) {
		if(mouseX >= owningGui.getGuiLeft()+xPosition && mouseY <= owningGui.getGuiTop()+yPosition  && mouseX <= owningGui.getGuiLeft()+xPosition+xSize && mouseY >= owningGui.getGuiTop()+yPosition-ySize ) {	
			return true; 
		}
		return false;    
	}
	@Override
	public List<String> getTooltip() {
		return GuiFluidBar.drawText(tank.getFluidAmount(), tank.getCapacity(), tank.getFluid());
	}
	@Override
	public void mouseHover(int mouseX, int mouseY) {		
	}
}
