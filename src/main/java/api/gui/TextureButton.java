package api.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.StaticVertexBuffer;

public class TextureButton extends Gui{
	
	public int BUTTON_WIDTH;
	public int BUTTON_HEIGHT;
	public int BUTTON_XPOS;
	public int BUTTON_YPOS;
	private int GUI_LEFT;
	private int GUI_TOP;

	public int GROWTH_STATE;
	private int WIDTH;
	private int HEIGHT;
	private int xSIZE;
	private int ySIZE;
	private int TIMER = 0;
	public boolean IS_VISIBLE = true;
	public boolean CLICKED = false;
	
	
	public TextureButton(int guiLeft, int guiTop, int width, int height, int xPos, int yPos, ResourceLocation texture) {
		this.GUI_LEFT = guiLeft;
		this.GUI_TOP = guiTop;
		this.BUTTON_WIDTH = width;
		this.BUTTON_HEIGHT = height;
		this.BUTTON_XPOS = xPos;
		this.BUTTON_YPOS = yPos;	
	}
	public void setInfo(int width, int height, int xSize, int ySize) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.xSIZE = xSize;
		this.ySIZE = ySize;
	}
	public void drawButton() {
		clickTime();
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		int buttonLeft = GUI_LEFT + j + BUTTON_XPOS;
		int buttonTop = GUI_TOP + k + BUTTON_YPOS;
		GL11.glDisable(GL11.GL_LIGHTING);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        
		if(IS_VISIBLE) {
			if(CLICKED == true) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_HOVER);
			}else{
				Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON);
			}	

		//Top
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+2, 0, 0, .1);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+2, 0, 1, .1);
		
		//Bottom
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+(BUTTON_HEIGHT), 0, 0, 1);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+18, 0, 0, .9);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+18, 0, 1, .9);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(BUTTON_HEIGHT), 0, 1, 1);
		
		//Right
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop+(BUTTON_HEIGHT), 0, 0, 1);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft-2+BUTTON_WIDTH, buttonTop, 0, .01, 0);
		StaticVertexBuffer.pos(buttonLeft-2+BUTTON_WIDTH, buttonTop+(BUTTON_HEIGHT), 0, .01, 1);

		//Left
		StaticVertexBuffer.pos(buttonLeft+2, buttonTop+(BUTTON_HEIGHT), 0, .99, 1);
		StaticVertexBuffer.pos(buttonLeft+2, buttonTop, 0, .99, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(BUTTON_HEIGHT), 0, 1, 1);

		//Body
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH-2, buttonTop-2+(BUTTON_HEIGHT), 0, 0.1, .8);
		StaticVertexBuffer.pos(buttonLeft+BUTTON_WIDTH-2, buttonTop+2, 0, 0.1, 0.1);
		StaticVertexBuffer.pos(buttonLeft+2, buttonTop+2, 0, .8, 0.1);
		StaticVertexBuffer.pos(buttonLeft+2, buttonTop-2+(BUTTON_HEIGHT), 0, .8, .8);
		
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);	
		}
	}	
    public void sound(SoundHandler soundHandler) {
    	//soundHandler.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }
	public void buttonMouseClick(int par1, int par2) {
		int j = (WIDTH - xSIZE) / 2;
		int k = (HEIGHT - ySIZE) / 2;
		if(par1 > j + BUTTON_XPOS && par1 < j + BUTTON_XPOS + 24) {
	    	if(par2 > k + BUTTON_YPOS && par2 < k + BUTTON_YPOS + 24) {
	    		CLICKED = true; 
	    	}
	    }
	}
	public void clickTime() {
		if(CLICKED) {		
			TIMER++;
			if(TIMER == 10) {
			CLICKED = false;
			TIMER = 0;
			}
		}
	}
}
