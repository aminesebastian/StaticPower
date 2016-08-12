package theking530.staticpower.client.gui.widgets;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import theking530.staticpower.machines.BaseMachine;

public class GuiPowerBarFromEnergyStorage {

	GuiPowerBar POWER_BAR = new GuiPowerBar();
	private BaseMachine MACHINE;
	
	public GuiPowerBarFromEnergyStorage(BaseMachine teInfuser) {
		MACHINE = teInfuser;
	}
	
	public List drawText() {
		return POWER_BAR.drawText(MACHINE.STORAGE.getEnergyStored(), MACHINE.STORAGE.getMaxEnergyStored(), MACHINE.STORAGE.getMaxReceive());
	}
	public void drawPowerBar(int xpos, int ypos, int width, int height, float zLevel) {
		POWER_BAR.drawPowerBar(xpos, ypos, width, height, zLevel, MACHINE.STORAGE.getEnergyStored(), MACHINE.STORAGE.getMaxEnergyStored());
	}
}
