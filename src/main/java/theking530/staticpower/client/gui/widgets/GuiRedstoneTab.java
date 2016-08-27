package theking530.staticpower.client.gui.widgets;

import org.lwjgl.opengl.GL11;

import api.gui.BaseGuiTab;
import api.gui.BlockButton;
import api.gui.ItemButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.EnumTextFormatting;

public class GuiRedstoneTab {
	
	public int GUI_LEFT;
	public int GUI_TOP;
	public int GROWTH_STATE;
	public World WORLD;
	public EntityPlayer PLAYER;
	public boolean IS_TAB_OPEN;
	public TileEntity TILE_ENTITY;
	private FontRenderer FONT_RENDERER;
	private ResourceLocation redTab = new ResourceLocation(Reference.MODID + ":" + "textures/gui/RedTab.png");
	private ResourceLocation bg = new ResourceLocation(Reference.MODID + ":" + "textures/gui/ButtonBG.png");
	
	public BaseGuiTab RED_TAB = new BaseGuiTab(GUI_LEFT, GUI_TOP, 90, 90, 175, 36, redTab, Items.REDSTONE);
	public ItemButton IGNORE_REDSTONE = new ItemButton(GUI_LEFT, GUI_TOP, 20, 20, 190, 62, Items.GUNPOWDER);
	public ItemButton LOW_REDSTONE = new ItemButton(GUI_LEFT, GUI_TOP, 20, 20, 220, 62, Items.REDSTONE);
	public BlockButton HIGH_REDSTONE = new BlockButton(GUI_LEFT, GUI_TOP, 20, 20, 250, 62, Blocks.REDSTONE_TORCH);
	
