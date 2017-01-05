package theking530.staticpower.machines.condenser;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiHeatBarFromStorage;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;
import theking530.staticpower.machines.fluidgenerator.PacketFluidGeneratorContainerMode;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
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
		this.xSize = 214;
		this.ySize = 173;
		
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
	
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		SIDE_TAB.updateTab(width+33, height+80, xSize, ySize, fontRendererObj, DISTILLERY);
		REDSTONE_TAB.updateTab(width+33, height+80, xSize, ySize, fontRendererObj, DISTILLERY);
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
		if(par1 >= 71 + var1 && par2 >= 16 + var2 && par1 <= 87 + var1 && par2 <= 79 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRendererObj); 
		}     
		if(par1 >= 127 + var1 && par2 >= 16 + var2 && par1 <= 142 + var1 && par2 <= 79 + var2) {	
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
			GuiFluidBar.drawFluidBar(DISTILLERY.PROCESSING_STACK, 1000, 1000, guiLeft + 90, guiTop + 49, 1, j1, 5);
		}
		SIDE_TAB.drawTab();	
		REDSTONE_TAB.drawTab();

		FLUIDBAR.drawFluidBar(guiLeft + 71, guiTop + 77, 16, 60, this.zLevel);
		FLUIDBAR2.drawFluidBar(guiLeft + 127, guiTop + 77, 16, 60, this.zLevel);
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


