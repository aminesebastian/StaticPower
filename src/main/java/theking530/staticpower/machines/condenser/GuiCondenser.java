package theking530.staticpower.machines.condenser;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.client.gui.widgets.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.GuiHeatBarFromStorage;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;
import theking530.staticpower.utils.GuiTextures;

public class GuiCondenser extends GuiContainer{
		
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	private GuiFluidBarFromTank FLUIDBAR;
	private GuiFluidBarFromTank FLUIDBAR2;
	private TileEntityCondenser DISTILLERY;
	
	public GuiCondenser(InventoryPlayer invPlayer, TileEntityCondenser teFluidGenerator) {
		super(new ContainerCondenser(invPlayer, teFluidGenerator));
		DISTILLERY = teFluidGenerator;
		FLUIDBAR = new GuiFluidBarFromTank(teFluidGenerator.TANK);
		FLUIDBAR2 = new GuiFluidBarFromTank(teFluidGenerator.TANK2);
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teFluidGenerator);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teFluidGenerator);
		this.xSize = 176;
		this.ySize = 173;
		
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, DISTILLERY);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, DISTILLERY);
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
		if(par1 >= 52 + var1 && par2 >= 16 + var2 && par1 <= 68 + var1 && par2 <= 79 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRendererObj); 
		}     
		if(par1 >= 108 + var1 && par2 >= 16 + var2 && par1 <= 123 + var1 && par2 <= 79 + var2) {	
			drawHoveringText(FLUIDBAR2.drawText(), par1, par2, fontRendererObj); 
		}	    
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.DISTILLERY.getName());
	
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6,4210752 );
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		FluidStack fluidStack = DISTILLERY.TANK.getFluid();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.CONDENSER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(DISTILLERY.PROCESSING_STACK != null) {
			int j1 = DISTILLERY.getProgressScaled(34);
			GuiFluidBar.drawFluidBar(DISTILLERY.PROCESSING_STACK, 1000, 1000, guiLeft + 71, guiTop + 49, 1, j1, 5);
		}
		
		REDSTONE_TAB.drawTab();
		SIDE_TAB.drawTab();	
		FLUIDBAR.drawFluidBar(guiLeft + 52, guiTop + 77, 16, 60, this.zLevel);
		FLUIDBAR2.drawFluidBar(guiLeft + 108, guiTop + 77, 16, 60, this.zLevel);
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


