package theking530.staticpower.machines.basicfarmer;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.utils.GUIUtilities;
import theking530.staticpower.utils.GuiTextures;

public class GuiBasicFarmer extends GuiContainer{
	
	public GuiPowerBarFromEnergyStorage POWER_BAR;
	private GuiFluidBarFromTank FLUIDBAR;
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;	
	private TileEntityBasicFarmer FARMER;
	
	public GuiBasicFarmer(InventoryPlayer invPlayer, TileEntityBasicFarmer teFarmer) {
		super(new ContainerBasicFarmer(invPlayer, teFarmer));
		FARMER = teFarmer;
		POWER_BAR = new GuiPowerBarFromEnergyStorage(teFarmer);
		FLUIDBAR = new GuiFluidBarFromTank(teFarmer.TANK);
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teFarmer);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teFarmer);
		this.xSize = 195;
		this.ySize = 172;
		
	}
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(FARMER.DRAIN_COMPONENT.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketBasicFarmerContainerMode(FARMER.DRAIN_COMPONENT.getInverseMode(), FARMER.getPos());
			PacketHandler.net.sendToServer(msg);
			FARMER.DRAIN_COMPONENT.setMode(FARMER.DRAIN_COMPONENT.getInverseMode());
			
		    if(FARMER.DRAIN_COMPONENT.getMode() == FluidContainerInteractionMode.FillFromContainer) {
		    	buttonList.get(0).displayString = ">";
		    }else{
		    	buttonList.get(0).displayString = "<";
		    }
		}
	}	
	
	public void updateScreen() {
		SIDE_TAB.updateTab(width+37, height, xSize, ySize, fontRenderer, FARMER);
		REDSTONE_TAB.updateTab(width+37, height, xSize, ySize, fontRenderer, FARMER);
		if(SIDE_TAB.GROWTH_STATE == 1){
			REDSTONE_TAB.RED_TAB.GROWTH_STATE = 2;
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
        if(par1 >= 26 + var1 && par2 >= 8 + var2 && par1 <= 32 + var1 && par2 <= 68 + var2) {
        	drawHoveringText(POWER_BAR.drawText(), par1, par2, fontRenderer); 
        }
		if(par1 >= 37 + var1 && par2 >= 8 + var2 && par1 <= 53 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRenderer); 
		}    
		this.renderHoveredToolTip(par1, par2);
	}	

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		//String name = I18n.format(this.FARMER.getName());
	
		//this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 + 10, 6,4210752 );
		//this.fontRenderer.drawString(I18n.format("container.inventory"), 30, this.ySize - 96 + 3, 4210752);
		
		float scale = 0.7F;
		GL11.glScalef(scale, scale, scale);
		
		String radius = "Radius: " + FARMER.RANGE;	
		fontRenderer.drawString(radius, xSize / 2 - 31, 119, GUIUtilities.getColor(20, 20, 20));
		
		String growthBounus = "Growth Bonus Chance: " + (int)(((1f/(float)FARMER.GROWTH_BONUS_CHANCE))*100) + "%";	
		fontRenderer.drawString(growthBounus, xSize / 2 +39, 119, GUIUtilities.getColor(20, 20, 20));
		GL11.glScalef(1/scale, 1/scale, 1/scale);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BASIC_FARMER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		//Tabs
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();		
		//Energy Bar
		POWER_BAR.drawPowerBar(guiLeft + 27, guiTop + 68, 6, 60, 1, f);
		FLUIDBAR.drawFluidBar(guiLeft + 37, guiTop + 68, 16, 60, this.zLevel);
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


