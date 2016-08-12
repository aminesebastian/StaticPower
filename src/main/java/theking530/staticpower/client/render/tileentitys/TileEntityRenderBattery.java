package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.client.model.ModelBlock;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBattery;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityRenderBattery extends TileEntitySpecialRenderer {

	ResourceLocation top;
	ResourceLocation topIn;
	ResourceLocation topOut;
	ResourceLocation topDis;
	
	ResourceLocation side;
	ResourceLocation sideDis;
	ResourceLocation sideIn;
	ResourceLocation sideOut;
	
	ResourceLocation eBar = new ResourceLocation(Reference.MODID, "textures/blocks/BatteryEnergyBar.png");
	
	private Tier TIER;
	
	public TileEntityRenderBattery(Tier tier) {
		TIER = tier;	
		switch(tier) {
    	case STATIC:top = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTop.png");
    				topIn = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTopIn.png");
    				topOut = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTopOut.png");
    				topDis = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTopDis.png");
    				side = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySides.png");
    				sideDis = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySidesDis.png");
    				sideIn = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySidesIn.png");
    				sideOut = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySidesOut.png");	
    		break;
    	case ENERGIZED:top = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatteryTop.png");
					topIn = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatteryTopIn.png");
					topOut = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatteryTopOut.png");
					topDis = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatteryTopDis.png");
					side = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatterySides.png");
					sideDis = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatterySidesDis.png");
					sideIn = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatterySidesIn.png");
					sideOut = new ResourceLocation(Reference.MODID, "textures/blocks/EnergizedBatterySidesOut.png");	
			break;
    	case LUMUM:top = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatteryTop.png");
					topIn = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatteryTopIn.png");
					topOut = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatteryTopOut.png");
					topDis = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatteryTopDis.png");
					side = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatterySides.png");
					sideDis = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatterySidesDis.png");
					sideIn = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatterySidesIn.png");
					sideOut = new ResourceLocation(Reference.MODID, "textures/blocks/LumumBatterySidesOut.png");	
			break;
    	default:top = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTop.png");
				topIn = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTopIn.png");
				topOut = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTopOut.png");
				topDis = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatteryTopDis.png");
				side = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySides.png");
				sideDis = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySidesDis.png");
				sideIn = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySidesIn.png");
				sideOut = new ResourceLocation(Reference.MODID, "textures/blocks/StaticBatterySidesOut.png");	
				break;
    	}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f, int dest) {
		TileEntityBattery battery = (TileEntityBattery)tileentity;			

		int i = tileentity.getBlockMetadata();	
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		if(i == 2) {
			GL11.glRotated(180, 0, 1, 0);
			GL11.glTranslated(-1, 0, -1);
			}
		if(i == 4) {
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(0, 0, -1);
			}
		if(i == 5) {
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-1, 0, 0);
			}
		GL11.glDisable(GL11.GL_LIGHTING);			
		checkAndRenderSides(tileentity, 0);
		checkAndRenderSides(tileentity, 1);
		checkAndRenderSides(tileentity, 2);
		checkAndRenderSides(tileentity, 3);
		checkAndRenderSides(tileentity, 4);
		checkAndRenderSides(tileentity, 5);
		drawEnergy(tileentity);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(-x, -y, -z);	
		GL11.glColor3f(1.0F, 1.0F, 1.0F);	
		GL11.glPopMatrix();
	}
	public void checkAndRenderSides(TileEntity tileentity, int side) {
		TileEntityBattery battery = (TileEntityBattery)tileentity;		
		ModelBlock block = new ModelBlock();
		if(side <= 1) {
			if(battery.getModeFromInt(side) == Mode.Regular) {
				this.bindTexture(top);
			}
			if(battery.getModeFromInt(side) == Mode.Input) {
				this.bindTexture(topIn);
			}
			if(battery.getModeFromInt(side) == Mode.Output) {
				this.bindTexture(topOut);
			}
			if(battery.getModeFromInt(side) == Mode.Disabled) {
				this.bindTexture(topDis);
			}
			block.drawBlock(side);
		}
		if(side >= 2) {
			if(battery.getModeFromInt(side) == Mode.Regular) {
				this.bindTexture(this.side);
			}
			if(battery.getModeFromInt(side) == Mode.Input) {
				this.bindTexture(sideIn);
			}
			if(battery.getModeFromInt(side) == Mode.Output) {
				this.bindTexture(sideOut);
			}
			if(battery.getModeFromInt(side) == Mode.Disabled) {
				this.bindTexture(sideDis);
			}
			block.drawBlock(side);
		}
	}
	public void drawEnergy(TileEntity tileentity) {		
		TileEntityBattery battery = (TileEntityBattery)tileentity;			
		float height = (battery.getEnergyLevelScaled(1) +.39F)*.72F;
		this.bindTexture(eBar);
		GL11.glPushMatrix();
			
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertex = tessellator.getBuffer();
		vertex.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		//Back
		vertex.pos(.25, .218, -0.001).tex(0, 0).endVertex();
		vertex.pos(.25, height*.78125, -0.001).tex(0, height).endVertex();
		vertex.pos(.75, height*.78125, -0.001).tex(1, height).endVertex();	
		vertex.pos(.75, .218, -0.001).tex(1, 0).endVertex();		

		//Left
		vertex.pos(1.001, .218, .25).tex(0, 0).endVertex();
		vertex.pos(1.001, height*.78125, .25).tex(0, height).endVertex();
		vertex.pos(1.001, height*.78125, .75).tex(1, height).endVertex();	
		vertex.pos(1.001, .218, .75).tex(1, 0).endVertex();

		//Right
		vertex.pos(-0.001, .218, .75).tex(0, 0).endVertex();
		vertex.pos(-0.001, height*.78125, .75).tex(0, height).endVertex();	
		vertex.pos(-0.001, height*.78125, .25).tex(1, height).endVertex();	
		vertex.pos(-0.001, .218, .25).tex(1, 0).endVertex();	

		//Front
		vertex.pos(.25, height*.78125, 1.001).tex(0, 0).endVertex();
		vertex.pos(.25, .218, 1.001).tex(0, height).endVertex();	
		vertex.pos(.75, .218, 1.001).tex(1, height).endVertex();	
		vertex.pos(.75, height*.78125, 1.001).tex(1, 0).endVertex();			

		tessellator.draw();		
		GL11.glPopMatrix();
	}
}

