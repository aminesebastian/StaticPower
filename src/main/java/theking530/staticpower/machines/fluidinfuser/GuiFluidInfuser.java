package theking530.staticpower.machines.fluidinfuser;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.assists.utilities.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent.FluidContainerInteractionMode;

public class GuiFluidInfuser extends BaseGuiContainer{
	

	public GuiInfoTab INFO_TAB;
	
	private TileEntityFluidInfuser Infuser;
	private GuiPowerBarFromEnergyStorage POWERBAR;
	private GuiFluidBarFromTank FLUIDBAR;
	
	public GuiFluidInfuser(InventoryPlayer invPlayer, TileEntityFluidInfuser teInfuser) {
		super(new ContainerFluidInfuser(invPlayer, teInfuser), 195, 166);
		Infuser = teInfuser;	
		POWERBAR = new GuiPowerBarFromEnergyStorage(teInfuser);
		FLUIDBAR = new GuiFluidBarFromTank(teInfuser.TANK);
		INFO_TAB = new GuiInfoTab(100, 85);
		
		getTabManager().registerTab(INFO_TAB);
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teInfuser));
		getTabManager().registerTab(new GuiSideConfigTab(100, 80, teInfuser));	
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
	
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);

    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;
		if(par1 >= 30 + var1 && par2 >= 8 + var2 && par1 <= 46 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRenderer); 
		}    
		if(par1 >= 49 + var1 && par2 >= 8 + var2 && par1 <= 56 + var1 && par2 <= 68 + var2) {
			drawHoveringText(POWERBAR.drawText(), par1, par2, fontRenderer); 
		}	
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.Infuser.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 + 12, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 27, this.ySize - 96 + 3, 4210752);
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {		
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FLUID_INFUSER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		progressBar();
		
		int powerCost; 
		int fluidCost; 
		if(Infuser.slotsInternal.getStackInSlot(0) != ItemStack.EMPTY) {
			fluidCost = InfuserRecipeRegistry.Infusing().getInfusingFluidCost(Infuser.slotsInternal.getStackInSlot(0), Infuser.TANK.getFluid());
			powerCost = Infuser.getProcessingEnergy(Infuser.slotsInternal.getStackInSlot(0));
		}else{
			fluidCost = InfuserRecipeRegistry.Infusing().getInfusingFluidCost(Infuser.slotsInput.getStackInSlot(0), Infuser.TANK.getFluid());
			powerCost = Infuser.getProcessingEnergy(Infuser.slotsInput.getStackInSlot(0));
		}
		String power = NumberFormat.getNumberInstance(Locale.US).format(powerCost);
		String fluid = NumberFormat.getNumberInstance(Locale.US).format(fluidCost);
    	String text = ("Infuse items with the" + "=" + "power of exceptional" + "=" + "Liquids." + "=" + "=" + EnumTextFormatting.GREEN +"Power Cost: " +  power + "=" + EnumTextFormatting.AQUA +"Fluid Cost: "+ fluid + EnumTextFormatting.WHITE);

		INFO_TAB.setText(Infuser.getBlockType().getLocalizedName(), text);
		POWERBAR.drawPowerBar(guiLeft + 50, guiTop + 68, 6, 60, this.zLevel, f);
		FLUIDBAR.drawFluidBar(guiLeft + 30, guiTop + 68, 16, 60, this.zLevel);
		
		
        getTabManager().drawTabs(guiLeft+194, guiTop+10, width, height, f);
	}	
	public void progressBar() {
		int j1 = Infuser.getProgressScaled(24);
		drawTexturedModalRect(guiLeft + 97, guiTop + 32, 195, 69, j1+1, 16);	
	}	
}


