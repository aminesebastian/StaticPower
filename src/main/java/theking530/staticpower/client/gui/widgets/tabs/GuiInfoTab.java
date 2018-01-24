package theking530.staticpower.client.gui.widgets.tabs;

import java.util.List;

import org.lwjgl.opengl.GL11;

import api.gui.BaseGuiTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GUIUtilities;
import theking530.staticpower.utils.GuiTextures;

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
		
	@Override
	protected void drawExtra(int xPos, int yPos, float partialTicks) {
		if(isOpen()) {	
			drawTextBG(xPos, yPos);	
			drawText(xPos, yPos);
    	}	
	}
	private void drawText(int xPos, int yPos) {
		String tabName = TITLE;
		String tabNameColored =  tabName;

		FONT_RENDERER.drawStringWithShadow(tabNameColored, xPos-FONT_RENDERER.getStringWidth(tabNameColored)/2 + 63, yPos+8, GUIUtilities.getColor(242, 0, 255));	
		for(int i = 0; i < INFO.size(); i++) {
			String string = (String) INFO.get(i);
    		FONT_RENDERER.drawString(string, xPos + 18, (yPos+28)+11*i, 16777215);
		}	
	}
	public void drawTextBG(int xPos, int yPos) {
		int height = 20;
		if(INFO != null) {
			height = INFO.size()*19;
		}
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);	
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos+114, yPos+height, 0).tex(0,1).endVertex();
		vertexbuffer.pos(xPos+114, yPos+22, 0).tex(0,0).endVertex();
		vertexbuffer.pos(xPos+12, yPos+22, 0).tex(1,0).endVertex();
		vertexbuffer.pos(xPos+12, yPos+height, 0).tex(1,1).endVertex();	
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
