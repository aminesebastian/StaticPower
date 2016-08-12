package theking530.staticpower.client.gui.widgets;

import java.util.List;

import org.lwjgl.opengl.GL11;

import api.gui.TabRightBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.BaseTileEntity;
import theking530.staticpower.utils.SideModeList.Mode;
import theking530.staticpower.utils.SidePicker;
import theking530.staticpower.utils.SidePicker.HitCoord;
import theking530.staticpower.utils.SidePicker.Side;

public class GuiSideConfigTab extends GuiScreen {
	
	public int GUI_LEFT;
	public int GUI_TOP;
	public int GROWTH_STATE;
	private float MOUSE_X;
	private float MOUSE_Y;
	private float MOUSE_DRAGX;
	private float MOUSE_DRAGY;
	private Side SIDE;
	public int BUTTON;
	private Block BLOCK;
	public World WORLD;
	public BaseTileEntity TE_INV;
	public EntityPlayer PLAYER;
	public static boolean IS_TAB_OPEN;
	public TileEntity TILE_ENTITY;
	private FontRenderer FONT_RENDERER;
	private ResourceLocation blueTab = new ResourceLocation(Reference.MODID + ":" + "textures/gui/BlueTab.png");
	private ResourceLocation bg = new ResourceLocation(Reference.MODID + ":" + "textures/gui/ButtonBG.png");
	
	public TabRightBlock BLUE_TAB = new TabRightBlock(GUI_LEFT, GUI_TOP, 80, 75, 175, 64, blueTab);
	
