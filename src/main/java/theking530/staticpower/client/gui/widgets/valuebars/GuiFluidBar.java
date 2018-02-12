package theking530.staticpower.client.gui.widgets.valuebars;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import api.gui.GuiDrawUtilities;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.utilities.GuiUtilities;
import theking530.staticpower.assists.utilities.RenderUtil;

public class GuiFluidBar {
	
	public static List<String> drawText(int fluidAmount, int maxCapacity, FluidStack fluid) {
		if(fluid != null) {	
			String name = fluid.getLocalizedName();
			String text = (name + "=" + NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity)+ "mB");
			String[] splitMsg = text.split("=");
			return Arrays.asList(splitMsg);
		}	
		String text = ("Empty" + "=" + NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity)+ "mB");
		String[] splitMsg = text.split("=");
		return Arrays.asList(splitMsg);
	}

	public static void drawFluidBar(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height) {
			GuiDrawUtilities.drawSlot((int)x, (int)(y-height), (int)width, (int)height);
			GlStateManager.disableLighting();
		    if (fluid != null && fluid.getFluid() != null) {
		    	
			    TextureAtlasSprite icon = RenderUtil.getStillTexture(fluid.getFluid());
			    if (icon != null) {
			    	float ratio = ((float)amount/(float)capacity);
				    float renderAmount = ratio * (float) height;
			
				    RenderUtil.bindBlockTexture();
				    GlStateManager.enableBlend();    
		
				    float segmentCapacity = capacity / ((float)height/16);
				    int segmentsUsed = (int) ((renderAmount+16)/16);
				    
			        double minU = icon.getMinU();
			        double maxU = icon.getMaxU();
			        double minV = icon.getMinV();
			        double maxV = icon.getMaxV();
			        double diffV = maxV - minV;
			        
					GL11.glColor3f(1.0f, 1.0f, 1.0f);
					
			        for(int i=0; i<segmentsUsed; i++) {
					    double currentSegmentCapacity = segmentCapacity*(i+1);
					    double fillRatio = Math.min(1.0, amount/currentSegmentCapacity);
		
					    double yMin = (i*16);
					    double yMax = ((i+1)*16)*fillRatio;				    	    
				        Tessellator tessellator = Tessellator.getInstance();
				        BufferBuilder tes = tessellator.getBuffer();
				        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
						tes.pos(x + width, y-yMin , zLevel).tex(maxU, minV).endVertex();
						tes.pos(x + width, y-yMax, zLevel).tex(maxU, minV + (fillRatio * diffV)).endVertex();
						tes.pos(x, y-yMax, zLevel).tex(minU, minV + (fillRatio * diffV)).endVertex();
						tes.pos(x, y-yMin , zLevel).tex(minU,minV).endVertex();	
				        tessellator.draw();
				        
			        }
				}
		    }
	    GlStateManager.enableAlpha();
	    GlStateManager.disableBlend();
	    
	    int linesColor = GuiUtilities.getColor(40, 40, 120);
	    for(int i=0; i<height/10; i++) {
	        Gui.drawRect((int)x, (int)(y-height)+2, (int)(x+width-10), (int)(y-height+3) , linesColor);
	        Gui.drawRect((int)x, (int)(y-height+7+(i*10)), (int)(x+width-10), (int)(y-height+8+(i*10)) , linesColor);
	        if(i != height/10 - 1) {
		        Gui.drawRect((int)x, (int)(y-height+12+(i*10)), (int)(x+width-7), (int)(y-height+13+(i*10)) , linesColor);	
	        }
	    }
		GlStateManager.enableLighting();
	}
}
