package theking530.staticpower.machines.fluidinfuser;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.GuiPowerBar;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GuiTextures;

public class GuiFluidInfuser extends GuiContainer{
	
	
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	public GuiInfoTab INFO_TAB = new GuiInfoTab(guiLeft, guiTop);
	
	private TileEntityFluidInfuser Infuser;
	private GuiPowerBarFromEnergyStorage POWERBAR;
	private GuiFluidBarFromTank FLUIDBAR;
	
	public GuiFluidInfuser(InventoryPlayer invPlayer, TileEntityFluidInfuser teInfuser) {
		super(new ContainerFluidInfuser(invPlayer, teInfuser));
		Infuser = teInfuser;	
		POWERBAR = new GuiPowerBarFromEnergyStorage(teInfuser);
		FLUIDBAR = new GuiFluidBarFromTank(teInfuser.TANK);
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teInfuser);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teInfuser);
		this.xSize = 176;
		this.ySize = 166;		
	}	
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, Infuser);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, Infuser);
		INFO_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, Infuser);
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
		if(par1 >= 11 + var1 && par2 >= 8 + var2 && par1 <= 27 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRendererObj); 
		}    
		if(par1 >= 31 + var1 && par2 >= 8 + var2 && par1 <= 37 + var1 && par2 <= 68 + var2) {
			drawHoveringText(POWERBAR.drawText(), par1, par2, fontRendererObj); 
		}	
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.Infuser.getName());
	
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6,4210752 );
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.INFUSER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		progressBar();
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		int powerCost; 
		int fluidCost; 
		if(Infuser.SLOTS_INTERNAL.getStackInSlot(0) != null) {
			fluidCost = InfuserRecipeRegistry.Infusing().getInfusingFluidCost(Infuser.SLOTS_INTERNAL.getStackInSlot(0), Infuser.TANK.getFluid());
			powerCost = Infuser.getProcessingEnergy(Infuser.SLOTS_INTERNAL.getStackInSlot(0));
		}else{
			fluidCost = InfuserRecipeRegistry.Infusing().getInfusingFluidCost(Infuser.SLOTS_INPUT.getStackInSlot(0), Infuser.TANK.getFluid());
			powerCost = Infuser.getProcessingEnergy(Infuser.SLOTS_INPUT.getStackInSlot(0));
		}
		String power = NumberFormat.getNumberInstance(Locale.US).format(powerCost);
		String fluid = NumberFormat.getNumberInstance(Locale.US).format(fluidCost);
    	String text = ("Infuse items with the" + "=" + "power of Energized" + "=" + "Liquids." + "=" + "=" + EnumTextFormatting.GREEN +"Power Cost: " +  power + "=" + EnumTextFormatting.AQUA +"Fluid Cost: "+ fluid + EnumTextFormatting.WHITE);
    	String[] splitMsg = text.split("=");
		
		INFO_TAB.drawTab(Arrays.asList(splitMsg));
		POWERBAR.drawPowerBar(guiLeft + 31, guiTop + 68, 6, 60, this.zLevel);
		FLUIDBAR.drawFluidBar(guiLeft + 11, guiTop + 68, 16, 60, this.zLevel);
	}	
	public void progressBar() {
		int j1 = Infuser.getProgressScaled(24);
		drawTexturedModalRect(guiLeft + 78, guiTop + 32, 176, 69, j1 + 1, 16);	
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


