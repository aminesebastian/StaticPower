package api.gui.button;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.staticpower.assists.utilities.StaticVertexBuffer;
import theking530.staticpower.client.gui.GuiTextures;

public class StandardButton extends BaseButton{

	public StandardButton(int width, int height, int xPos, int yPos) {
		super(width, height, xPos, yPos);
	}

	@Override
	protected void drawButton() {
		int buttonLeft = xPosition;
		int buttonTop = yPosition;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        
		if(isClicked() || isHovered() || isToggled()) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_HOVER);
		}else{
			Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON);
		}	
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		//Top
		StaticVertexBuffer.pos(buttonLeft+width, buttonTop+3, 0, 0, .15);
		StaticVertexBuffer.pos(buttonLeft+width, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+3, 0, 1, .15);
		
		//Bottom
		StaticVertexBuffer.pos(buttonLeft+width, buttonTop+(height), 0, 0, 1);
		StaticVertexBuffer.pos(buttonLeft+width, buttonTop+(height - 3), 0, 0, .85);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(height - 3), 0, 1, .85);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(height), 0, 1, 1);
	
		//Right
		StaticVertexBuffer.pos(buttonLeft+width, buttonTop+(height), 0, 0, 1);
		StaticVertexBuffer.pos(buttonLeft+width, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft-3+width, buttonTop, 0, .02, 0);
		StaticVertexBuffer.pos(buttonLeft-3+width, buttonTop+(height), 0, .02, 1);

		//Left
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop+(height), 0, .98, 1);
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop, 0, .98, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop+(height), 0, 1, 1);

		//Body
		StaticVertexBuffer.pos(buttonLeft+width-3, buttonTop-3+(height), 0, 0.2, .8);
		StaticVertexBuffer.pos(buttonLeft+width-3, buttonTop+3, 0, 0.2, 0.2);
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop+3, 0, .8, 0.2);
		StaticVertexBuffer.pos(buttonLeft+3, buttonTop-3+(height), 0, .8, .8);
		
		tessellator.draw();	
	}
}
