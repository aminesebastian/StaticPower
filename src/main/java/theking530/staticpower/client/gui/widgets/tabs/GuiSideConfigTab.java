package theking530.staticpower.client.gui.widgets.tabs;

import org.lwjgl.opengl.GL11;

import api.gui.BaseGuiTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.utilities.GuiTextures;
import theking530.staticpower.assists.utilities.SidePicker;
import theking530.staticpower.assists.utilities.SideUtilities;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.assists.utilities.SidePicker.Side;
import theking530.staticpower.client.sideSelector.GuiComponentSideSelector;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.tileentity.BaseTileEntity;

public class GuiSideConfigTab extends BaseGuiTab {

	private int MOUSE_X;
	private int MOUSE_Y;
	private int MOUSE_DRAGX;
	private int MOUSE_DRAGY;
	private Side SIDE;
	public int BUTTON;
	public World WORLD;
	public TileEntity TILE_ENTITY;
	private FontRenderer FONT_RENDERER;

	private int X_POS;
	private int Y_POS;
	
	private GuiComponentSideSelector selector;
	
	public GuiSideConfigTab(int width, int height, TileEntity te){
		super(width, height, GuiTextures.BLUE_TAB, te.getBlockType());
		TILE_ENTITY = te;
		FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
		
		selector = new GuiComponentSideSelector(140, 18, 40.0d, te.getWorld().getBlockState(te.getPos()), te, true);
	}
	@Override
	public void drawExtra(int xPos, int yPos, float partialTicks) {
		if(isOpen()) {	
    		if(MOUSE_X > 0 && BUTTON == 1) {
    			//rotationDecider(SIDE);
    			MOUSE_X = 0;
    		}	
    	drawText(xPos+10, yPos+8);
		drawButtonBG(xPos, yPos);	
		selector.render(xPos, yPos, MOUSE_DRAGX, MOUSE_DRAGY);
		X_POS = xPos;
		Y_POS = yPos;
		}
	}
	public void drawText(int xPos, int yPos) {
		String tabName = "Side Config";
		modeText(xPos, yPos);	
		FONT_RENDERER.drawStringWithShadow(tabName, xPos-this.FONT_RENDERER.getStringWidth(tabName)/2 + 52, yPos, 16777215);
		
	}
	public void modeText(int tabLeft, int tabTop) {

	}
	public void drawButtonBG(int xPos, int yPos) {
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		GlStateManager.color(1, 1, 1);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos+110, yPos+110, 0).tex(0,1).endVertex();
		vertexbuffer.pos(xPos+110, yPos+20, 0).tex(0,0).endVertex();
		vertexbuffer.pos(xPos+15, yPos+20, 0).tex(1,0).endVertex();
		vertexbuffer.pos(xPos+15, yPos+110, 0).tex(1,1).endVertex();	
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
    @Override
    public void handleExtraMouseInteraction(int par1, int par2, int button) {
		MOUSE_X = par1;
		MOUSE_Y = par2;
		BUTTON = button;
	}	
	@Override
	protected void handleExtraKeyboardInteraction(char par1, int par2) {
		
	}
	@Override
	protected void handleExtraClickMouseMove(int mouseX, int mouseY, int button, long time) {
		MOUSE_DRAGX = mouseX;
		MOUSE_DRAGY = mouseY;
	}
}