	public GuiRedstoneTab(int guiLeft, int guiTop, TileEntity TILE_ENTITY){
		this.GUI_LEFT = guiLeft;
		this.GUI_TOP = guiTop;
	}
	public void drawTab() {
		RED_TAB.drawTab();
		if(IS_TAB_OPEN) {
			drawButtonBG();	
			IGNORE_REDSTONE.drawButton();
			LOW_REDSTONE.drawButton();
			HIGH_REDSTONE.drawButton();
			drawText();
			function();
		}else{
			IGNORE_REDSTONE.TIMER = 0;
			LOW_REDSTONE.TIMER = 0;
			HIGH_REDSTONE.TIMER = 0;
			IGNORE_REDSTONE.CLICKED = false;
			LOW_REDSTONE.CLICKED = false;
			HIGH_REDSTONE.CLICKED = false;
		}
	}
	public void drawText() {
		String tabName = EnumTextFormatting.YELLOW + "Redstone Config";
		String redstoneMode = "Mode: ";
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		int j = (RED_TAB.WIDTH - RED_TAB.xSIZE) / 2;
		int k = (RED_TAB.HEIGHT - RED_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + RED_TAB.TAB_XPOS;
		int tabTop = GUI_TOP + k;
		modeText(tabLeft, tabTop);	
		this.FONT_RENDERER.drawStringWithShadow(redstoneMode, tabLeft-this.FONT_RENDERER.getStringWidth(redstoneMode)/2 + 21, tabTop+95, 16777215);
		this.FONT_RENDERER.drawStringWithShadow(tabName, tabLeft-this.FONT_RENDERER.getStringWidth(tabName)/2 + 58, tabTop+44, 16777215);		
	}
	public void modeText(int tabLeft, int tabTop) {
		String mode;
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		if(entity.REDSTONE_MODE == 1) {
		this.FONT_RENDERER.drawStringWithShadow("Low", tabLeft + 37, tabTop+95, 16777215);
		this.FONT_RENDERER.drawStringWithShadow("This machine will", tabLeft + 5, tabTop+110, 16777215);		
		this.FONT_RENDERER.drawStringWithShadow("only operate with no", tabLeft + 5, tabTop+118, 16777215);	
		this.FONT_RENDERER.drawStringWithShadow("signal.", tabLeft + 5, tabTop+127, 16777215);
		}
		if(entity.REDSTONE_MODE == 2) {
		this.FONT_RENDERER.drawStringWithShadow("High", tabLeft + 37, tabTop+95, 16777215);
		this.FONT_RENDERER.drawStringWithShadow("This machine will", tabLeft + 5, tabTop+110, 16777215);		
		this.FONT_RENDERER.drawStringWithShadow("only operate with a", tabLeft + 5, tabTop+118, 16777215);	
		this.FONT_RENDERER.drawStringWithShadow("redstone signal.", tabLeft + 5, tabTop+127, 16777215);
		}
		if(entity.REDSTONE_MODE == 0) {
		this.FONT_RENDERER.drawStringWithShadow("Ignore", tabLeft + 37, tabTop+95, 16777215);	
		this.FONT_RENDERER.drawStringWithShadow("This machine will", tabLeft + 5, tabTop+110, 16777215);		
		this.FONT_RENDERER.drawStringWithShadow("ignore any redstone", tabLeft + 6, tabTop+118, 16777215);	
		this.FONT_RENDERER.drawStringWithShadow("signal.", tabLeft + 5, tabTop+127, 16777215);
		}
	}
	public void drawButtonBG() {
		int j = (RED_TAB.WIDTH - RED_TAB.xSIZE) / 2;
		int k = (RED_TAB.HEIGHT - RED_TAB.ySIZE) / 2;
		int tabLeft = GUI_LEFT + j + RED_TAB.TAB_XPOS - 10;
		int tabTop = GUI_TOP + k;

		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(bg);
		GlStateManager.color(1, 1, 1);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(tabLeft+114, tabTop+88, 0).tex(0,1).endVertex();
		vertexbuffer.pos(tabLeft+114, tabTop+57, 0).tex(0,0).endVertex();
		vertexbuffer.pos(tabLeft+17, tabTop+57, 0).tex(1,0).endVertex();
		vertexbuffer.pos(tabLeft+17, tabTop+88, 0).tex(1,1).endVertex();	
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);

	}
	public void function() {
		if(TILE_ENTITY != null) {
			BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;		
			if(IGNORE_REDSTONE.CLICKED) {
				entity.REDSTONE_MODE = 0;
				IMessage msg = new PacketRedstoneTab(0, entity.getPos());
				PacketHandler.net.sendToServer(msg);
			}
			if(LOW_REDSTONE.CLICKED) {
				entity.REDSTONE_MODE = 1;
				IMessage msg = new PacketRedstoneTab(1, entity.getPos());
				PacketHandler.net.sendToServer(msg);
			}
			if(HIGH_REDSTONE.CLICKED) {
				entity.REDSTONE_MODE = 2;	
				IMessage msg = new PacketRedstoneTab(2, entity.getPos());
				PacketHandler.net.sendToServer(msg);
			}	
		}
	}
	public void updateTab(int width, int height, int xSize, int ySize, FontRenderer fontRenderer, TileEntity te) {
		RED_TAB.updateMethod(width, height, xSize, ySize);
		IGNORE_REDSTONE.updateMethod(width, height, xSize, ySize, te);
		LOW_REDSTONE.updateMethod(width, height, xSize, ySize, te);
		HIGH_REDSTONE.updateMethod(width, height, xSize, ySize, te);
		setTabOpen();
		setGrowthState();
		this.FONT_RENDERER = fontRenderer;
		this.TILE_ENTITY = te;
	}
	public void mouseInteraction(int par1, int par2, int button) {
		RED_TAB.tabMouseExtension(par1, par2, button);
		IGNORE_REDSTONE.buttonMouseClick(par1, par2, button);
		LOW_REDSTONE.buttonMouseClick(par1, par2, button);
		HIGH_REDSTONE.buttonMouseClick(par1, par2, button);		
	}	
	public void setTabOpen() {
		if(RED_TAB.TAB_ANIMATION == RED_TAB.TAB_ANIMATION_SPEED) {
			IS_TAB_OPEN = true;
			IGNORE_REDSTONE.IS_VISIBLE = true;
			LOW_REDSTONE.IS_VISIBLE = true;
			HIGH_REDSTONE.IS_VISIBLE = true;
		}else{
			IS_TAB_OPEN = false;
			IGNORE_REDSTONE.IS_VISIBLE = false;
			LOW_REDSTONE.IS_VISIBLE = false;
			HIGH_REDSTONE.IS_VISIBLE = false;
		}
	}
	public void setGrowthState() {
		int state = RED_TAB.GROWTH_STATE ;
			GROWTH_STATE = state;
	}
}
