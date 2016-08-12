package theking530.staticpower.client.gui.widgets;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;

public class GuiPowerBar {


	private int MOUSE_X;
	private int MOUSE_Y;
	
	private ResourceLocation powerBar = new ResourceLocation(Reference.MODID + ":" + "textures/gui/PowerBar.png");
	
	public GuiPowerBar() {

	}
	public List drawText(int currentEnergy, int maxEnergy, int energyPerTick) {
    	String text = ("Max: " + energyPerTick + " RF/t" + "=" + NumberFormat.getNumberInstance(Locale.US).format(currentEnergy)  + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxEnergy) + " " + "RF");
    	String[] splitMsg = text.split("=");
		return Arrays.asList(splitMsg);
	}
	public void drawPowerBar(int xpos, int ypos, int width, int height, float zLevel, int currentEnergy, int maxEnergy) {
		float u1 = (float)currentEnergy/(float)maxEnergy;
		float k1 = u1 * height;
		
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(powerBar);
		GlStateManager.color(1, 1, 1);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xpos + width, ypos, zLevel).tex(1,0).endVertex();
		vertexbuffer.pos(xpos + width, ypos - k1, zLevel).tex(1,u1).endVertex();
		vertexbuffer.pos(xpos, ypos - k1, zLevel).tex(0,u1).endVertex();
		vertexbuffer.pos(xpos, ypos, zLevel).tex(0,0).endVertex();	
		tessellator.draw();
	}
}	
