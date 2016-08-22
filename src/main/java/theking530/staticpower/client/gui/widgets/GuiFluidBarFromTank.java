package theking530.staticpower.client.gui.widgets;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class GuiFluidBarFromTank {
	
	IFluidTank TANK;
	GuiFluidBar FLUID_BAR;
	
	public GuiFluidBarFromTank(IFluidTank tank) {
		TANK = tank;
		FLUID_BAR = new GuiFluidBar();
	}
	public List drawText() {
		return FLUID_BAR.drawText(TANK.getFluidAmount(), TANK.getCapacity(), TANK.getFluid());
	}
	public void drawFluidBar(int xpos, int ypos, int width, int height, float zLevel) {  ;
		FLUID_BAR.drawFluidBar(TANK.getFluid(),TANK.getCapacity(), TANK.getFluidAmount(),xpos, ypos, zLevel, width, height);
	}
}
