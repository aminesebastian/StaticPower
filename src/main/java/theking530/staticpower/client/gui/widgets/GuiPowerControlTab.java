package theking530.staticpower.client.gui.widgets;

import org.lwjgl.opengl.GL11;

import api.gui.ItemButton;
import api.gui.TabRightItem;
import api.gui.TextField;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.machines.BaseTileEntity;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.StaticVertexBuffer;

public class GuiPowerControlTab {
	
	public int GUI_LEFT;
	public int GUI_TOP;
	public int GROWTH_STATE;
	public World WORLD;
	public EntityPlayer PLAYER;
	public boolean IS_TAB_OPEN;
	public TileEntity TILE_ENTITY;
	private FontRenderer FONT_RENDERER;
	private ResourceLocation redTab = new ResourceLocation(Reference.MODID + ":" + "textures/gui/PurpleTab.png");
	private ResourceLocation bg = new ResourceLocation(Reference.MODID + ":" + "textures/gui/ButtonBG.png");
	private String ON_PERC = "";
	private String OFF_PERC = "100%";
	public TabRightItem YELLOW_TAB = new TabRightItem(GUI_LEFT, GUI_TOP, 100, 70, 175, 8, redTab, ModItems.StaticWrench);
	private int MIN_VALUE;
	private TextField MIN_PERCENTAGE = new TextField(GUI_LEFT + 230, GUI_TOP + 35, 30, 15);
	private int MAX_VALUE;
	private TextField MAX_PERCENTAGE = new TextField(GUI_LEFT + 230, GUI_TOP + 54, 30, 15);
	private ItemButton SET_PERCENTAGE = new ItemButton(GUI_LEFT, GUI_TOP, 50, 20, 210, 75, null);
	
