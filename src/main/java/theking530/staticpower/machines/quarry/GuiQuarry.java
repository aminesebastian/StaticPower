package theking530.staticpower.machines.quarry;

import static theking530.staticpower.items.ModItems.BasicItemFilter;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.CustomGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.utils.GUIUtilities;
import theking530.staticpower.utils.GuiTextures;
import theking530.staticpower.utils.WorldUtilities;

public class GuiQuarry extends CustomGuiContainer{
	
	private GuiPowerBarFromEnergyStorage POWERBAR;
	private GuiFluidBarFromTank FLUIDBAR;
	public GuiSideConfigTab SIDE_TAB;
	private TileEntityQuarry QUARRY;
	public GuiDrawItem DRAW_ITEM = new GuiDrawItem(true);
	
	public GuiQuarry(InventoryPlayer invPlayer, TileEntityQuarry teQuarry) {
		super(new ContainerQuarry(invPlayer, teQuarry));
		QUARRY = teQuarry;	
		POWERBAR = new GuiPowerBarFromEnergyStorage(teQuarry);
		FLUIDBAR = new GuiFluidBarFromTank(teQuarry.TANK);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teQuarry);
		xSize = 176;
		ySize = 173;

	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, QUARRY);
	}	
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
		drawRect(guiLeft + 82, guiTop + 38, 176, 69, 3394815);
		
		if(par1 >= 8 + var1 && par2 >= 5 + var2 && par1 <= 24 + var1 && par2 <= 66 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRendererObj); 
		}     
		if(par1 >= 27 + var1 && par2 >= 5 + var2 && par1 <= 35 + var1 && par2 <= 66 + var2) {
			drawHoveringText(POWERBAR.drawText(), par1, par2, fontRendererObj); 
		}	
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.QUARRY.getName());
		fontRendererObj.drawString(name, xSize / 2 - fontRendererObj.getStringWidth(name) / 2 + 3, 7, 255 << 24 | 255 << 16 | 255 << 8 | 255);

		float scale = 0.7F;
		String currentPosition = QUARRY.CURRENT_COORD.toString().substring(9, QUARRY.CURRENT_COORD.toString().length()-1);
		String area = "Area: " + WorldUtilities.getAreaBetweenCorners(QUARRY.STARTING_COORD, QUARRY.ENDING_COORD);
		String tutorial = "• Right click on the Quarry";
		String tutorial2 = "block with a populated";
		String tutorial3 = "Coordinate Marker to start";
		String tutorial4 = "quarrying!";
		String tutorial5 = "• Place a chest on";
		String tutorial6 = "top to collect items!";
		String tutorial7 = "• Power with RF Power.";
		String speed = "Ticks per Operation: " + QUARRY.PROCESSING_TIME;
		String blocks = "Blocks per Operation: " + QUARRY.BLOCKS_PER_TICK;
		String energy = "RF per Block: " + QUARRY.PROCESSING_ENERGY_MULT * 100;
		String fortune = "Fortune Level: " + QUARRY.getFortuneMultiplier();
		
		GL11.glScalef(scale, scale, scale);
		if(!QUARRY.isAbleToMine()) {
			fontRendererObj.drawString(tutorial, xSize / 2 - 26, 30, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(tutorial2, xSize / 2 - 26, 40, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(tutorial3, xSize / 2 - 26, 50, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(tutorial4, xSize / 2 - 26, 60, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(tutorial5, xSize / 2 - 26, 75, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(tutorial6, xSize / 2 - 26, 85, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(tutorial7, xSize / 2 - 26, 100, GUIUtilities.getColor(200, 200, 200));
		}else if(QUARRY.isDoneMining()) {
			fontRendererObj.drawString("Quarrying Completed!", xSize / 2 - 26, 30, GUIUtilities.getColor(200, 200, 200));
		}else{
			fontRendererObj.drawString("Current Position: ", xSize / 2 - fontRendererObj.getStringWidth("Current Position: ") / 2 + 18, 30, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(currentPosition, xSize / 2 - 26, 40, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(area, xSize / 2 - 26, 55, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(speed, xSize / 2 - 26, 65, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(blocks, xSize / 2 - 26, 75, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(energy, xSize / 2 - 26, 85, GUIUtilities.getColor(200, 200, 200));
			fontRendererObj.drawString(fortune, xSize / 2 - 26, 95, GUIUtilities.getColor(200, 200, 200));
		}

		GL11.glScalef(1/scale, 1/scale, 1/scale);
	
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.QUARRY_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    	DRAW_ITEM.drawItem(ModItems.BasicItemFilter, guiLeft, guiTop, 8, 71, zLevel);
		SIDE_TAB.drawTab();	
		POWERBAR.drawPowerBar(guiLeft + 28, guiTop + 66, 6, 60, this.zLevel);
		FLUIDBAR.drawFluidBar(guiLeft + 8, guiTop + 66, 16, 60, this.zLevel);
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


