	package theking530.staticpower.machines.fermenter;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;
import theking530.staticpower.utils.GuiTextures;

public class GuiFermenter extends GuiContainer{	

	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	
	private TileEntityFermenter FERMENTER;
	private GuiPowerBarFromEnergyStorage POWERBAR;
	private GuiFluidBarFromTank FLUIDBAR;
	private static ContainerFermenter CONTAINER;
	
	public GuiFermenter(InventoryPlayer invPlayer, TileEntityFermenter teCropSqueezer) {
		super(CONTAINER = new ContainerFermenter(invPlayer, teCropSqueezer));
		FERMENTER = teCropSqueezer;
		POWERBAR = new GuiPowerBarFromEnergyStorage(teCropSqueezer);
		FLUIDBAR = new GuiFluidBarFromTank(teCropSqueezer.TANK);
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teCropSqueezer);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teCropSqueezer);
		this.xSize = 176;
		this.ySize = 166;		
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, FERMENTER);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, FERMENTER);
		CONTAINER.moveUpgradeSlots(100, 100);
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
		if(par1 >= 18 + var1 && par2 >= 8 + var2 && par1 <= 34 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRendererObj); 
		}    
		if(par1 >= 8 + var1 && par2 >= 8 + var2 && par1 <= 14 + var1 && par2 <= 68 + var2) {
			drawHoveringText(POWERBAR.drawText(), par1, par2, fontRendererObj); 
		}		
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.FERMENTER.getName());	
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2 + 7, 6,4210752 );
		//this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FERMENTER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if(FERMENTER.SLOTS_INTERNAL.getStackInSlot(0) != null) {
			int j1 = FERMENTER.getProgressScaled(32);
			//System.out.println(j1);
			FluidStack fluid = FermenterRecipeRegistry.Fermenting().getFluidResult(FERMENTER.SLOTS_INTERNAL.getStackInSlot(0));
			GuiFluidBar.drawFluidBar(fluid, 1000, 1000, guiLeft + 67 - j1, guiTop + 44, 1, j1, 5);
		}
			
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		POWERBAR.drawPowerBar(guiLeft + 8, guiTop + 68, 6, 60, this.zLevel);
		FLUIDBAR.drawFluidBar(guiLeft + 18, guiTop + 68, 16, 60, this.zLevel);
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