	public GuiPowerControlTab(int guiLeft, int guiTop){
		this.GUI_LEFT = guiLeft;
		this.GUI_TOP = guiTop;
	}
	public void updateTab(int width, int height, int xSize, int ySize, FontRenderer fontRenderer, TileEntity te) {
		BaseMachine entity = (BaseMachine)TILE_ENTITY;
		setTabOpen();
		setGrowthState();
		this.FONT_RENDERER = fontRenderer;
		this.TILE_ENTITY = te;
		YELLOW_TAB.updateMethod(width, height, xSize, ySize);
		MAX_PERCENTAGE.updateMethod(FONT_RENDERER, width, height, xSize, ySize);
		MAX_PERCENTAGE.setMaxStringLength(3);		
		MIN_PERCENTAGE.updateMethod(FONT_RENDERER, width, height, xSize, ySize);
		SET_PERCENTAGE.updateMethod(width, height, xSize, ySize, te);
		MIN_PERCENTAGE.setMaxStringLength(3);
		if(YELLOW_TAB.TAB_ANIMATION == 7) {
			try {
				MAX_PERCENTAGE.setText(Integer.toString(entity.MAX_POWER_THRESHOLD));
			} catch(NumberFormatException e) {}	
			try {
				MIN_PERCENTAGE.setText(Integer.toString(entity.MIN_POWER_THRESHOLD));
			} catch(NumberFormatException e) {}	
		}
	}
	public void drawTab() {
		int j = (YELLOW_TAB.WIDTH - YELLOW_TAB.xSIZE) / 2;
		int k = (YELLOW_TAB.HEIGHT - YELLOW_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + YELLOW_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
		YELLOW_TAB.drawTab();
		if(IS_TAB_OPEN) {
		SET_PERCENTAGE.drawButton();
		MAX_PERCENTAGE.drawTextBox();
		MIN_PERCENTAGE.drawTextBox();
		function();
		drawText();
		}else{
			SET_PERCENTAGE.TIMER = 0;
			SET_PERCENTAGE.CLICKED = false;
		}		
	}
	public void fieldInit() {
	}
	public void drawText() {
		String tabName = "Power Control";
		int j = (YELLOW_TAB.WIDTH - YELLOW_TAB.xSIZE) / 2;
		int k = (YELLOW_TAB.HEIGHT - YELLOW_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + YELLOW_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
		modeText(tabLeft, tabTop);	
		this.FONT_RENDERER.drawStringWithShadow(tabName, tabLeft-this.FONT_RENDERER.getStringWidth(tabName)/2 + 60, tabTop+15, 16777215);	
	}
	public void modeText(int tabLeft, int tabTop) {
		String max = "Minimum:";
		String min = "Maximum:";
		String percent = "%";
		String buttonText = "Confirm";
		
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		this.FONT_RENDERER.drawString(max, tabLeft+10, tabTop+39, 16777215);				
		this.FONT_RENDERER.drawString(min, tabLeft+10, tabTop+58, 16777215);	
		this.FONT_RENDERER.drawString(EnumTextFormatting.BOLD + percent, tabLeft+90, tabTop+39, 16777215);	
		this.FONT_RENDERER.drawString(EnumTextFormatting.BOLD + percent, tabLeft+90, tabTop+58, 16777215);
		this.FONT_RENDERER.drawString(buttonText, tabLeft+40, tabTop+80, 16777215);
	}
	public void buttonText(int tabLeft, int tabTop){
	}
	public void drawButtonBG() {
	
		int j = (YELLOW_TAB.WIDTH - YELLOW_TAB.xSIZE) / 2;
		int k = (YELLOW_TAB.HEIGHT - YELLOW_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + YELLOW_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
    	GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        tes.color(.5F, .5F, .5F, 1F);    
		StaticVertexBuffer.pos(tabLeft+104, tabTop+90, 0, 0, 1);
		StaticVertexBuffer.pos(tabLeft+104, tabTop+30, 0, 0, 0);
		StaticVertexBuffer.pos(tabLeft+17, tabTop+30, 0, 1, 0);
		StaticVertexBuffer.pos(tabLeft+17, tabTop+90, 0, 1, 1);	
		tessellator.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
	public void function() {
		BaseMachine entity = (BaseMachine)TILE_ENTITY;
		
		if(MAX_PERCENTAGE.getText() != null) {			
			try { 
				MAX_VALUE = Integer.valueOf(MAX_PERCENTAGE.getText().replaceFirst(".*?(\\d+).*", "$1"));				
			} catch(NumberFormatException e) {}				
		}
		if(MIN_PERCENTAGE.getText() != null) {	
			try { 
				MIN_VALUE = Integer.valueOf(MIN_PERCENTAGE.getText().replaceFirst(".*?(\\d+).*", "$1"));			
			} catch(NumberFormatException e) {}				
		}
		if(SET_PERCENTAGE.CLICKED) {
			entity.MIN_POWER_THRESHOLD = MIN_VALUE;
			entity.MAX_POWER_THRESHOLD = MAX_VALUE;
		IMessage msg = new PacketPowerControlTab(MAX_VALUE, MIN_VALUE, TILE_ENTITY.getPos());
		PacketHandler.net.sendToServer(msg);	
		}
	}
	public void mouseInteraction(int x, int y, int button) {	
		YELLOW_TAB.tabMouseExtension(x, y, button);
		MAX_PERCENTAGE.mouseClicked(x, y, button);
		MIN_PERCENTAGE.mouseClicked(x, y, button);
		SET_PERCENTAGE.buttonMouseClick(x, y, button);
	}	
	public void keyboardInteraction(char par1, int par2) {
		MAX_PERCENTAGE.textboxKeyTyped(par1, par2);	
		MIN_PERCENTAGE.textboxKeyTyped(par1, par2);	
	}
	public void setTabOpen() {
		if(YELLOW_TAB.TAB_ANIMATION == YELLOW_TAB.TAB_ANIMATION_SPEED) {
			IS_TAB_OPEN = true;
		}else{
			IS_TAB_OPEN = false;
		}
		if(YELLOW_TAB.GROWTH_STATE == 2) {
			IS_TAB_OPEN = false;
		}
	}
	public void setGrowthState() {
		int state = YELLOW_TAB.GROWTH_STATE ;
			GROWTH_STATE = state;
	}
}
