package theking530.staticpower.machines.esotericenchanter;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.utilities.GuiUtilities;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.machines.tileentitycomponents.PacketFluidContainerComponent;

public class GuiEsotericEnchanter extends BaseGuiContainer {
	
	private TileEsotericEnchanter esotericEnchanter;
	private GuiInfoTab infoTab;
	
	public GuiEsotericEnchanter(InventoryPlayer invPlayer, TileEsotericEnchanter enchanter) {
		super(new ContainerEsotericEnchanter(invPlayer, enchanter), 176, 166);
		esotericEnchanter = enchanter;
		
		registerWidget(new GuiPowerBarFromEnergyStorage(enchanter, 8, 50, 16, 42));
		registerWidget(new GuiFluidBarFromTank(enchanter.fluidTank, 152, 68, 16, 60));
		
		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 70));
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, enchanter));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, enchanter));
		
		GuiPowerInfoTab powerInfoTab;
		//getTabManager().registerTab(powerInfoTab = new GuiPowerInfoTab(80, 60, enchanter.getEnergyStorage()));
		//powerInfoTab.setTabSide(TabSide.LEFT);	
	}
	@Override
	public void initGui() {
		super.initGui();

	    this.buttonList.add(new ArrowButton(1, guiLeft-24, guiTop+30, 16, 10, "<"));
	    
	    if(esotericEnchanter.getFluidInteractionComponent().getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketFluidContainerComponent(esotericEnchanter.getFluidInteractionComponent().getInverseMode(), esotericEnchanter.getComponents().indexOf(esotericEnchanter.getFluidInteractionComponent()), esotericEnchanter.getPos());
			PacketHandler.net.sendToServer(msg);
			esotericEnchanter.getFluidInteractionComponent().setMode(esotericEnchanter.getFluidInteractionComponent().getInverseMode());
			
		    if(esotericEnchanter.getFluidInteractionComponent().getMode() == FluidContainerInteractionMode.FillFromContainer) {
		    	buttonList.get(0).displayString = ">";
		    }else{
		    	buttonList.get(0).displayString = "<";
		    }
		}
	}	
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.esotericEnchanter.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 8, GuiUtilities.getColor(50, 50, 50) );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3,  GuiUtilities.getColor(50, 50, 50));
	}	
	@Override
	protected void drawExtra(float f, int i, int j) {
		this.drawGenericBackground(-30, 5, 28, 60, new Color(198, 198, 198), new Color(50, 50, 50));
		this.drawGenericBackground(-30, 70, 28, 64, new Color(198, 198, 198), new Color(50, 50, 50));
		this.drawGenericBackground(0, 0, xSize, ySize, new Color(198, 198, 198), new Color(50, 50, 50));
		this.drawPlayerInventorySlots();
		
		this.drawSlot(-24+guiLeft, 11+guiTop, 16, 16);
		this.drawSlot(-24+guiLeft, 43+guiTop, 16, 16);
		
		this.drawSlot(-24+guiLeft, 76+guiTop, 16, 16);
		this.drawSlot(-24+guiLeft, 94+guiTop, 16, 16);
		this.drawSlot(-24+guiLeft, 112+guiTop, 16, 16);

		this.drawSlot(8+guiLeft, 8+guiTop, 16, 42);
		
		this.drawSlot(32+guiLeft, 35+guiTop, 16, 16);
		this.drawSlot(52+guiLeft, 35+guiTop, 16, 16);
		this.drawSlot(72+guiLeft, 35+guiTop, 16, 16);	
		
		this.drawSlot(118+guiLeft, 31+guiTop, 24, 24);
		
		this.drawSlot(8+guiLeft, 54+guiTop, 16, 16);
		
    	GuiDrawItem.drawItem(Items.BOOK, guiLeft, guiTop, 32, 35, this.zLevel, 0.3f);	
    	GuiDrawItem.drawItem(ModItems.BasicBattery, guiLeft, guiTop, 8, 54, this.zLevel, 0.3f);
    	GuiDrawItem.drawItem(ModItems.BaseFluidCapsule, guiLeft, guiTop, -24, 11, this.zLevel, 0.3f);
    	
    	int progress = esotericEnchanter.getProgressScaled(21);
    	
		drawSlot(92+guiLeft, 40+guiTop, 21, 5);
		
		GlStateManager.disableLighting();
    	drawRect(92+guiLeft, 40+guiTop, 92+guiLeft+progress, 45+guiTop, GuiUtilities.getColor(255, 255, 255));
		GlStateManager.enableLighting();
		
		infoTab.setText("Esoteric Enchanter", "The Esoteric Enchanter=can use convert=the experience stored=in liquid for into=written text.");

	}
}


