package theking530.staticpower.client.gui.widgets.tabs;

import java.util.List;

import org.lwjgl.opengl.GL11;

import api.gui.TabRightItem;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.OldSidePicker.Side;

public class GuiInfoTab extends GuiScreen {
	
	public int GUI_LEFT;
	public int GUI_TOP;
	public int GROWTH_STATE;
	private float MOUSE_X;
	private float MOUSE_Y;
	private float MOUSE_DRAGX;
	private float MOUSE_DRAGY;
	private Side SIDE;
	public int BUTTON;
	public World WORLD;
	public BaseTileEntity TE_INV;
	public EntityPlayer PLAYER;
	public static boolean IS_TAB_OPEN;
	public TileEntity TILE_ENTITY;
	private FontRenderer FONT_RENDERER;

	
	public TabRightItem GREEN_TAB = new TabRightItem(GUI_LEFT, GUI_TOP, 115, 80, 175, 8, GuiTextures.GREEN_TAB, Items.PAPER);
	
	public GuiInfoTab(int guiLeft, int guiTop){
		this.GUI_LEFT = guiLeft;
		this.GUI_TOP = guiTop;
	}
	public void drawTab(List list) {
		int k = 0;
		GREEN_TAB.drawTab();
		if(IS_TAB_OPEN) {	
			drawButtonBG();	
    	    drawText(list);
    	}	
	}
	public void drawText(List list) {
		Block block = TILE_ENTITY.getBlockType();
		String tabName = EnumTextFormatting.UNDERLINE + block.getLocalizedName();
		String tabNameColored =  EnumTextFormatting.WHITE + tabName;
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		int j = (GREEN_TAB.WIDTH - GREEN_TAB.xSIZE) / 2;
		int k = (GREEN_TAB.HEIGHT - GREEN_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + GREEN_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
		GL11.glDisable(GL11.GL_LIGHTING);
		modeText(tabLeft, tabTop);	
		this.FONT_RENDERER.drawStringWithShadow(tabNameColored, tabLeft-this.FONT_RENDERER.getStringWidth(tabNameColored)/2 + 70, tabTop+15, 16777215);	
		for(int i = 0; i < list.size(); i++) {
			String string = EnumTextFormatting.ITALIC + (String) list.get(i);
    		this.FONT_RENDERER.drawStringWithShadow(string, tabLeft + 18, (tabTop+32)+11*i, 16777215);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		
	}
	public void modeText(int tabLeft, int tabTop) {
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
	}
	public void drawButtonBG() {
		int j = (GREEN_TAB.WIDTH - GREEN_TAB.xSIZE) / 2;
		int k = (GREEN_TAB.HEIGHT - GREEN_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + GREEN_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
    	GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        tes.color(.5F, .5F, .5F, 1F);
        tes.pos(tabLeft+126, tabTop+100, 0).endVertex();
        tes.pos(tabLeft+126, tabTop+30, 0).endVertex();
        tes.pos(tabLeft+11, tabTop+30, 0).endVertex();
        tes.pos(tabLeft+11, tabTop+100, 0).endVertex();	
		tessellator.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
	public void updateTab(int width, int height, int xSize, int ySize, FontRenderer fontRenderer, TileEntity te) {
		GREEN_TAB.updateMethod(width, height, xSize, ySize);
		setTabOpen();
		setGrowthState();
		this.FONT_RENDERER = fontRenderer;
		this.TILE_ENTITY = te;
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		this.TE_INV = entity;
	}
    
    public void mouseInteraction(int par1, int par2, int button) {
		GREEN_TAB.tabMouseExtension(par1, par2, button);	
		MOUSE_X = par1;
		MOUSE_Y = par2;
		BUTTON = button;
	}	
	public void setTabOpen() {
		if(GREEN_TAB.TAB_ANIMATION == GREEN_TAB.TAB_ANIMATION_SPEED) {
			IS_TAB_OPEN = true;
		}else{
			IS_TAB_OPEN = false;
		}
	}
	public void setGrowthState() {
		int state = GREEN_TAB.GROWTH_STATE ;
			GROWTH_STATE = state;
	}
}
