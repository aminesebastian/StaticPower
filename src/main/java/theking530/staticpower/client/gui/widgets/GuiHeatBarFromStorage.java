package theking530.staticpower.client.gui.widgets;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.machines.heatingelement.HeatStorage;
import theking530.staticpower.utils.RenderUtil;

public class GuiHeatBarFromStorage {

	public HeatStorage STORAGE;
	public GuiHeatBar BAR;
	
	public GuiHeatBarFromStorage(HeatStorage storage) {
		STORAGE = storage;
		BAR = new GuiHeatBar();
	}
	public void drawHeatBar(double x, double y, double zLevel, double width, double height) {
		BAR.drawHeatBar(STORAGE.getMaxHeat(), STORAGE.getHeat(), x, y, zLevel, width, height);
	}
	public List drawText() {
		return BAR.drawText(STORAGE.getHeat(), STORAGE.getMaxHeat());
	}
}
