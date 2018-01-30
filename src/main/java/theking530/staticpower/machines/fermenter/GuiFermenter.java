	package theking530.staticpower.machines.fermenter;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.utilities.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;

public class GuiFermenter extends BaseGuiContainer {	

	private TileEntityFermenter FERMENTER;
	private GuiPowerBarFromEnergyStorage POWERBAR;
	private GuiFluidBarFromTank FLUIDBAR;
	
	public GuiFermenter(InventoryPlayer invPlayer, TileEntityFermenter teCropSqueezer) {
		super(new ContainerFermenter(invPlayer, teCropSqueezer), 214, 172);
		FERMENTER = teCropSqueezer;
		POWERBAR = new GuiPowerBarFromEnergyStorage(teCropSqueezer);
		FLUIDBAR = new GuiFluidBarFromTank(teCropSqueezer.TANK);
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teCropSqueezer));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teCropSqueezer));
		
	}
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		
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
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FERMENTER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if(FERMENTER.slotsInternal.getStackInSlot(0) != null) {
			int j1 = FERMENTER.getProgressScaled(32);
			//System.out.println(j1);
			FluidStack fluid = FermenterRecipeRegistry.Fermenting().getFluidResult(FERMENTER.slotsInternal.getStackInSlot(0));
			GuiFluidBar.drawFluidBar(fluid, 1000, 1000, guiLeft + 86 - j1, guiTop + 44, 1, j1, 5);
		}

		POWERBAR.drawPowerBar(guiLeft + 27, guiTop + 68, 6, 60, this.zLevel, f);
		FLUIDBAR.drawFluidBar(guiLeft + 37, guiTop + 68, 16, 60, this.zLevel);
		
		
        getTabManager().drawTabs(guiLeft+194, guiTop+10, width, height, f);
	}
}


