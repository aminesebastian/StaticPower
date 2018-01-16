package api.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.StaticVertexBuffer;

public class ItemButton extends Gui{
	
	public int BUTTON_WIDTH;
	public int BUTTON_HEIGHT;
	public int BUTTON_XPOS;
	public int BUTTON_YPOS;
	private int GUI_LEFT;
	private int GUI_TOP;
	private Item ITEM;
	public int GROWTH_STATE;
	public TileEntity TILE_ENTITY;
	private int WIDTH;
	private int HEIGHT;
	private int xSIZE;
	private int ySIZE;
	public int TIMER = 0;
	public boolean HIGHLIGHT;
	public boolean IS_VISIBLE = true;
	public boolean CLICKED = false;
	
	
	public ItemButton(int guiLeft, int guiTop, int width, int height, int xPos, int yPos, Item item) {
		this.GUI_LEFT = guiLeft;
		this.GUI_TOP = guiTop;
		this.BUTTON_WIDTH = width;
		this.BUTTON_HEIGHT = height;
		this.BUTTON_XPOS = xPos;
		this.BUTTON_YPOS = yPos;
		this.ITEM = item;		
	}
	public void playSound() {
		if(TIMER == 1) {
			if(TILE_ENTITY != null) {
				//TILE_ENTITY.getWorldObj().playSound(TILE_ENTITY.xCoord, TILE_ENTITY.yCoord, TILE_ENTITY.zCoord, "gui.button.press", 0.4F, 1.0F, CLICKED); 
			}
		}
	}
	public void updateMethod(int width, int height, int xSize, int ySize, TileEntity te) {
		playSound();
		clickTime();
		this.WIDTH = width;
		this.HEIGHT = height;
		this.xSIZE = xSize;
		this.ySIZE = ySize;
		this.TILE_ENTITY = te;
	}
	public void drawButton() {
		if(IS_VISIBLE) {
		drawButtonBase();
			if(ITEM != null) {
				drawButtonIcon();
			}
		}
	}
	public void drawButtonIcon() {
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		int buttonLeft = GUI_LEFT + j + BUTTON_XPOS+1;
		int buttonTop = GUI_TOP + k + BUTTON_YPOS+1;
		
		GuiDrawItem.drawItem(ITEM, buttonLeft, buttonTop, 1, 0, this.zLevel, 1.0f);
	}
	public void drawButtonBase() {
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		int buttonLeft = GUI_LEFT + j + BUTTON_XPOS;
		int buttonTop = GUI_TOP + k + BUTTON_YPOS;
		
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		if(CLICKED == true || HIGHLIGHT == true) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_HOVER);
		}else{
			Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON);
		}	
		//Top
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+3, 0, 0, .15);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+3, 0, 1, .15);
		
		//Bottom
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+(BUTTON_HEIGHT), 0, 0, 1);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+(BUTTON_HEIGHT - 3), 0, 0, .85);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(BUTTON_HEIGHT - 3), 0, 1, .85);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(BUTTON_HEIGHT), 0, 1, 1);
	
		//Right
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+(BUTTON_HEIGHT), 0, 0, 1);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft-3+BUTTON_WIDTH, buttonTop, 0, .02, 0);
		StaticVertexBuffer.pos(buttonLeft-3+BUTTON_WIDTH, buttonTop+(BUTTON_HEIGHT), 0, .02, 1);

		//Left
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop+(BUTTON_HEIGHT), 0, .98, 1);
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop, 0, .98, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(BUTTON_HEIGHT), 0, 1, 1);

		//Body
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH-3, buttonTop-3+(BUTTON_HEIGHT), 0, 0.2, .8);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH-3, buttonTop+3, 0, 0.2, 0.2);
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop+3, 0, .8, 0.2);
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop-3+(BUTTON_HEIGHT), 0, .8, .8);
		
		tessellator.draw();
	}	
    public void sound(SoundHandler soundHandler) {
    	//soundHandler.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }
	public void buttonMouseClick(int par1, int par2, int button) {
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		if(par1 > j + BUTTON_XPOS && par1 < j + BUTTON_XPOS + BUTTON_WIDTH && IS_VISIBLE) {
	    	if(par2 > k + BUTTON_YPOS && par2 < k + BUTTON_YPOS + BUTTON_HEIGHT) {
	    		CLICKED = true; 
	    		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	    	}
	    }
	}
	public void clickTime() {
		if(CLICKED) {		
			TIMER++;
			if(TIMER == 3) {
			CLICKED = false;
			TIMER = 0;
			}
		}
	}
}
