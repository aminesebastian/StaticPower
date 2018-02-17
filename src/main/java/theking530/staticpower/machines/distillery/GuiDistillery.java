package theking530.staticpower.machines.distillery;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiHeatBarFromStorage;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.machines.tileentitycomponents.PacketFluidContainerComponent;

public class GuiDistillery extends BaseGuiContainer {

	private GuiHeatBarFromStorage heatbar;
	private TileEntityDistillery distillery;
	
	public GuiDistillery(InventoryPlayer invPlayer, TileEntityDistillery teFluidGenerator) {
		super(new ContainerDistillery(invPlayer, teFluidGenerator), 214, 173);
		distillery = teFluidGenerator;
		
		heatbar = new GuiHeatBarFromStorage(teFluidGenerator.HEAT_STORAGE);
		registerWidget(new GuiFluidBarFromTank(teFluidGenerator.fluidTank, 71, 77, 16, 60));
		registerWidget(new GuiFluidBarFromTank(teFluidGenerator.TANK2, 127, 77, 16, 60));

		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teFluidGenerator));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teFluidGenerator));
	}
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(distillery.DRAIN_COMPONENT_MASH.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketFluidContainerComponent(distillery.DRAIN_COMPONENT_MASH.getInverseMode(), distillery.getComponents().indexOf(distillery.DRAIN_COMPONENT_MASH), distillery.getPos());
			PacketHandler.net.sendToServer(msg);
			distillery.DRAIN_COMPONENT_MASH.setMode(distillery.DRAIN_COMPONENT_MASH.getInverseMode());
			
		    if(distillery.DRAIN_COMPONENT_MASH.getMode() == FluidContainerInteractionMode.FillFromContainer) {
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
		if(par1 >= 71 + var1 && par2 >= 80 + var2 && par1 <= 87 + var1 && par2 <= 88 + var2) {	
			drawHoveringText(heatbar.drawText(), par1, par2, fontRenderer); 
		}	  
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.distillery.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {   	
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.DISTILLERY_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(distillery.PROCESSING_STACK != null) {
			GuiFluidBar.drawFluidBar(new FluidStack(ModFluids.Mash, 100), 100, 100, guiLeft + 90, guiTop + 49, 1, 20, 5);
		}
		heatbar.drawHeatBar(guiLeft + 71, guiTop + 88, this.zLevel, 16, 8);
	}
}


