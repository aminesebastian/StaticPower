package api.gui;

import org.lwjgl.opengl.GL11;

import api.RectangleBounds;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.client.gui.widgets.GuiTabManager;
import theking530.staticpower.utils.StaticVertexBuffer;

public abstract class BaseGuiTab extends Gui {
	
	public enum TabState {
		CLOSED, OPENING, OPEN, CLOSING;
		
		static TabState incrementState(TabState curr) {
			int newIndex = curr.ordinal() + 1;
			newIndex = newIndex % 3;
			return TabState.values()[newIndex];
		}
	}
	
	protected int TAB_WIDTH;
	protected int TAB_HEIGHT;
	protected int TAB_XPOS;
	protected int TAB_YPOS;

	protected float TAB_ANIMATION = 0;
	protected float TAB_ANIMATION_SPEED = 5.0f;
	protected Item ITEM;
	protected ResourceLocation TAB_TEXTURE;
	protected TabState TAB_STATE;
	
	private GuiTabManager TAB_MANAGER;
	
	public BaseGuiTab(int tabWidth, int tabHeight, ResourceLocation texture, Item item) {
		TAB_WIDTH = tabWidth;
		TAB_HEIGHT = tabHeight;
		ITEM = item;		
		TAB_TEXTURE = texture;
		TAB_STATE = TabState.CLOSED;
	}
	public BaseGuiTab(int tabWidth, int tabHeight, ResourceLocation texture, Block block) {
		this(tabWidth, tabHeight, texture, Item.getItemFromBlock(block));
	}
	
	public void update(int xPos, int yPos, float partialTicks) {
		updateAnimation(partialTicks);
		
		TAB_XPOS = xPos;
		TAB_YPOS = yPos;
		

		drawTab(xPos, yPos, partialTicks);
		drawExtra(xPos, yPos, partialTicks);	
	}
	public void mouseInteraction(int mouseX, int mouseY, int button) {
		if(mouseX >  TAB_XPOS && mouseX <  TAB_XPOS + 24) {
	    	if(mouseY >  TAB_YPOS && mouseY <  TAB_YPOS + 24) {
	    		if(TAB_STATE == TabState.CLOSED) {
	    			TAB_STATE = TabState.OPENING;
		    		TAB_MANAGER.tabOpening(this);
	    		}
	    		if(TAB_STATE == TabState.OPEN) {
	    			TAB_STATE = TabState.CLOSING;
	    			TAB_MANAGER.tabClosing(this);
	    		}
	    	}
	    }
		if(isOpen()) {
			handleExtraMouseInteraction(mouseX, mouseY, button);
		}
	}
	public void keyboardInteraction(char par1, int par2) {
		if(isOpen()) {
			handleExtraKeyboardInteraction(par1, par2);
		}
	}
	public void mouseClickMoveIntraction(int x, int y, int button, long time) {
		if(isOpen()) {
			handleExtraClickMouseMove(x, y, button, time);
		}
	}
	public boolean isOpen() {
		return TAB_STATE == TabState.OPEN;
	}
	public boolean isClosed() {
		return TAB_STATE == TabState.CLOSED;
	}
	public TabState getTabState() {
		return TAB_STATE;
	}
	public boolean setTabState(TabState newState) {
		if(newState == TabState.CLOSED || newState == TabState.CLOSING) {
			if(getTabState() == TabState.OPEN) {
				TAB_STATE = TabState.CLOSING;
				return true;
			}
		}else if(newState == TabState.OPEN || newState == TabState.OPENING) {
			if(getTabState() == TabState.CLOSED) {
				TAB_STATE = TabState.OPENING;
				return true;
			}
		}
		return false;
	}
	public void setManager(GuiTabManager manager) {
		TAB_MANAGER = manager;
	}
	
	protected abstract void drawExtra(int xPos, int yPos, float partialTicks);
	protected abstract void handleExtraMouseInteraction(int mouseX, int mouseY, int button);
	protected abstract void handleExtraKeyboardInteraction(char par1, int par2);
	protected abstract void handleExtraClickMouseMove(int mouseX, int mouseY, int button, long time);
	
	private void updateAnimation(float partialTicks) {
		if(TAB_STATE == TabState.OPENING && TAB_ANIMATION < TAB_ANIMATION_SPEED) {
			TAB_ANIMATION = Math.min(TAB_ANIMATION_SPEED, TAB_ANIMATION + partialTicks*2);
			if(TAB_ANIMATION == TAB_ANIMATION_SPEED) {
				TAB_STATE = TabState.OPEN;
			}
		}
		if(TAB_STATE == TabState.CLOSING && TAB_ANIMATION > 0) {
			TAB_ANIMATION = Math.max(0, TAB_ANIMATION - partialTicks*2);
			if(TAB_ANIMATION == 0) {
				TAB_STATE = TabState.CLOSED;
			}
		}
		if(TAB_STATE == TabState.CLOSING && TAB_ANIMATION <=  0) {
			TAB_STATE = TabState.CLOSED;
    		TAB_MANAGER.tabClosed(this);
		}
		if(TAB_STATE == TabState.OPENING && TAB_ANIMATION >= TAB_ANIMATION_SPEED) {
			TAB_STATE = TabState.OPEN;
    		TAB_MANAGER.tabOpened(this);
		}
	}
	private void drawTab(int xPos, int yPos, float partialTicks) {
		drawBaseTab(xPos, yPos, partialTicks);
		drawButtonIcon(xPos, yPos, partialTicks);
	}
	private void drawButtonIcon(int xPos, int yPos, float partialTicks) {
		if(ITEM != null) {
	        GlStateManager.disableDepth();
	        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(ITEM), xPos+3, yPos+4);
		}
	}
	private void drawBaseTab(int xPos, int yPos, float partialTicks) {
		int tabLeft = xPos;
		int tabTop = yPos;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
		

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
		
	}
	
	public RectangleBounds getBounds() {
		return new RectangleBounds(TAB_XPOS, TAB_YPOS, TAB_WIDTH, TAB_HEIGHT);
	}
}

