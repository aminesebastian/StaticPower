package api.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.utils.StaticVertexBuffer;

public class TabRightBlock extends Gui {
	
	public int TAB_WIDTH;
	public int TAB_HEIGHT;
	public int TAB_XPOS;
	public int TAB_YPOS;
	private int GUI_LEFT;
	private int GUI_TOP;
	public float TAB_ANIMATION = 0;
	public float TAB_ANIMATION_SPEED = 5;
	private Block BLOCK;
	private ResourceLocation texture;
	public int GROWTH_STATE;
	public int WIDTH;
	public int HEIGHT;
	public int xSIZE;
	public int ySIZE;
	public boolean IS_OPEN = false;
	
	public TabRightBlock(int guiLeft, int guiTop, int width, int height, int xPos, int yPos, ResourceLocation texture) {
		this.GUI_LEFT = guiLeft;
		this.GUI_TOP = guiTop;
		this.TAB_WIDTH = width;
		this.TAB_HEIGHT = height;
		this.TAB_XPOS = xPos;
		this.TAB_YPOS = yPos;
		this.texture = texture;
	}
	public void updateMethod(int width, int height, int xSize, int ySize, Block block) {
		extendTab();
		setState();
		this.WIDTH = width;
		this.HEIGHT = height;
		this.xSIZE = xSize;
		this.ySIZE = ySize;
		this.BLOCK = block;		
	}
	public void drawTab() {
		drawBaseTab();
		if(BLOCK != null) {
			drawButtonIcon();
		}
	}
	public void drawButtonIcon() {
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + TAB_XPOS;
		int tabTop = GUI_TOP + k + TAB_YPOS;
		ItemStack item = new ItemStack(Item.getItemFromBlock(BLOCK));
		
	}
	public void drawBaseTab() {
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + TAB_XPOS;
		int tabTop = GUI_TOP + k + TAB_YPOS;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GL11.glPushMatrix();
		//Top
		StaticVertexBuffer.pos(tabLeft+20+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+3, 0, .976, .03);
		StaticVertexBuffer.pos(tabLeft+20+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop, 0, .976, 0);
		StaticVertexBuffer.pos(tabLeft+1, tabTop, 0, 0, 0);
		StaticVertexBuffer.pos(tabLeft+1, tabTop+3, 0, 0, .03);
		
		//Bottom
		StaticVertexBuffer.pos(tabLeft+21+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+24+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, .976, 1);
		StaticVertexBuffer.pos(tabLeft+21+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+21+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, .976, .965);
		StaticVertexBuffer.pos(tabLeft+1, tabTop+21+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, 0, .965);
		StaticVertexBuffer.pos(tabLeft+1, tabTop+24+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, 0, 1);
		
		//Right Side
		StaticVertexBuffer.pos(tabLeft+23.8+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+22+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, 1, 0.035);
		StaticVertexBuffer.pos(tabLeft+23.8+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+4, 0, 1, .95);
		StaticVertexBuffer.pos(tabLeft+21+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+4, 0, .976, .95);
		StaticVertexBuffer.pos(tabLeft+21+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+22+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, .976, 0.035);
		
		//Body
		StaticVertexBuffer.pos(tabLeft+21+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+21+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, .976, 0.035);
		StaticVertexBuffer.pos(tabLeft+21+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+3, 0, .976, .965);
		StaticVertexBuffer.pos(tabLeft+1, tabTop+3, 0, 0, .965);
		StaticVertexBuffer.pos(tabLeft+1, tabTop+21+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, 0, 0.035);
		
		//Bottom Corner
		StaticVertexBuffer.pos(tabLeft+24.1+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+23+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, 1, 1);
		StaticVertexBuffer.pos(tabLeft+24.1+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+20+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, 1, .965);
		StaticVertexBuffer.pos(tabLeft+20+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+20+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, .976, .965);
		StaticVertexBuffer.pos(tabLeft+20+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+23+(TAB_HEIGHT*TAB_ANIMATION/TAB_ANIMATION_SPEED), 0, .976, 1);
		
		//Top Corner
		StaticVertexBuffer.pos(tabLeft+24+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+4, 0, 1, .03);
		StaticVertexBuffer.pos(tabLeft+24+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop, 0, 1, 0);
		StaticVertexBuffer.pos(tabLeft+20+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop, 0, .976, 0);
		StaticVertexBuffer.pos(tabLeft+20+(TAB_WIDTH*TAB_ANIMATION/TAB_ANIMATION_SPEED), tabTop+4, 0, .9767, .03);

		tessellator.draw();
		GL11.glPopMatrix();
	}
	public void extendTab() {
		if(GROWTH_STATE == 1 && TAB_ANIMATION < TAB_ANIMATION_SPEED) {
			TAB_ANIMATION++;
			if(TAB_ANIMATION == TAB_ANIMATION_SPEED) {
				GROWTH_STATE = 1;
			}
		}
		if(GROWTH_STATE == 2 && TAB_ANIMATION > 0) {
			TAB_ANIMATION--;
			if(TAB_ANIMATION == 0) {
				GROWTH_STATE = 0;
			}
		}
		if(GROWTH_STATE == 2 && TAB_ANIMATION == 0) {
			GROWTH_STATE = 0;
		}
		if(GROWTH_STATE > 2) {
			GROWTH_STATE = 2;
		}
	}
	public void tabMouseExtension(int par1, int par2, int button) {
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		if(par1 > j + TAB_XPOS && par1 < j + TAB_XPOS + 24) {
	    	if(par2 > k + TAB_YPOS && par2 < k + TAB_YPOS + 24) {
	    		GROWTH_STATE += 1; 
	    	}
	    		if(GROWTH_STATE > 2){
	    			GROWTH_STATE = 0;
	    	}
	    }
	}
	public void setState() {
		if(TAB_ANIMATION == TAB_ANIMATION_SPEED) {
			IS_OPEN = true;
		}else{
			IS_OPEN = false;
		}
	}
}

