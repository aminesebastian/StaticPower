package theking530.staticpower.client.render.conduit;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.conduits.TileEntityBaseConduit;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;

public class TileEntityRenderStaticConduit extends TileEntityRenderBaseConduit {
	
	ResourceLocation energy = new ResourceLocation(Reference.MODID, "textures/models/conduits/ConduitEnergy.png");
	ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/models/conduits/FluidConduit.png");
	ResourceLocation pullTexture = new ResourceLocation(Reference.MODID, "textures/models/conduits/FluidConduitPull.png");
	
	boolean drawInside = true;
	
	float pixel = 1F/16F;
	float texel = 1F/64F;

	@Override
	public void render(TileEntityBaseConduit tileentity, double translationX, double translationY, double translationZ, float f, int dest, float alpha) {
		TileEntityStaticConduit conduit = (TileEntityStaticConduit) tileentity;
		float energyAmount = conduit.STORAGE.getEnergyStored();
		
		GL11.glTranslated(translationX, translationY, translationZ);
		GL11.glDisable(GL11.GL_LIGHTING);	
		
		for(int i = 0; i < conduit.receivers.length; i++){
			if(conduit.receivers[i] != null) {
				if(conduit.SIDE_MODES[i] == 2) {
					
				}else{
					if(conduit.SIDE_MODES[i] == 1) {
						this.bindTexture(pullTexture);
					}else{
						this.bindTexture(texture);
					}
				drawConnection(conduit.receivers[i], tileentity);
				this.bindTexture(energy);
				//drawConnectionEnergy(conduit.receivers[i], tileentity);
				}		
			}
		}
		if (!conduit.straightConnection(conduit.connections)) {
			this.bindTexture(texture);
			drawNode(tileentity); 		
			this.bindTexture(energy);
			//drawNodeEnergy(conduit);
			for(int i = 0; i < conduit.connections.length; i++) {
				if(conduit.connections[i] != null) {
					this.bindTexture(energy);
					//drawCoreEnergy(conduit.connections[i], conduit);
					this.bindTexture(texture);
					drawCore(conduit.connections[i], tileentity);
				}
			}
		} else {
			for(int i = 0; i < conduit.connections.length; i++) 
				if(conduit.connections[i] != null) {	
					this.bindTexture(texture);
					drawStraight(conduit.connections[i], tileentity);
					this.bindTexture(energy);
					//drawStraightEnergy(conduit.connections[i], conduit);
					break;
				}
			}
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(-translationX, -translationY, -translationZ);	
	}

	/**
	public void drawConnectionEnergy(EnumFacing direction, TileEntity tileentity) {
		TileEntityStaticConduit conduit = (TileEntityStaticConduit) tileentity;
		float energyLevel = conduit.getEnergyAdjusted()*(3.5f*pixel/2);
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads(); 
		{	
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
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5-energyLevel, 0, 1);		
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5-energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5-energyLevel, 0, 0);			
			
			//Back
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5+energyLevel, 0, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5+energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5+energyLevel, 1, 0);	
			
			//Left
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5-energyLevel, 0, 0);	
			
			//Right
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5-energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5+energyLevel, 0, 1);	
		}
		tessellator.draw();
		GL11.glPopMatrix();
	}
	public void drawStraightEnergy(EnumFacing direction, TileEntity tileentity) {	
		TileEntityStaticConduit conduit = (TileEntityStaticConduit) tileentity;
		float energyLevel = conduit.getEnergyAdjusted()*(3.5f*pixel/2);
	
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads(); 
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
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5-energyLevel, 0, 1);		
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5-energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5-energyLevel, 0, 0);			
			
			//Back
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5+energyLevel, 0, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5+energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5+energyLevel, 0, 0);	
			
			//Left
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5+energyLevel, 0, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, 1, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5-energyLevel, 0, 0);	
			
			//Right
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5-energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, 1, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5+energyLevel, 0, 1);		
		}
		tessellator.draw();		
		GL11.glPopMatrix();
	}
	public void drawCoreEnergy(EnumFacing direction, TileEntity tileentity) {
		TileEntityStaticConduit conduit = (TileEntityStaticConduit) tileentity;
		float energyLevel = conduit.getEnergyAdjusted()*(3.5f*pixel/2);
	
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads(); 
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			if (direction.equals(EnumFacing.UP)) {
				GL11.glRotatef(180, 1, 0, 0);
			}else if (direction.equals(EnumFacing.DOWN)) {
				GL11.glRotatef(-180, 0, 1, 0);
			}	
			else if (direction.equals(EnumFacing.SOUTH)) {
				GL11.glRotatef(-90, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.NORTH)) {
				GL11.glRotatef(-270, 1, 0, 0);
			}	
			else if (direction.equals(EnumFacing.WEST)) {
				GL11.glRotatef(-90, 0, 0, 1);
			}	
			else if (direction.equals(EnumFacing.EAST)) {
				GL11.glRotatef(-270, 0, 0, 1);
			}	
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			
			//Front
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5-energyLevel, 0, 1);		
			tessellator.addVertexWithUV(.5-energyLevel, .5, .5-energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, .5, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5-energyLevel, 0, 0);			
			
			//Back
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5+energyLevel, 0, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, .5, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, .5, .5+energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5+energyLevel, 0, 0);	
			
			//Left
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5+energyLevel, 0, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, .5, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, .5, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, 0, .5-energyLevel, 0, 0);	
			
			//Right
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5-energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, 0, .5+energyLevel, 0, 1);		
		}
		tessellator.draw();		
		GL11.glPopMatrix();
	}
	public void drawNodeEnergy(TileEntity tileentity)  {
		TileEntityStaticConduit conduit = (TileEntityStaticConduit) tileentity;
		float energyLevel = conduit.getEnergyAdjusted()*(4*pixel/2);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads(); 
		{		

			//Front
			tessellator.addVertexWithUV(.5-energyLevel, .5+energyLevel, .5+energyLevel, 1, 1);		
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5+energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5+energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5+energyLevel, .5+energyLevel, 0, 1);	
			
			//Right
			tessellator.addVertexWithUV(.5-energyLevel, .5+energyLevel, .5-energyLevel, 1, 1);		
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5+energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, .5+energyLevel, .5+energyLevel, 0, 1);	

			//Left
			tessellator.addVertexWithUV(.5+energyLevel, .5+energyLevel, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5+energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5-energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5+energyLevel, .5-energyLevel, 0, 1);		
			
			//Top
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5-energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5+energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5+energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5-energyLevel, 0, 1);		
			
			//Bottom
			tessellator.addVertexWithUV(.5+energyLevel, .5+energyLevel, .5+energyLevel, 1, 1);	
			tessellator.addVertexWithUV(.5+energyLevel, .5+energyLevel, .5-energyLevel, 1, 0);	
			tessellator.addVertexWithUV(.5-energyLevel, .5+energyLevel, .5-energyLevel, 0, 0);				
			tessellator.addVertexWithUV(.5-energyLevel, .5+energyLevel, .5+energyLevel, 0, 1);				

			
			//Back
			tessellator.addVertexWithUV(.5-energyLevel, .5-energyLevel, .5-energyLevel, 1, 1);
			tessellator.addVertexWithUV(.5-energyLevel, .5+energyLevel, .5-energyLevel, 1, 0);
			tessellator.addVertexWithUV(.5+energyLevel, .5+energyLevel, .5-energyLevel, 0, 0);	
			tessellator.addVertexWithUV(.5+energyLevel, .5-energyLevel, .5-energyLevel, 0, 1);	
			

		}
	tessellator.draw();
	}
	*/
}
