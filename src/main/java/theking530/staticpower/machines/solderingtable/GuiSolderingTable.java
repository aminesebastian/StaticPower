package theking530.staticpower.machines.solderingtable;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.CustomGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.utils.GuiTextures;

public class GuiSolderingTable extends CustomGuiContainer{

	public GuiSideConfigTab SIDE_TAB;
	private TileEntitySolderingTable TABLE;
	public GuiDrawItem DRAW_ITEM = new GuiDrawItem();
	
	public GuiSolderingTable(InventoryPlayer invPlayer, TileEntitySolderingTable teTable) {
		super(new ContainerSolderingTable(invPlayer, teTable));
		TABLE = teTable;	
		xSize = 176;
		ySize = 179;
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teTable);
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, TABLE);
	}	
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
		drawRect(guiLeft + 82, guiTop + 38, 176, 69, 3394815);
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.TABLE.getName());
	
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6,4210752 );
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.SOLDERING_TABLE_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    	DRAW_ITEM.drawItem(ModItems.SolderingIron, guiLeft, guiTop, 11, 17, this.zLevel);
		SIDE_TAB.drawTab();		
	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    SIDE_TAB.mouseInteraction(x, y, button);
	}	
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		SIDE_TAB.mouseDrag(x, y, button, time);
	}	
}


