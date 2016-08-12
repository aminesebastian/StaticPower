package theking530.staticpower.machines.poweredgrinder;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.CustomGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.utils.GuiTextures;

public class GuiPoweredGrinder extends CustomGuiContainer{
	
	public GuiSideConfigTab SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, ModBlocks.PoweredGrinder);
	public GuiRedstoneTab REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop);
	private GuiPowerBarFromEnergyStorage POWERBAR;
	
	private TileEntityPoweredGrinder Grinder;
	public GuiPoweredGrinder(InventoryPlayer invPlayer, TileEntityPoweredGrinder teGrinder) {
		super(new ContainerPoweredGrinder(invPlayer, teGrinder));
		Grinder = teGrinder;
		POWERBAR = new GuiPowerBarFromEnergyStorage(teGrinder);
		
		this.xSize = 176;
		this.ySize = 166;
		
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, Grinder);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, Grinder);
		if(SIDE_TAB.GROWTH_STATE == 1){
			REDSTONE_TAB.RED_TAB.GROWTH_STATE = 2;
		}
		if(REDSTONE_TAB.GROWTH_STATE == 1) {
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}
	}	
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
        if(par1 >= 8 + var1 && par2 >= 8 + var2 && par1 <= 24 + var1 && par2 <= 68 + var2) {
        	int j1 = Grinder.STORAGE.getEnergyStored();
        	int k1 = Grinder.STORAGE.getMaxEnergyStored();
        	int i1 = Grinder.STORAGE.getMaxReceive();
        	String text = ("Max: " + i1 + " RF/t" + "=" + NumberFormat.getNumberInstance(Locale.US).format(j1)  + "/" + NumberFormat.getNumberInstance(Locale.US).format(k1) + " " + "RF");
        	String[] splitMsg = text.split("=");
        	List temp = Arrays.asList(splitMsg);
        	drawHoveringText(temp, par1, par2, fontRendererObj); 
        }

		drawRect(guiLeft + 82, guiTop + 38, 176, 69, 3394815);

	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.Grinder.hasCustomName() ? this.Grinder.getName() : I18n.format(this.Grinder.getName());
	
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6,4210752 );
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.GRINDER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = Grinder.getProgressScaled(17);
		drawTexturedModalRect(guiLeft + 76, guiTop + 38, 176, 69, 24, j1);	
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    REDSTONE_TAB.mouseInteraction(x, y, button);
	    SIDE_TAB.mouseInteraction(x, y, button);
	}	
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		SIDE_TAB.mouseDrag(x, y, button, time);
	}	
}


