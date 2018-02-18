package theking530.staticpower.machines.condenser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.machines.tileentitycomponents.PacketFluidContainerComponent;

public class GuiCondenser extends BaseGuiContainer{
		
	private TileEntityCondenser condenser;
	
	public GuiCondenser(InventoryPlayer invPlayer, TileEntityCondenser teFluidGenerator) {
		super(new ContainerCondenser(invPlayer, teFluidGenerator), 214, 173);
		condenser = teFluidGenerator;
		
		registerWidget(new GuiFluidBarFromTank(teFluidGenerator.fluidTank, 71, 77, 16, 60));
		registerWidget(new GuiFluidBarFromTank(teFluidGenerator.TANK2, 127, 77, 16, 60));
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teFluidGenerator));
		getTabManager().registerTab(new GuiSideConfigTab(80, 80, teFluidGenerator));	
	}
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(condenser.DRAIN_COMPONENT_EVAPORATED_MASH.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketFluidContainerComponent(condenser.DRAIN_COMPONENT_EVAPORATED_MASH.getInverseMode(), condenser.getComponents().indexOf(condenser.DRAIN_COMPONENT_EVAPORATED_MASH), condenser.getPos());
			PacketHandler.net.sendToServer(msg);
			condenser.DRAIN_COMPONENT_EVAPORATED_MASH.setMode(condenser.DRAIN_COMPONENT_EVAPORATED_MASH.getInverseMode());
			
		    if(condenser.DRAIN_COMPONENT_EVAPORATED_MASH.getMode() == FluidContainerInteractionMode.FillFromContainer) {
		    	buttonList.get(0).displayString = ">";
		    }else{
		    	buttonList.get(0).displayString = "<";
		    }
		}
	}	
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.condenser.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
	}
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.CONDENSER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(condenser.PROCESSING_STACK != null) {
			int j1 = condenser.getProgressScaled(34);
			GuiFluidBarUtilities.drawFluidBar(condenser.PROCESSING_STACK, 1000, 1000, guiLeft + 90, guiTop + 49, 1, j1, 5);
		};
	}
}


