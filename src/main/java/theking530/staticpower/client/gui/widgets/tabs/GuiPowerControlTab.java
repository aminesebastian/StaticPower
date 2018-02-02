package theking530.staticpower.client.gui.widgets.tabs;

import org.lwjgl.opengl.GL11;

import api.gui.IInteractableGui;
import api.gui.TextField;
import api.gui.button.BaseButton;
import api.gui.button.ButtonManager;
import api.gui.button.StandardButton;
import api.gui.tab.BaseGuiTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.assists.utilities.GuiTextures;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.machines.BaseMachine;

public class GuiPowerControlTab extends BaseGuiTab implements IInteractableGui{
	
	public BaseMachine TILE_ENTITY;
	private FontRenderer FONT_RENDERER;

	private int MIN_VALUE;
	private TextField MIN_PERCENTAGE;
	private int MAX_VALUE;
	private TextField MAX_PERCENTAGE;
	private StandardButton SET_PERCENTAGE;
	
	private ButtonManager buttonManager;
	
	public GuiPowerControlTab(int guiLeft, int guiTop, BaseMachine te){
		super(guiLeft, guiTop, GuiTextures.PURPLE_TAB, ModItems.StaticWrench);
		FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
		TILE_ENTITY = te;
	
		buttonManager = new ButtonManager(this);
		
		MIN_PERCENTAGE = new TextField(30, 15);
		MAX_PERCENTAGE = new TextField(30, 15);
		SET_PERCENTAGE = new StandardButton(48, 20, 0, 0);
		
		buttonManager.registerButton(SET_PERCENTAGE);
	}
	@Override
	public void drawExtra(int xPos, int yPos, float partialTicks) {
		if(isOpen()) {
			drawButtonBG(xPos, yPos);
			MAX_PERCENTAGE.updateMethod();
			MAX_PERCENTAGE.setMaxStringLength(3);		
			MIN_PERCENTAGE.updateMethod();
			SET_PERCENTAGE.setPosition(xPos+40, yPos+67);
			buttonManager.drawButtons();
			MIN_PERCENTAGE.setMaxStringLength(3);
			

			MAX_PERCENTAGE.drawTextBox(xPos+60, yPos+25);
			MIN_PERCENTAGE.drawTextBox(xPos+60, yPos+45);
			function();
			drawText(xPos, yPos);

		}	
	}
	public void drawText(int xPos, int yPos) {
		String tabName = "Power Control";
		this.FONT_RENDERER.drawStringWithShadow(EnumTextFormatting.GREEN + tabName, xPos-this.FONT_RENDERER.getStringWidth(tabName)/2 + 64, yPos+8, 16777215);	
		
		String min = "Minimum:";
		String max = "Maximum:";
		String percent = "%";
		String buttonText = "Confirm";

		this.FONT_RENDERER.drawStringWithShadow(EnumTextFormatting.RED + min, xPos+15, yPos+29, 16777215);				
		this.FONT_RENDERER.drawStringWithShadow(EnumTextFormatting.WHITE + max, xPos+15, yPos+48, 16777215);	
		this.FONT_RENDERER.drawStringWithShadow(EnumTextFormatting.BOLD + percent, xPos+95, yPos+29, 16777215);	
		this.FONT_RENDERER.drawStringWithShadow(EnumTextFormatting.BOLD + percent, xPos+95, yPos+48, 16777215);
		this.FONT_RENDERER.drawStringWithShadow(buttonText, xPos+46, yPos+73, 16777215);
	}
	public void drawButtonBG(int xPos, int yPos) {
		int height = -40;
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);	
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos+10, yPos+20, 0).tex(0,1).endVertex();
		vertexbuffer.pos(xPos+10, yPos+64, 0).tex(0,0).endVertex();
		vertexbuffer.pos(xPos+110, yPos+64, 0).tex(1,0).endVertex();
		vertexbuffer.pos(xPos+110, yPos+20, 0).tex(1,1).endVertex();	
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
	public void function() {

	}
	@Override
	public void handleExtraMouseInteraction(int x, int y, int button) {	
		MAX_PERCENTAGE.mouseClicked(x, y, button);
		MIN_PERCENTAGE.mouseClicked(x, y, button);
		buttonManager.handleMouseInteraction(x, y, button);
	}	
	@Override
	public void handleExtraKeyboardInteraction(char par1, int par2) {
		MAX_PERCENTAGE.textboxKeyTyped(par1, par2);	
		MIN_PERCENTAGE.textboxKeyTyped(par1, par2);	
	}
	@Override
	protected void handleExtraClickMouseMove(int mouseX, int mouseY, int button, long time) {
		
	}
	@Override
	public void buttonPressed(BaseButton button) {
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
		if(SET_PERCENTAGE == button) {
			TILE_ENTITY.minPowerThreshold = MIN_VALUE;
			TILE_ENTITY.maxPowerThreshold = MAX_VALUE;
			IMessage msg = new PacketPowerControlTab(MAX_VALUE, MIN_VALUE, TILE_ENTITY.getPos());
			PacketHandler.net.sendToServer(msg);	
		}
	}
}
