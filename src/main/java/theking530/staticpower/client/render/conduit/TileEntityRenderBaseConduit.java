package theking530.staticpower.client.render.conduit;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;
import theking530.staticpower.utils.StaticVertexBuffer;

public class TileEntityRenderBaseConduit extends TileEntitySpecialRenderer<TileEntity> {

	boolean drawInside = true;
	
	float pixel = 1F/16F;
	float texel = 1F/64F;

	public void drawConnection(EnumFacing direction, TileEntity tileentity) {
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertextBuffer = tessellator.getBuffer();
        vertextBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			if (direction.equals(EnumFacing.DOWN)) {
				GL11.glRotatef(0, 0, 0, 0);
			}else if (direction.equals(EnumFacing.UP)) {
				GL11.glRotatef(180, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.NORTH)) {
				GL11.glRotatef(90, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.SOUTH)) {
				GL11.glRotatef(270, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.EAST)) {
				GL11.glRotatef(90, 0, 0, 1);
			}	
			else if (direction.equals(EnumFacing.WEST)) {
				GL11.glRotatef(270, 0, 0, 1);
			}	
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			//Front
			StaticVertexBuffer.pos(9*pixel/2, 1, 1-9*pixel/2, 0*texel, 10*texel);
			StaticVertexBuffer.pos(9*pixel/2, 28*pixel/2, 1-9*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(1-9*pixel/2, 28*pixel/2, 1-9*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(1-9*pixel/2, 1, 1-9*pixel/2, 10*texel, 10*texel);		
			
			//Right
			StaticVertexBuffer.pos(1-9*pixel/2, 1, 1-9*pixel/2, 0*texel, 10*texel);	
			StaticVertexBuffer.pos(1-9*pixel/2, 28*pixel/2, 1-9*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(1-9*pixel/2, 28*pixel/2, 9*pixel/2, 10*texel, 0*texel);
			StaticVertexBuffer.pos(1-9*pixel/2, 1, 9*pixel/2, 10*texel, 10*texel);		
	
			//Back
			StaticVertexBuffer.pos(1-9*pixel/2, 1, 9*pixel/2, 0*texel, 10*texel);	
			StaticVertexBuffer.pos(1-9*pixel/2, 28*pixel/2, 9*pixel/2, 0*texel, 0*texel);
			StaticVertexBuffer.pos(9*pixel/2, 28*pixel/2, 9*pixel/2, 10*texel, 0*texel);
			StaticVertexBuffer.pos(9*pixel/2, 1, 9*pixel/2, 10*texel, 10*texel);
	
			//Left
			StaticVertexBuffer.pos(9*pixel/2, 1, 9*pixel/2, 0*texel, 10*texel);	
			StaticVertexBuffer.pos(9*pixel/2, 28*pixel/2, 9*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(9*pixel/2, 28*pixel/2, 1-9*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(9*pixel/2, 1, 1-9*pixel/2, 10*texel, 10*texel);	
	
			//Top
			StaticVertexBuffer.pos(9*pixel/2, 28*pixel/2, 1-9*pixel/2, 0*texel, 10*texel);
			StaticVertexBuffer.pos(9*pixel/2, 28*pixel/2, 9*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(1-9*pixel/2, 28*pixel/2, 9*pixel/2, 10*texel, 0*texel);
			StaticVertexBuffer.pos(1-9*pixel/2, 28*pixel/2, 1-9*pixel/2, 10*texel, 10*texel);
			
			//Bottom
			StaticVertexBuffer.pos(1-9*pixel/2, 1, 1-9*pixel/2, 0*texel, 10*texel);	
			StaticVertexBuffer.pos(1-9*pixel/2, 1, 9*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(9*pixel/2, 1, 9*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(9*pixel/2, 1, 1-9*pixel/2, 10*texel, 10*texel);	

		//Front
		StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 10*texel);		
		StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 10*texel);	
		StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
		StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);			
		
		//Back
		StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
		StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
		StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 0*texel);	
		StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 0*texel);	
		
		//Left
		StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
		StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
		StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
		StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
		
		//Right
		StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
		StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
		StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
		StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
		
		if(drawInside) {
			//Front
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 10*texel);		

			//Back
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
	
			//Left
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
		
			//Right
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
			
			//Bottom
			StaticVertexBuffer.pos(11*pixel/2, 1, 1-11*pixel/2, 10*texel, 10*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 1, 11*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(1-11*pixel/2, 1, 11*pixel/2, 0*texel, 0*texel);				
			StaticVertexBuffer.pos(1-11*pixel/2, 1, 1-11*pixel/2, 0*texel, 10*texel);	
		}
		tessellator.draw();
		GL11.glPopMatrix();
	}
	public void drawStraight(EnumFacing direction) {
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertextBuffer = tessellator.getBuffer();
        vertextBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			if (direction.equals(EnumFacing.UP) || (direction.equals(EnumFacing.DOWN))) {
			}else if (direction.equals(EnumFacing.SOUTH) || (direction.equals(EnumFacing.NORTH))) {
				GL11.glRotatef(90, 1, 0, 0);
			}
			else if (direction.equals(EnumFacing.WEST) || (direction.equals(EnumFacing.EAST))) {
				GL11.glRotatef(90, 0, 0, 1);
			}	
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);		
			
			//Front
			StaticVertexBuffer.pos(1-12*pixel/2, 0, 1-12*pixel/2, 20*texel, 10*texel);		
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 52*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 52*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 0, 1-12*pixel/2, 20*texel, 0*texel);			
			
			//Back
			StaticVertexBuffer.pos(12*pixel/2, 0, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 52*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 52*texel, 0*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 0, 12*pixel/2, 20*texel, 0*texel);	
			
			//Left
			StaticVertexBuffer.pos(1-12*pixel/2, 0, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 52*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 52*texel, 0*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 0, 1-12*pixel/2, 20*texel, 0*texel);	
			
			//Right
			StaticVertexBuffer.pos(12*pixel/2, 0, 1-12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 52*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 52*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 0, 12*pixel/2, 20*texel, 10*texel);	
			
			if(drawInside) {
				//Front
				StaticVertexBuffer.pos(12*pixel/2, 0, 1-12*pixel/2, 20*texel, 0*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 52*texel, 0*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 52*texel, 10*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 0, 1-12*pixel/2, 20*texel, 10*texel);		
	
				//Back
				StaticVertexBuffer.pos(1-12*pixel/2, 0, 12*pixel/2, 20*texel, 0*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 52*texel, 0*texel);
				StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 52*texel, 10*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 0, 12*pixel/2, 20*texel, 10*texel);	
				
				//Left
				StaticVertexBuffer.pos(1-12*pixel/2, 0, 1-12*pixel/2, 20*texel, 0*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 52*texel, 0*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 52*texel, 10*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 0, 12*pixel/2, 20*texel, 10*texel);	
	
				//Right
				StaticVertexBuffer.pos(12*pixel/2, 0, 12*pixel/2, 20*texel, 10*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 52*texel, 10*texel);
				StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 52*texel, 0*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 0, 1-12*pixel/2, 20*texel, 0*texel);	
			}
		}
		tessellator.draw();		
		GL11.glPopMatrix();
	}
	public void drawCore(EnumFacing direction) {
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertextBuffer = tessellator.getBuffer();
        vertextBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			if (direction.equals(EnumFacing.UP)) {
			//ROTATE
			}else if (direction.equals(EnumFacing.DOWN)) {
				GL11.glRotatef(180, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.SOUTH)) {
				GL11.glRotatef(90, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.NORTH)) {
				GL11.glRotatef(270, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.WEST)) {
				GL11.glRotatef(90, 0, 0, 1);
			}	
			else if (direction.equals(EnumFacing.EAST)) {
				GL11.glRotatef(270, 0, 0, 1);
			}	
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			
			
			//Front
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 10*texel);		
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);			
			
			//Back
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 0*texel);	
			
			//Left
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
			
			//Right
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
			StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
			
			if(drawInside) {
				//Front
				StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 10*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 10*texel);		

				//Back
				StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 0*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
		
				//Left
				StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);
				StaticVertexBuffer.pos(1-12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
				StaticVertexBuffer.pos(1-12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
			
				//Right
				StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 12*pixel/2, 10*texel, 10*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1, 12*pixel/2, 20*texel, 10*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1, 1-12*pixel/2, 20*texel, 0*texel);	
				StaticVertexBuffer.pos(12*pixel/2, 1-12*pixel/2, 1-12*pixel/2, 10*texel, 0*texel);	

			}

		}
		tessellator.draw();		
		GL11.glPopMatrix();
	}
	public void drawNode(TileEntity tileentity) {
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertextBuffer = tessellator.getBuffer();
        vertextBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		{		
			//Front
			StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);		
			StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);	
			
			//Right
			StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 11*pixel/2, 10*texel, 10*texel);		
			StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 11*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);	
			
			//Back
			StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 11*pixel/2, 10*texel, 10*texel);
			StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 11*pixel/2, 10*texel, 0*texel);
			StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 11*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 11*pixel/2, 0*texel, 10*texel);	
			
			//Left
			StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 11*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 11*pixel/2, 0*texel, 10*texel);		
			
			//Top
			StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);	
			StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 11*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 11*pixel/2, 0*texel, 0*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);		
			
			//Bottom
			StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);	
			StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 11*pixel/2, 10*texel, 0*texel);	
			StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 11*pixel/2, 0*texel, 0*texel);				
			StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);	
			
			if(drawInside) {
				//Front
				StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);	
				StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 0*texel, 0*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);		
				
				//Right
				StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 0*texel, 0*texel);
				StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 11*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 11*pixel/2, 10*texel, 10*texel);		

				//Back
				StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 11*pixel/2, 0*texel, 10*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 11*pixel/2, 0*texel, 0*texel);	
				StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 11*pixel/2, 10*texel, 0*texel);
				StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 11*pixel/2, 10*texel, 10*texel);
	
				//Left
				StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 11*pixel/2, 0*texel, 10*texel);		
				StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 11*pixel/2, 0*texel, 0*texel);	
				StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);	

				//Top
				StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);	
				StaticVertexBuffer.pos(11*pixel/2, 1-11*pixel/2, 11*pixel/2, 0*texel, 0*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 11*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);	
				
				//Bottom
				StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 1-11*pixel/2, 0*texel, 10*texel);	
				StaticVertexBuffer.pos(1-11*pixel/2, 11*pixel/2, 11*pixel/2, 0*texel, 0*texel);	
				StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 11*pixel/2, 10*texel, 0*texel);	
				StaticVertexBuffer.pos(11*pixel/2, 11*pixel/2, 1-11*pixel/2, 10*texel, 10*texel);	
			}
		}
		tessellator.draw();
	}
}
