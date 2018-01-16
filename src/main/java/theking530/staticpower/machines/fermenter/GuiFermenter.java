	package theking530.staticpower.machines.fermenter;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
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
		this.xSize = 214;
		this.ySize = 172;		
	}
	public void updateScreen() {
		SIDE_TAB.updateTab(width+33, height, xSize, ySize, fontRenderer, FERMENTER);
		REDSTONE_TAB.updateTab(width+33, height, xSize, ySize, fontRenderer, FERMENTER);
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
		
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		int var1 = (this.width - this.xSize) / 2;
		int var2 = (this.height - this.ySize) / 2;
		if(par1 >= 26 + var1 && par2 >= 8 + var2 && par1 <= 33 + var1 && par2 <= 68 + var2) {
			drawHoveringText(POWERBAR.drawText(), par1, par2, fontRenderer); 
		}		
		if(par1 >= 36 + var1 && par2 >= 8 + var2 && par1 <= 53 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRenderer); 
		}    
		this.renderHoveredToolTip(par1, par2);
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.FERMENTER.getName());	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 + 7, 6,4210752 );
		//this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
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
			GuiFluidBar.drawFluidBar(fluid, 1000, 1000, guiLeft + 86 - j1, guiTop + 44, 1, j1, 5);
		}
			
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		POWERBAR.drawPowerBar(guiLeft + 27, guiTop + 68, 6, 60, this.zLevel, f);
		FLUIDBAR.drawFluidBar(guiLeft + 37, guiTop + 68, 16, 60, this.zLevel);
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