	public GuiSideConfigTab(int guiLeft, int guiTop, Block block){
		GUI_LEFT = guiLeft;
		GUI_TOP = guiTop;
		BLOCK = block;
		FONT_RENDERER = Minecraft.getMinecraft().fontRendererObj;
	}
	public void drawTab() {
		BLUE_TAB.drawTab();
		if(IS_TAB_OPEN) {	
    		if(MOUSE_X > 0 && BUTTON == 1) {
    			//rotationDecider(SIDE);
    			MOUSE_X = 0;
    		}	
    	drawText();
		drawButtonBG();	
		drawBlock();
		}
	}
	public void drawText() {
		String tabName = "Side Config";
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		int j = (BLUE_TAB.WIDTH - BLUE_TAB.xSIZE) / 2;
		int k = (BLUE_TAB.HEIGHT - BLUE_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + BLUE_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
		modeText(tabLeft, tabTop);	
		FONT_RENDERER.drawStringWithShadow(tabName, tabLeft-this.FONT_RENDERER.getStringWidth(tabName)/2 + 52, tabTop+72, 16777215);
		
	}
	public void modeText(int tabLeft, int tabTop) {
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
	}
	public void drawButtonBG() {
		int j = (BLUE_TAB.WIDTH - BLUE_TAB.xSIZE) / 2;
		int k = (BLUE_TAB.HEIGHT - BLUE_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + BLUE_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;

		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(bg);
		GlStateManager.color(1, 1, 1);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(tabLeft+93, tabTop+155, 0).tex(0,1).endVertex();
		vertexbuffer.pos(tabLeft+93, tabTop+86, 0).tex(0,0).endVertex();
		vertexbuffer.pos(tabLeft+10, tabTop+86, 0).tex(1,0).endVertex();
		vertexbuffer.pos(tabLeft+10, tabTop+155, 0).tex(1,1).endVertex();	
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
	/**
	public void functionMetaIgnored(Side side) {
		if(TILE_ENTITY != null) {
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;

		if(side != null) {
            switch (side) {
					case XPos:
						entity.incrementSide(5);
						if(isShiftKeyDown()) {
							resetSides();
						}
						break;
						
					case YPos:					
						entity.incrementSide(1);
						if(isShiftKeyDown()) {
							resetSides();
						}
						break;
						
					case ZPos:
						entity.incrementSide(3);
						if(isShiftKeyDown()) {
							resetSides();
						}
						break;
						
					case XNeg:		
						entity.incrementSide(4);
						if(isShiftKeyDown()) {
							resetSides();
						}
						break;
						
					case YNeg:
						entity.incrementSide(0);
						if(isShiftKeyDown()) {
							resetSides();
						}
						break;
						
					case ZNeg:
						entity.incrementSide(2);
						if(isShiftKeyDown()) {
							resetSides();
						}
						break;
						
					default:
						break;
            	}
            }  
		//TILE_ENTITY.getWorld().markBlockForUpdate(TILE_ENTITY.xCoord, TILE_ENTITY.yCoord+1, TILE_ENTITY.zCoord);
		IMessage msg = new PacketSideConfigTab(entity.SIDE_MODES, entity.getPos());
		PacketHandler.net.sendToServer(msg);
		}
	}
	public void functionMeta2(Side side) {
		if(TILE_ENTITY != null) {
			BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		int x = TILE_ENTITY.xCoord;
		int y = TILE_ENTITY.yCoord;
		int z = TILE_ENTITY.zCoord;
		if(side != null) {
            switch (side) {
					case XPos:
						entity.SIDE4++;
						if(entity.SIDE4 > 3) {
							entity.SIDE4 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						TILE_ENTITY.getWorld().markBlockForUpdate(TILE_ENTITY.xCoord, TILE_ENTITY.yCoord+1, TILE_ENTITY.zCoord);
						IMessage msg = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg);
						break;
						
					case YPos:					
						entity.SIDE1++;
						if(entity.SIDE1 > 3) {
							entity.SIDE1 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg1 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg1);
						break;
						
					case ZPos:
						entity.SIDE2++;
						if(entity.SIDE2 > 3) {
							entity.SIDE2 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg2 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg2);
						break;
						
					case XNeg:		
						entity.SIDE5++;
						if(entity.SIDE5 > 3) {
							entity.SIDE5 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg3 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg3);
						break;
						
					case YNeg:
						entity.SIDE0++;
						if(entity.SIDE0 > 3) {
							entity.SIDE0 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg4 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg4);
						break;
						
					case ZNeg:
						break;
						
					default:
						break;
            	}
            }               
		}
	}
	public void functionMeta4(Side side) {
		if(TILE_ENTITY != null) {
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		int x = TILE_ENTITY.xCoord;
		int y = TILE_ENTITY.yCoord;
		int z = TILE_ENTITY.zCoord;
		if(side != null) {
            switch (side) {
					case XPos:
						entity.SIDE2++;
						if(entity.SIDE2 > 3) {
							entity.SIDE2 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						TILE_ENTITY.getWorldObj().markBlockForUpdate(TILE_ENTITY.xCoord, TILE_ENTITY.yCoord+1, TILE_ENTITY.zCoord);
						IMessage msg = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg);
						break;
						
					case YPos:					
						entity.SIDE1++;
						if(entity.SIDE1 > 3) {
							entity.SIDE1 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg1 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg1);
						break;
						
					case ZPos:
						entity.SIDE5++;
						if(entity.SIDE5 > 3) {
							entity.SIDE5 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg2 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg2);
						break;
						
					case XNeg:		
						break;
						
					case YNeg:
						entity.SIDE0++;
						if(entity.SIDE0 > 3) {
							entity.SIDE0 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg4 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg4);
						break;
						
					case ZNeg:
						entity.SIDE4++;
						if(entity.SIDE4 > 3) {
							entity.SIDE4 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg5 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg5);
						break;
						
					default:
						break;
            	}
            }               
		}
	}
	public void functionMeta5(Side side) {
		if(TILE_ENTITY != null) {
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		int x = TILE_ENTITY.xCoord;
		int y = TILE_ENTITY.yCoord;
		int z = TILE_ENTITY.zCoord;
		if(side != null) {
            switch (side) {
					case XPos:
						break;
						
					case YPos:					
						entity.SIDE1++;
						if(entity.SIDE1 > 3) {
							entity.SIDE1 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg1 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg1);
						break;
						
					case ZPos:
						entity.SIDE4++;
						if(entity.SIDE4 > 3) {
							entity.SIDE4 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg2 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg2);
						break;
						
					case XNeg:		
						entity.SIDE2++;
						if(entity.SIDE2 > 3) {
							entity.SIDE2 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg3 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg3);
						break;
						
					case YNeg:
						entity.SIDE0++;
						if(entity.SIDE0 > 3) {
							entity.SIDE0 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg4 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg4);
						break;
						
					case ZNeg:
						entity.SIDE5++;
						if(entity.SIDE5 > 3) {
							entity.SIDE5 = 0;
						}
						if(isShiftKeyDown()) {
							resetSides();
						}
						IMessage msg5 = new PacketSideConfigTab(entity.SIDE0, entity.SIDE1, entity.SIDE2, entity.SIDE3, entity.SIDE4, entity.SIDE5, x, y, z);
						PacketHandler.net.sendToServer(msg5);
						break;
						
					default:
						break;
            	}
            }               
		}
	}

	public void rotationDecider(Side side) {
		int meta = TILE_ENTITY.getWorld().getBlockMetadata(TILE_ENTITY.getpo);
		if(meta != 2 && meta != 4 && meta != 5) {
		functionMetaIgnored(side);
		}
		if(meta == 2) {
		functionMeta2(side);
		}
		if(meta == 4) {
		functionMeta4(side);
		}
		if(meta == 5) {
		functionMeta5(side);
		}
	}
	*/
	public void updateTab(int width, int height, int xSize, int ySize, FontRenderer fontRenderer, TileEntity te) {
		BLUE_TAB.updateMethod(width, height, xSize, ySize, BLOCK);
		setTabOpen();
		setGrowthState();
		this.FONT_RENDERER = fontRenderer;
		this.TILE_ENTITY = te;
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		this.TE_INV = entity;
	}

	public void drawBlock() {
		int j = (BLUE_TAB.WIDTH - BLUE_TAB.xSIZE) / 2;
		int k = (BLUE_TAB.HEIGHT - BLUE_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + BLUE_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
		renderBlock(new ItemStack(BLOCK), 1, tabLeft+74, tabTop+125, false);
    }

    public void renderBlock(ItemStack item, float zLevel, float x, float y, boolean highlight) {
    	TileEntity te;
    	float yaw = (Minecraft.getMinecraft().thePlayer.rotationYaw) - 85;
    	float pitch = (Minecraft.getMinecraft().thePlayer.rotationPitch);
    	float rotateX = yaw + (MOUSE_DRAGX*4.5f);
    	float rotateY = (MOUSE_DRAGY*-4.5f);
    	float identifierY = MathHelper.sin((float) (rotateX*0.5));
    	float identifierZ = MathHelper.cos((float) (rotateX*0.5));
    	
    	float multRotateY = (float) Math.pow(MathHelper.sin((float) (rotateX*0.1025)), 2); 
    	float multRotateZ = (float) Math.pow(MathHelper.cos((float) (rotateX*0.1025)), 2); 

        GL11.glPushMatrix();
		
        GL11.glTranslatef(x-4, y, 10.0F + zLevel);
        GL11.glScalef(38F, 38F, -10F);
        GL11.glRotatef(210F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(-0.5F, 0.4F, 0.5F);
        GL11.glRotatef(rotateX, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotateY, -multRotateY, 0.0F, 0.0F);
        GL11.glRotatef(rotateY, 0.0F, 0.0F, -multRotateZ);
        GL11.glTranslatef(0.5F, -0.4F, -0.5F);              
        GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
        TileEntityRendererDispatcher.instance.renderTileEntityAt(TILE_ENTITY, 0.0, -0.1, 0, 0.0F);
 
        SidePicker picker = new SidePicker(1);
        HitCoord coord = picker.getNearestHit();
	        if(coord != null) {	 
	            Mode mode = TE_INV.getSideModeFromSide(coord.side);
	            if(mode != null) {
	            	switch(mode) {
	            	case Regular:
	            		drawHighlight(16777215, coord.side);
	            		break;
	            	case Input:
	            		drawHighlight(3394815, coord.side);
	            		break;
	            	case Output:
	            		drawHighlight(16750899, coord.side);
	            		break;
	            	case Disabled:
	            		drawHighlight(10098969, coord.side);
	            		break;
	            		default:
	            			break;
	            	}
	            }
            this.SIDE = coord.side;
	    }	        
        GL11.glPopMatrix(); 
    }

    public void drawHighlight(int color, Side side) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    	GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glScalef(1.1F, 1.1F, 1.1F);
		GL11.glTranslatef(0.45F, 0.36F, 0.45F);
        //tes.setColorRGBA_I(color, 80);
		switch (side) {
			case XPos:
				tes.pos(0.5, -0.5, -0.5).endVertex();
				tes.pos(0.5, 0.5, -0.5).endVertex();
				tes.pos(0.5, 0.5, 0.5).endVertex();
				tes.pos(0.5, -0.5, 0.5).endVertex();
				break;
			case YPos:
				tes.pos(-0.5, 0.5, -0.5).endVertex();
				tes.pos(-0.5, 0.5, 0.5).endVertex();
				tes.pos(0.5, 0.5, 0.5).endVertex();
				tes.pos(0.5, 0.5, -0.5).endVertex();
				break;
			case ZPos:
				tes.pos(-0.5, -0.5, 0.5).endVertex();
				tes.pos(0.5, -0.5, 0.5).endVertex();
				tes.pos(0.5, 0.5, 0.5).endVertex();
				tes.pos(-0.5, 0.5, 0.5).endVertex();
				break;
			case XNeg:
				tes.pos(-0.5, -0.5, -0.5).endVertex();
				tes.pos(-0.5, -0.5, 0.5).endVertex();
				tes.pos(-0.5, 0.5, 0.5).endVertex();
				tes.pos(-0.5, 0.5, -0.5).endVertex();
				break;
			case YNeg:
				tes.pos(-0.5, -0.5, -0.5).endVertex();
				tes.pos(0.5, -0.5, -0.5).endVertex();
				tes.pos(0.5, -0.5, 0.5).endVertex();
				tes.pos(-0.5, -0.5, 0.5).endVertex();
				break;
			case ZNeg:
				tes.pos(-0.5, -0.5, -0.5).endVertex();
				tes.pos(-0.5, 0.5, -0.5).endVertex();
				tes.pos(0.5, 0.5, -0.5).endVertex();
				tes.pos(0.5, -0.5, -0.5).endVertex();
				break;
			default:
				break;
		}
		tessellator.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
    protected void drawHoveringText(List p_146283_1_, int p_146283_2_, int p_146283_3_, FontRenderer font) {
    }
    
    public void setTileInv() {
        if(TILE_ENTITY != null) {
    		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
    		TE_INV = entity;
        }	
    }
    public void mouseInteraction(int par1, int par2, int button) {
		BLUE_TAB.tabMouseExtension(par1, par2, button);	
		MOUSE_X = par1;
		MOUSE_Y = par2;
		BUTTON = button;
	}	
    public void mouseDrag(int xMouse, int yMouse, int button, long time) {
		int j = (BLUE_TAB.WIDTH - BLUE_TAB.xSIZE) / 2;
		int k = (BLUE_TAB.HEIGHT - BLUE_TAB.ySIZE) / 2;
		float yaw = (Minecraft.getMinecraft().thePlayer.rotationYaw);
    	if(xMouse > j + 188 && xMouse < j + 265) {
    		if(yMouse > k + 82 && yMouse < k + 155){
    			float x = -(MOUSE_X - xMouse);
    			float y = (MOUSE_Y - yMouse);
    			if(x > 0 && button == 0) {
    				MOUSE_DRAGX++;
    				MOUSE_X = xMouse;
    			}
    			if(x < 0 && button == 0) {
    				MOUSE_DRAGX--;
    				MOUSE_X = xMouse;
    			}
    			if(y > 0 && button == 0) {
    				MOUSE_DRAGY++;
    				MOUSE_Y = yMouse;
    			}
    			if(y < 0 && button == 0) {
    				MOUSE_DRAGY--;
    				MOUSE_Y = yMouse;
    			}
    			if(MOUSE_DRAGY > 360 || MOUSE_DRAGY < -360) {
    				MOUSE_DRAGY = 0;
    			}
    			if(MOUSE_DRAGX > 360 || MOUSE_DRAGX < -360) {
    				MOUSE_DRAGX = 0;
    			}			
    		}
    	}
    }
	public void setTabOpen() {
		if(BLUE_TAB.TAB_ANIMATION == BLUE_TAB.TAB_ANIMATION_SPEED) {
			IS_TAB_OPEN = true;
		}else{
			IS_TAB_OPEN = false;
		}
	}
	public void setGrowthState() {
		int state = BLUE_TAB.GROWTH_STATE ;
			GROWTH_STATE = state;
	}
}
