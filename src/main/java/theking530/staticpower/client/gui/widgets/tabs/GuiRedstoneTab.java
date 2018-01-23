package theking530.staticpower.client.gui.widgets.tabs;

import org.lwjgl.opengl.GL11;

import api.gui.BaseGuiTab;
import api.gui.ItemButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.RedstoneModeList.RedstoneMode;

public class GuiRedstoneTab extends BaseGuiTab {
	
	public int GUI_LEFT;
	public int GUI_TOP;
	public int GROWTH_STATE;
	public World WORLD;
	public TileEntity TILE_ENTITY;
	private FontRenderer FONT_RENDERER;

	public ItemButton IGNORE_REDSTONE;
	public ItemButton LOW_REDSTONE;
	public ItemButton HIGH_REDSTONE;
	
	public GuiRedstoneTab(int width, int height, TileEntity te) {
		super(width, height, GuiTextures.RED_TAB, Items.REDSTONE);
		FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
		TILE_ENTITY = te;
		
		IGNORE_REDSTONE = new ItemButton(20, 20, Items.GUNPOWDER);
		LOW_REDSTONE = new ItemButton(20, 20, Items.REDSTONE);
		HIGH_REDSTONE = new ItemButton(20, 20, Item.getItemFromBlock(Blocks.REDSTONE_TORCH));
	}
	@Override
	public void drawExtra(int xPos, int yPos, float partialTicks) {
		if(isOpen()) {
			drawButtonBG(xPos, yPos-32);	
			IGNORE_REDSTONE.drawButton(xPos+25, yPos+30);
			LOW_REDSTONE.drawButton(xPos+55, yPos+30);
			HIGH_REDSTONE.drawButton(xPos+85, yPos+30);
			drawText(xPos+5, yPos-35);
			function();
			IGNORE_REDSTONE.updateMethod();
			LOW_REDSTONE.updateMethod();
			HIGH_REDSTONE.updateMethod();
		}else{
			IGNORE_REDSTONE.TIMER = 0;
			LOW_REDSTONE.TIMER = 0;
			HIGH_REDSTONE.TIMER = 0;
			IGNORE_REDSTONE.CLICKED = false;
			LOW_REDSTONE.CLICKED = false;
			HIGH_REDSTONE.CLICKED = false;
		}
	}
	public void drawText(int xPos, int yPos) {
		String tabName = EnumTextFormatting.YELLOW + "Redstone Config";
		String redstoneMode = "Mode: ";

		modeText(xPos, yPos);	
		FONT_RENDERER.drawStringWithShadow(redstoneMode, xPos-this.FONT_RENDERER.getStringWidth(redstoneMode)/2 + 24, yPos+95, 16777215);
		FONT_RENDERER.drawStringWithShadow(tabName, xPos-this.FONT_RENDERER.getStringWidth(tabName)/2 + 58, yPos+43, 16777215);		
	}
	public void modeText(int tabLeft, int tabTop) {
		BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;
		if(entity.REDSTONE_MODE == RedstoneMode.Low) {
			FONT_RENDERER.drawStringWithShadow("Low", tabLeft + 37, tabTop+95, 16777215);
			FONT_RENDERER.drawStringWithShadow("This machine will", tabLeft + 8, tabTop+110, 16777215);		
			FONT_RENDERER.drawStringWithShadow("only operate with no", tabLeft + 8, tabTop+118, 16777215);	
			FONT_RENDERER.drawStringWithShadow("signal.", tabLeft + 8, tabTop+127, 16777215);
		} else if(entity.REDSTONE_MODE == RedstoneMode.High) {
			FONT_RENDERER.drawStringWithShadow("High", tabLeft + 37, tabTop+95, 16777215);
			FONT_RENDERER.drawStringWithShadow("This machine will", tabLeft + 8, tabTop+110, 16777215);		
			FONT_RENDERER.drawStringWithShadow("only operate with a", tabLeft + 8, tabTop+118, 16777215);	
			FONT_RENDERER.drawStringWithShadow("redstone signal.", tabLeft + 8, tabTop+127, 16777215);
		} else if(entity.REDSTONE_MODE == RedstoneMode.Ignore) {
			FONT_RENDERER.drawStringWithShadow("Ignore", tabLeft + 37, tabTop+95, 16777215);	
			FONT_RENDERER.drawStringWithShadow("This machine will", tabLeft + 8, tabTop+110, 16777215);		
			FONT_RENDERER.drawStringWithShadow("ignore any redstone", tabLeft + 8, tabTop+118, 16777215);	
			FONT_RENDERER.drawStringWithShadow("signal.", tabLeft + 8, tabTop+127, 16777215);
		}
	}
	public void drawButtonBG(int xPos, int yPos) {
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		GlStateManager.color(1, 1, 1);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos+114, yPos+88, 0).tex(0,1).endVertex();
		vertexbuffer.pos(xPos+114, yPos+57, 0).tex(0,0).endVertex();
		vertexbuffer.pos(xPos+17, yPos+57, 0).tex(1,0).endVertex();
		vertexbuffer.pos(xPos+17, yPos+88, 0).tex(1,1).endVertex();	
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);

	}
	public void function() {
		if(TILE_ENTITY != null) {
			BaseTileEntity entity = (BaseTileEntity)TILE_ENTITY;		
			if(IGNORE_REDSTONE.CLICKED) {
				entity.REDSTONE_MODE = RedstoneMode.Ignore;
				IMessage msg = new PacketRedstoneTab(0, entity.getPos());
				PacketHandler.net.sendToServer(msg);
			}
			if(LOW_REDSTONE.CLICKED) {
				entity.REDSTONE_MODE = RedstoneMode.Low;
				IMessage msg = new PacketRedstoneTab(1, entity.getPos());
				PacketHandler.net.sendToServer(msg);
			}
			if(HIGH_REDSTONE.CLICKED) {
				entity.REDSTONE_MODE = RedstoneMode.High;	
				IMessage msg = new PacketRedstoneTab(2, entity.getPos());
				PacketHandler.net.sendToServer(msg);
			}	
		}
	}

	@Override
	protected void handleExtraMouseInteraction(int mouseX, int mouseY, int button) {
		IGNORE_REDSTONE.buttonMouseClick(mouseX, mouseY, button);
		LOW_REDSTONE.buttonMouseClick(mouseX, mouseY, button);
		HIGH_REDSTONE.buttonMouseClick(mouseX, mouseY, button);	
	}
	@Override
	protected void handleExtraKeyboardInteraction(char par1, int par2) {

	}
	@Override
	protected void handleExtraClickMouseMove(int x, int y, int button, long time) {
		
	}	
}
