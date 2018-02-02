package theking530.staticpower.client.gui.widgets.tabs;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import api.gui.tab.BaseGuiTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import theking530.staticpower.assists.utilities.GuiTextures;
import theking530.staticpower.assists.utilities.GuiUtilities;

public class GuiInfoTab extends BaseGuiTab {
	

	public int BUTTON;
	private FontRenderer FONT_RENDERER;
	
	private List<String> INFO;
	private String TITLE;

	public GuiInfoTab(int width, int height){
		super(width, height, GuiTextures.GREEN_TAB, Items.PAPER);
		FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
	}
	public void setText(String title, List<String> text) {
		INFO = text;
		TITLE = title;
	}
	public void setText(String title, String text) {
		INFO = Arrays.asList(text.split("="));
		int stringLengthMax = 0;
		for(int i=0; i<INFO.size(); i++) {
			if(FONT_RENDERER.getStringWidth(INFO.get(i)) > stringLengthMax) {
				stringLengthMax = FONT_RENDERER.getStringWidth(INFO.get(i));
			}
		}
		this.tabWidth = stringLengthMax+8;
		TITLE = title;
	}	
	@Override
	protected void drawExtra(int xPos, int yPos, float partialTicks) {
		if(isOpen()) {	
			if(INFO.size() > 0) {
				drawTextBG(xPos, yPos);	
				drawText(xPos, yPos);	
			}
    	}	
	}
	private void drawText(int xPos, int yPos) {
		FONT_RENDERER.drawStringWithShadow(TITLE, xPos+FONT_RENDERER.getStringWidth(TITLE)/2+3, yPos+8, GuiUtilities.getColor(242, 0, 255));	
		for(int i = 0; i < INFO.size(); i++) {
			String string = (String) INFO.get(i);
    		FONT_RENDERER.drawString(string, xPos + 18, (yPos+28)+11*i, 16777215);
		}	
	}
	public void drawTextBG(int xPos, int yPos) {
		int height = 0;
		if(INFO != null) {
			height = INFO.size()*12;
		}
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);	
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos+tabWidth+15, yPos+height+28, 0).tex(0,1).endVertex();
		vertexbuffer.pos(xPos+tabWidth+15, yPos+22, 0).tex(0,0).endVertex();
		vertexbuffer.pos(xPos+10, yPos+22, 0).tex(1,0).endVertex();
		vertexbuffer.pos(xPos+10, yPos+height+28, 0).tex(1,1).endVertex();	
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
	@Override
	protected void handleExtraMouseInteraction(int mouseX, int mouseY, int button) {

	}
	@Override
	protected void handleExtraKeyboardInteraction(char par1, int par2) {
		
	}

	@Override
	protected void handleExtraClickMouseMove(int x, int y, int button, long time) {

	}
}
