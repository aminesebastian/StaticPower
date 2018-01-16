package theking530.staticpower.machines.fluidinfuser;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
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
		this.xSize = 214;
		this.ySize = 166;		
	}	
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(Infuser.DRAIN_COMPONENT.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketFluidInfuserContainerMode(Infuser.DRAIN_COMPONENT.getInverseMode(), Infuser.getPos());
			PacketHandler.net.sendToServer(msg);
			Infuser.DRAIN_COMPONENT.setMode(Infuser.DRAIN_COMPONENT.getInverseMode());
			
		    if(Infuser.DRAIN_COMPONENT.getMode() == FluidContainerInteractionMode.FillFromContainer) {
		    	buttonList.get(0).displayString = ">";
		    }else{
		    	buttonList.get(0).displayString = "<";
		    }
		}
	}	
	
	public void updateScreen() {
		SIDE_TAB.updateTab(width+38, height, xSize, ySize, fontRenderer, Infuser);
		REDSTONE_TAB.updateTab(width+38, height, xSize, ySize, fontRenderer, Infuser);
		INFO_TAB.updateTab(width+38, height, xSize, ySize, fontRenderer, Infuser);
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
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;
		if(par1 >= 30 + var1 && par2 >= 8 + var2 && par1 <= 46 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRenderer); 
		}    
		if(par1 >= 49 + var1 && par2 >= 8 + var2 && par1 <= 56 + var1 && par2 <= 68 + var2) {
			drawHoveringText(POWERBAR.drawText(), par1, par2, fontRenderer); 
		}	
		this.renderHoveredToolTip(par1, par2);
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.Infuser.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 +6, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 27, this.ySize - 96 + 3, 4210752);
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
		POWERBAR.drawPowerBar(guiLeft + 50, guiTop + 68, 6, 60, this.zLevel, f);
		FLUIDBAR.drawFluidBar(guiLeft + 30, guiTop + 68, 16, 60, this.zLevel);
	}	
	public void progressBar() {
		int j1 = Infuser.getProgressScaled(24);
		drawTexturedModalRect(guiLeft + 97, guiTop + 32, 195, 69, j1+1, 16);	
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


