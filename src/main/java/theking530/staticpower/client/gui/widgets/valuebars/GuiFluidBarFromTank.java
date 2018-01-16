package theking530.staticpower.client.gui.widgets.valuebars;

import java.util.List;

import net.minecraftforge.fluids.IFluidTank;

public class GuiFluidBarFromTank {
	
	IFluidTank TANK;
	
	public GuiFluidBarFromTank(IFluidTank tank) {
		TANK = tank;
	}
	public List<String> drawText() {
		return GuiFluidBar.drawText(TANK.getFluidAmount(), TANK.getCapacity(), TANK.getFluid());
	}
	public void drawFluidBar(int xpos, int ypos, int width, int height, float zLevel) {  ;
		GuiFluidBar.drawFluidBar(TANK.getFluid(),TANK.getCapacity(), TANK.getFluidAmount(),xpos, ypos, zLevel, width, height);
	}
}
