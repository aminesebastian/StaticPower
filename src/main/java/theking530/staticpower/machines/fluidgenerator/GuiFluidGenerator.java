package theking530.staticpower.machines.fluidgenerator;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.tileentitycomponents.BucketInteractionComponent.FluidContainerInteractionMode;

public class GuiFluidGenerator extends BaseGuiContainer {
	
	private TileEntityFluidGenerator fGenerator;
		
	public GuiFluidGenerator(InventoryPlayer invPlayer, TileEntityFluidGenerator teFluidGenerator) {
		super(new ContainerFluidGenerator(invPlayer, teFluidGenerator), 195, 166);
		fGenerator = teFluidGenerator;
		
		registerWidget(new GuiPowerBarFromEnergyStorage(teFluidGenerator, 50, 68, 6, 60));
		registerWidget(new GuiFluidBarFromTank(teFluidGenerator.fluidTank, 30, 68, 16, 60));
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teFluidGenerator));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teFluidGenerator));
		
		GuiPowerInfoTab powerInfoTab;
		getTabManager().registerTab(powerInfoTab = new GuiPowerInfoTab(80, 60, teFluidGenerator.getEnergyStorage()));
		powerInfoTab.setTabSide(TabSide.LEFT);	
	}
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(fGenerator.fluidContainerComponent.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketFluidGeneratorContainerMode(fGenerator.fluidContainerComponent.getInverseMode(), fGenerator.getPos());
			PacketHandler.net.sendToServer(msg);
			fGenerator.fluidContainerComponent.setMode(fGenerator.fluidContainerComponent.getInverseMode());
			
		    if(fGenerator.fluidContainerComponent.getMode() == FluidContainerInteractionMode.FillFromContainer) {
		    	buttonList.get(0).displayString = ">";
		    }else{
		    	buttonList.get(0).displayString = "<";
		    }
		}
	}	
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.fGenerator.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 +18, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 27, this.ySize - 96 + 3, 4210752);
	}	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FGENERATOR_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}


