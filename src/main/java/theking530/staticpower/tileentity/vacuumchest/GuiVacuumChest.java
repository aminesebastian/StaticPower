package theking530.staticpower.tileentity.vacuumchest;

import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.utils.GuiTextures;

public class GuiVacuumChest extends GuiContainer{
	
	private TileEntityVacuumChest V_CHEST;
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	public GuiInfoTab INFO_TAB = new GuiInfoTab(guiLeft, guiTop);
	public GuiDrawItem DRAW_ITEM = new GuiDrawItem();
	
	public GuiVacuumChest(InventoryPlayer invPlayer, TileEntityVacuumChest teVChest) {
		super(new ContainerVacuumChest(invPlayer, teVChest));
		V_CHEST = teVChest;		
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teVChest);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teVChest);
		xSize = 176;
		ySize = 205;	
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, V_CHEST);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, V_CHEST);
		INFO_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, V_CHEST);
		if(INFO_TAB.GROWTH_STATE == 1){
			REDSTONE_TAB.RED_TAB.GROWTH_STATE = 2;
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}
		if(REDSTONE_TAB.GROWTH_STATE == 1) {
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}
	}	
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;        		 
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.V_CHEST.getName());
		this.fontRendererObj.drawString(name, this.xSize - 169, 6, 4210752 );
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {	
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.VCHEST_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
    	String text = ("Vacuums items in a  =nearby radius.");
    	String[] splitMsg = text.split("=");
		
    	DRAW_ITEM.drawItem(ModItems.BasicItemFilter, guiLeft, guiTop, 8, 78, this.zLevel);
		INFO_TAB.drawTab(Arrays.asList(splitMsg));
	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    REDSTONE_TAB.mouseInteraction(x, y, button);
	    SIDE_TAB.mouseInteraction(x, y, button);
	    INFO_TAB.mouseInteraction(x, y, button);
	}	
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		SIDE_TAB.mouseDrag(x, y, button, time);
	}	
}



