package theking530.staticpower.machines.condenser;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.utils.GuiTextures;

public class GuiCondenser extends BaseGuiContainer{
		
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	private GuiFluidBarFromTank FLUIDBAR;
	private GuiFluidBarFromTank FLUIDBAR2;
	private TileEntityCondenser DISTILLERY;
	
	public GuiCondenser(InventoryPlayer invPlayer, TileEntityCondenser teFluidGenerator) {
		super(new ContainerCondenser(invPlayer, teFluidGenerator), 214, 173);
		DISTILLERY = teFluidGenerator;
		FLUIDBAR = new GuiFluidBarFromTank(teFluidGenerator.TANK);
		FLUIDBAR2 = new GuiFluidBarFromTank(teFluidGenerator.TANK2);
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 100, teFluidGenerator));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teFluidGenerator));	
	}
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(DISTILLERY.DRAIN_COMPONENT_EVAPORATED_MASH.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketCondenserContainerMode(DISTILLERY.DRAIN_COMPONENT_EVAPORATED_MASH.getInverseMode(), DISTILLERY.getPos());
			PacketHandler.net.sendToServer(msg);
			DISTILLERY.DRAIN_COMPONENT_EVAPORATED_MASH.setMode(DISTILLERY.DRAIN_COMPONENT_EVAPORATED_MASH.getInverseMode());
			
		    if(DISTILLERY.DRAIN_COMPONENT_EVAPORATED_MASH.getMode() == FluidContainerInteractionMode.FillFromContainer) {
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
				
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		if(par1 >= 71 + var1 && par2 >= 16 + var2 && par1 <= 87 + var1 && par2 <= 79 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRenderer); 
		}     
		if(par1 >= 127 + var1 && par2 >= 16 + var2 && par1 <= 142 + var1 && par2 <= 79 + var2) {	
			drawHoveringText(FLUIDBAR2.drawText(), par1, par2, fontRenderer); 
		}	    
		this.renderHoveredToolTip(par1, par2);
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.DISTILLERY.getName());
	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.CONDENSER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(DISTILLERY.PROCESSING_STACK != null) {
			int j1 = DISTILLERY.getProgressScaled(34);
			GuiFluidBar.drawFluidBar(DISTILLERY.PROCESSING_STACK, 1000, 1000, guiLeft + 90, guiTop + 49, 1, j1, 5);
		}
		
        getTabManager().drawTabs(guiLeft+175, guiTop+10, width, height, f);
        
		FLUIDBAR.drawFluidBar(guiLeft + 71, guiTop + 77, 16, 60, this.zLevel);
		FLUIDBAR2.drawFluidBar(guiLeft + 127, guiTop + 77, 16, 60, this.zLevel);
	}
}


