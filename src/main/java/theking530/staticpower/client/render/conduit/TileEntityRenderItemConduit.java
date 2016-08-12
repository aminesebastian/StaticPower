package theking530.staticpower.client.render.conduit;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.conduits.itemconduit.TileEntityItemConduit;

public class TileEntityRenderItemConduit extends TileEntityRenderBaseConduit {
	
	ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/models/conduits/ItemConduit.png");
	ResourceLocation pullTexture = new ResourceLocation(Reference.MODID, "textures/models/conduits/ItemConduitPull.png");
	boolean drawInside = false;
	
	float pixel = 1F/16F;
	float texel = 1F/64F;
	static float width = .1F;
	private float itemPosition = 0.0f;

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double translationX, double translationY, double translationZ, float f, int dest) {
		TileEntityItemConduit conduit = (TileEntityItemConduit)tileentity;	
		
		GL11.glTranslated(translationX, translationY, translationZ);
		//renderItemInConduit(conduit, translationX, translationY, translationZ);
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
				}		
			}
		}
		if (!conduit.straightConnection(conduit.connections)) {
			this.bindTexture(texture);
			drawNode(tileentity); 			
			for(int i = 0; i < conduit.connections.length; i++) {
				if(conduit.connections[i] != null) {
					this.bindTexture(texture);
					drawCore(conduit.connections[i]);
				}
			}
		} else {
			for(int i = 0; i < conduit.connections.length; i++) 
				if(conduit.connections[i] != null) {	
					this.bindTexture(texture);
					drawStraight(conduit.connections[i]);
					break;
				}
			}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(-translationX, -translationY, -translationZ);	
	}
	
	/**
	public void renderItemInConduit(TileEntityItemConduit conduit, double x, double y, double z) {
		EntityItem entItem = null;
		if(conduit.slot != null && conduit.slot.ITEM != null) {
			ItemStack tempStack = conduit.slot.ITEM.copy();
			tempStack.stackSize = 1;
			entItem = new EntityItem(conduit.getWorld(), x, y, z,  tempStack);
		}
		if(entItem != null && conduit.slot.hasValidInventory()) {
			EnumFacing direction = conduit.slot.getNextDirection();
			GL11.glPushMatrix();
			entItem.hoverStart = 0.0F;
			RenderItem.renderInFrame = true;	
			GL11.glScalef(0.6f, 0.6f, 0.6f);
			if(direction != null) {
				if (direction.equals(EnumFacing.UP))  {
					GL11.glTranslatef(0.88f, 0.7f + itemPosition, 0.88f);
				}else if(direction.equals(EnumFacing.DOWN)) {
					GL11.glTranslatef(0.88f, 0.7f - itemPosition, 0.88f);
				}else if (direction.equals(EnumFacing.SOUTH)) {
					GL11.glTranslatef(0.88f, 0.7f, 0.88f + itemPosition);
				}else if(direction.equals(EnumFacing.NORTH)) {
					GL11.glTranslatef(0.88f, 0.7f, 0.88f - itemPosition);
				}else if (direction.equals(EnumFacing.WEST)) {
					GL11.glTranslatef(0.88f - itemPosition, 0.7f, 0.88f);
				}else if(direction.equals(EnumFacing.EAST)) {
					GL11.glTranslatef(0.88f + itemPosition, 0.7f, 0.88f);
				}
			}
			RenderManager.instance.renderEntityWithPosYaw(entItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
			RenderItem.renderInFrame = false;
			GL11.glPopMatrix();
		}
	}
	*/
}
