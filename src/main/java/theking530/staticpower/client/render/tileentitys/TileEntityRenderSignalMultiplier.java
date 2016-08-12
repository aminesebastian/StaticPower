package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.model.ModelSignalMultiplier;
import theking530.staticpower.machines.signalmultiplier.TileEntitySignalMultiplier;

public class TileEntityRenderSignalMultiplier extends TileEntitySpecialRenderer {

    private final ModelSignalMultiplier model;
    ResourceLocation textureON = new ResourceLocation(Reference.MODID, "textures/models/misc/MultiplierON.png");
    ResourceLocation textureOFF = new ResourceLocation(Reference.MODID, "textures/models/misc/MultiplierOFF.png");
    
    public TileEntityRenderSignalMultiplier() {
            this.model = new ModelSignalMultiplier();
    }
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f, int dest) {
		TileEntitySignalMultiplier multiplier = (TileEntitySignalMultiplier)tileentity;
		 GL11.glPushMatrix();
		 GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);		
		 GL11.glPushMatrix();
		 GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		 if(multiplier.getInputSignal() == multiplier.INPUT_SIGNAL_LIMIT) { 
			 bindTexture(textureON);
			 model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		 }else{
			 bindTexture(textureOFF);
			 model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		 }
		 GL11.glPopMatrix();
		 GL11.glPopMatrix();
	}
}



