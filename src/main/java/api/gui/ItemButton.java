package api.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.StaticVertexBuffer;

public class ItemButton extends Gui{
	
	public int BUTTON_WIDTH;
	public int BUTTON_HEIGHT;
	public int BUTTON_XPOS;
	public int BUTTON_YPOS;
	
	private Item ITEM;
	public int GROWTH_STATE;

	public int TIMER = 0;
	public boolean HIGHLIGHT;
	public boolean IS_VISIBLE = true;
	public boolean CLICKED = false;
	
	
	public ItemButton(int width, int height, Item item) {
		this.BUTTON_WIDTH = width;
		this.BUTTON_HEIGHT = height;

		this.ITEM = item;		
	}
	public void playSound() {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
	public void updateMethod() {
		clickTime();
	}
	public void drawButton(int xPos, int yPos) {
		if(IS_VISIBLE) {
		drawButtonBase(xPos, yPos);
			if(ITEM != null) {
				drawButtonIcon(xPos, yPos);
			}
		}
		BUTTON_XPOS = xPos;
		BUTTON_YPOS = yPos;
	}
	public void drawButtonIcon(int xPos, int yPos) {
		int buttonLeft = xPos+1;
		int buttonTop =  yPos+1;
		
		ItemStack item = new ItemStack(ITEM);		
		RenderItem customRenderer = Minecraft.getMinecraft().getRenderItem();
		customRenderer.renderItemIntoGUI(item, buttonLeft+1, buttonTop);
	}
	public void drawButtonBase(int xPos, int yPos) {
		int buttonLeft = xPos;
		int buttonTop = yPos;
		
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
		if(par1 > BUTTON_XPOS && par1 < BUTTON_XPOS + BUTTON_WIDTH && IS_VISIBLE) {
	    	if(par2 > BUTTON_YPOS && par2 < BUTTON_YPOS + BUTTON_HEIGHT) {
	    		CLICKED = true;
	    		playSound();
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
