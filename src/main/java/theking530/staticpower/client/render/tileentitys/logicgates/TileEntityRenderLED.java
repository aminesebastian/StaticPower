package theking530.staticpower.client.render.tileentitys.logicgates;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.tileentity.gates.led.TileEntityLED;
import theking530.staticpower.utils.GUIUtilities;
import theking530.staticpower.utils.Vector3;

public class TileEntityRenderLED extends TileEntitySpecialRenderer {

    private final float PIXEL = 1f/16f;
    ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/BlankTexture.png");
    
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f, int dest) {
		TileEntityLED LED = (TileEntityLED)tileentity;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(0.0F, PIXEL*2, 0.0F);		
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);		 
		GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		bindTexture(TEXTURE);
		renderBlock(LED);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	public void renderBlock(TileEntityLED LED) {
		Vector3 vec3 = GUIUtilities.getColor(LED.COLOR);
		float POWER = ((float)LED.LIGHT_LEVEL+1f)/16f;
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		//Bottom
		GL11.glPushMatrix();
		GL11.glColor3f(((float)vec3.getX()/255f)*POWER, ((float)vec3.getY()/255f)*POWER, ((float)vec3.getZ()/255f)*POWER);
		vertexbuffer.pos(0, 0, 1).tex(1, 0).endVertex();	
		vertexbuffer.pos(0, 0, 0).tex(0, 0).endVertex();		
		vertexbuffer.pos(1, 0, 0).tex(0, 1).endVertex();		
		vertexbuffer.pos(1, 0, 1).tex(1, 1).endVertex();	
		//Top
		vertexbuffer.pos(1, PIXEL*2, 1).tex(0, 1).endVertex();			
		vertexbuffer.pos(1, PIXEL*2, 0).tex(1, 1).endVertex();		
		vertexbuffer.pos(0, PIXEL*2, 0).tex(1, 0).endVertex();		
		vertexbuffer.pos(0, PIXEL*2, 1).tex(0, 0).endVertex();
		//Back
		vertexbuffer.pos(0, 0, 0).tex(1, 1).endVertex();	
		vertexbuffer.pos(0, PIXEL*2, 0).tex(1, 0).endVertex();	
		vertexbuffer.pos(1, PIXEL*2, 0).tex(0, 0).endVertex();	
		vertexbuffer.pos(1, 0, 0).tex(0, 1).endVertex();	
		//Front
		vertexbuffer.pos(0, PIXEL*2, 1).tex(0, 0).endVertex();
		vertexbuffer.pos(0, 0, 1).tex(0, 1).endVertex();	
		vertexbuffer.pos(1, 0, 1).tex(1, 1).endVertex();	
		vertexbuffer.pos(1, PIXEL*2, 1).tex(1, 0).endVertex();	
		//Right
		vertexbuffer.pos(0, 0, 1).tex(1, 1).endVertex();	
		vertexbuffer.pos(0, PIXEL*2, 1).tex(1, 0).endVertex();	
		vertexbuffer.pos(0, PIXEL*2, 0).tex(0, 0).endVertex();	
		vertexbuffer.pos(0, 0, 0).tex(0, 1).endVertex();	
		//Left
		vertexbuffer.pos(1, 0, 0).tex(1, 1).endVertex();	
		vertexbuffer.pos(1, PIXEL*2, 0).tex(1, 0).endVertex();	
		vertexbuffer.pos(1, PIXEL*2, 1).tex(0, 0).endVertex();	
		vertexbuffer.pos(1, 0, 1).tex(0, 1).endVertex();	
		GL11.glPopMatrix();
		tessellator.draw();	
		GL11.glColor3f(1f, 1f, 1f);
	}
}



