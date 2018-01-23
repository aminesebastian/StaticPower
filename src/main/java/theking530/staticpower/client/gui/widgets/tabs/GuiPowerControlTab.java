package theking530.staticpower.client.gui.widgets.tabs;

import org.lwjgl.opengl.GL11;

import api.gui.BaseGuiTab;
import api.gui.ItemButton;
import api.gui.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.StaticVertexBuffer;

public class GuiPowerControlTab extends BaseGuiTab{
	
	public TileEntity TILE_ENTITY;
	private FontRenderer FONT_RENDERER;

	private int MIN_VALUE;
	private TextField MIN_PERCENTAGE;
	private int MAX_VALUE;
	private TextField MAX_PERCENTAGE;
	private ItemButton SET_PERCENTAGE;
	
	public GuiPowerControlTab(int guiLeft, int guiTop, TileEntity te){
		super(guiTop, guiTop, GuiTextures.PURPLE_TAB, ModItems.StaticWrench);
		FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
		TILE_ENTITY = te;
		
		MIN_PERCENTAGE = new TextField(guiLeft + 230, guiTop + 35, 30, 15);
		MAX_PERCENTAGE = new TextField(guiLeft + 230, guiTop + 54, 30, 15);
		SET_PERCENTAGE = new ItemButton(16, 16, ModItems.BasicBattery);
	}
	@Override
	public void drawExtra(int xPos, int yPos, float partialTicks) {
		if(isOpen()) {
			MAX_PERCENTAGE.updateMethod();
			MAX_PERCENTAGE.setMaxStringLength(3);		
			MIN_PERCENTAGE.updateMethod();
			SET_PERCENTAGE.updateMethod();
			MIN_PERCENTAGE.setMaxStringLength(3);
			
			SET_PERCENTAGE.drawButton(xPos, yPos);
			MAX_PERCENTAGE.drawTextBox();
			MIN_PERCENTAGE.drawTextBox();
			function();
			drawText(xPos, yPos);
		}else{
			SET_PERCENTAGE.TIMER = 0;
			SET_PERCENTAGE.CLICKED = false;
		}		
	}
	public void drawText(int xPos, int yPos) {
		String tabName = "Power Control";
		modeText(xPos, yPos);	
		this.FONT_RENDERER.drawStringWithShadow(tabName, xPos-this.FONT_RENDERER.getStringWidth(tabName)/2 + 60, yPos+15, 16777215);	
	}
	public void modeText(int tabLeft, int tabTop) {
		String max = "Minimum:";
		String min = "Maximum:";
		String percent = "%";
		String buttonText = "Confirm";

		this.FONT_RENDERER.drawString(max, tabLeft+10, tabTop+39, 16777215);				
		this.FONT_RENDERER.drawString(min, tabLeft+10, tabTop+58, 16777215);	
		this.FONT_RENDERER.drawString(EnumTextFormatting.BOLD + percent, tabLeft+90, tabTop+39, 16777215);	
		this.FONT_RENDERER.drawString(EnumTextFormatting.BOLD + percent, tabLeft+90, tabTop+58, 16777215);
		this.FONT_RENDERER.drawString(buttonText, tabLeft+40, tabTop+80, 16777215);
	}
	public void buttonText(int tabLeft, int tabTop){
	}
	public void drawButtonBG(int xPos, int yPos) {
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
		StaticVertexBuffer.pos(xPos+104, yPos+90, 0, 0, 1);
		StaticVertexBuffer.pos(xPos+104, yPos+30, 0, 0, 0);
		StaticVertexBuffer.pos(xPos+17, yPos+30, 0, 1, 0);
		StaticVertexBuffer.pos(xPos+17, yPos+90, 0, 1, 1);	
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
	@Override
	public void handleExtraMouseInteraction(int x, int y, int button) {	
		mouseInteraction(x, y, button);
		MAX_PERCENTAGE.mouseClicked(x, y, button);
		MIN_PERCENTAGE.mouseClicked(x, y, button);
		SET_PERCENTAGE.buttonMouseClick(x, y, button);
	}	
	@Override
	public void handleExtraKeyboardInteraction(char par1, int par2) {
		MAX_PERCENTAGE.textboxKeyTyped(par1, par2);	
		MIN_PERCENTAGE.textboxKeyTyped(par1, par2);	
	}
	@Override
	protected void handleExtraClickMouseMove(int mouseX, int mouseY, int button, long time) {
		
	}
}
