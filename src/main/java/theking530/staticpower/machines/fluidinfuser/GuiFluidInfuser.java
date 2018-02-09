package theking530.staticpower.machines.fluidinfuser;

import java.text.NumberFormat;
import java.util.Locale;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.tileentitycomponents.BucketInteractionComponent.FluidContainerInteractionMode;

public class GuiFluidInfuser extends BaseGuiContainer{
	

	private GuiInfoTab infoTab;
	private TileEntityFluidInfuser infuser;
	
	public GuiFluidInfuser(InventoryPlayer invPlayer, TileEntityFluidInfuser teInfuser) {
		super(new ContainerFluidInfuser(invPlayer, teInfuser), 195, 166);
		infuser = teInfuser;	
		
		registerWidget(new GuiPowerBarFromEnergyStorage(teInfuser, 50, 68, 6, 60));
		registerWidget(new GuiFluidBarFromTank(teInfuser.TANK, 30, 68, 16, 60));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 85));
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teInfuser));
		getTabManager().registerTab(new GuiSideConfigTab(100, 80, teInfuser));	
		
		GuiMachinePowerInfoTab powerInfoTab;
		getTabManager().registerTab(powerInfoTab = new GuiMachinePowerInfoTab(80, 80, teInfuser));
		powerInfoTab.setTabSide(TabSide.LEFT);	
	}	
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(infuser.DRAIN_COMPONENT.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketFluidInfuserContainerMode(infuser.DRAIN_COMPONENT.getInverseMode(), infuser.getPos());
			PacketHandler.net.sendToServer(msg);
			infuser.DRAIN_COMPONENT.setMode(infuser.DRAIN_COMPONENT.getInverseMode());
			
		    if(infuser.DRAIN_COMPONENT.getMode() == FluidContainerInteractionMode.FillFromContainer) {
		    	buttonList.get(0).displayString = ">";
		    }else{
		    	buttonList.get(0).displayString = "<";
		    }
		}
	}	

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.infuser.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 + 12, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 27, this.ySize - 96 + 3, 4210752);
	}	
	@Override
	protected void drawExtra(float f, int i, int j) {		
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FLUID_INFUSER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		progressBar();
		
		int powerCost; 
		int fluidCost; 
		if(infuser.slotsInternal.getStackInSlot(0) != ItemStack.EMPTY) {
			fluidCost = InfuserRecipeRegistry.Infusing().getInfusingFluidCost(infuser.slotsInternal.getStackInSlot(0), infuser.TANK.getFluid());
			powerCost = infuser.getProcessingEnergy(infuser.slotsInternal.getStackInSlot(0));
		}else{
			fluidCost = InfuserRecipeRegistry.Infusing().getInfusingFluidCost(infuser.slotsInput.getStackInSlot(0), infuser.TANK.getFluid());
			powerCost = infuser.getProcessingEnergy(infuser.slotsInput.getStackInSlot(0));
		}
		String power = NumberFormat.getNumberInstance(Locale.US).format(powerCost);
		String fluid = NumberFormat.getNumberInstance(Locale.US).format(fluidCost);
    	String text = ("Infuse items with the" + "=" + "power of exceptional" + "=" + "Liquids." + "=" + "=" + EnumTextFormatting.GREEN +"Power Cost: " +  power + "=" + EnumTextFormatting.AQUA +"Fluid Cost: "+ fluid + EnumTextFormatting.WHITE);

		infoTab.setText(infuser.getBlockType().getLocalizedName(), text);
	}	
	public void progressBar() {
		int j1 = infuser.getProgressScaled(24);
		drawTexturedModalRect(guiLeft + 97, guiTop + 32, 195, 69, j1+1, 16);	
	}	
}


