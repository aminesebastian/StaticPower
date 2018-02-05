package theking530.staticpower.machines.basicfarmer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.assists.utilities.GuiUtilities;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.buttons.ArrowButton;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent.FluidContainerInteractionMode;

public class GuiBasicFarmer extends BaseGuiContainer{
	
	private GuiPowerBarFromEnergyStorage powerBar;
	private GuiFluidBarFromTank fluidBar;
	private TileEntityBasicFarmer tileEntityFarmer;
	private GuiInfoTab infoTab;
	
	public GuiBasicFarmer(InventoryPlayer invPlayer, TileEntityBasicFarmer teFarmer) {
		super(new ContainerBasicFarmer(invPlayer, teFarmer), 195, 172);
		tileEntityFarmer = teFarmer;
		powerBar = new GuiPowerBarFromEnergyStorage(teFarmer);
		fluidBar = new GuiFluidBarFromTank(teFarmer.TANK);
		infoTab = new GuiInfoTab(100, 100);
		
		getTabManager().registerTab(infoTab);
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teFarmer));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teFarmer));	
		getTabManager().setInitiallyOpenTab(infoTab);
	}
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new ArrowButton(1, j+7, k+35, 16, 10, "<"));
	    
	    if(tileEntityFarmer.DRAIN_COMPONENT.getMode() == FluidContainerInteractionMode.FillFromContainer) {
	    	buttonList.get(0).displayString = ">";
	    }else{
	    	buttonList.get(0).displayString = "<";
	    }
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			IMessage msg = new PacketBasicFarmerContainerMode(tileEntityFarmer.DRAIN_COMPONENT.getInverseMode(), tileEntityFarmer.getPos());
			PacketHandler.net.sendToServer(msg);
			tileEntityFarmer.DRAIN_COMPONENT.setMode(tileEntityFarmer.DRAIN_COMPONENT.getInverseMode());
		}
	}	
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    			
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;  
        if(par1 >= 26 + var1 && par2 >= 8 + var2 && par1 <= 32 + var1 && par2 <= 68 + var2) {
        	drawHoveringText(powerBar.drawText(), par1, par2, fontRenderer); 
        }
		if(par1 >= 37 + var1 && par2 >= 8 + var2 && par1 <= 53 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(fluidBar.drawText(), par1, par2, fontRenderer); 
		}    
	}	

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		super.drawGuiContainerForegroundLayer(i, j);
		//String name = I18n.format(this.FARMER.getName());
	
		//this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 + 10, 6,4210752 );
		//this.fontRenderer.drawString(I18n.format("container.inventory"), 30, this.ySize - 96 + 3, 4210752);
		
		float scale = 0.7F;
		GL11.glScalef(scale, scale, scale);
		
		String radius = "Radius: " + tileEntityFarmer.RANGE;	
		fontRenderer.drawString(radius, xSize / 2 - 31, 119, GuiUtilities.getColor(20, 20, 20));
		
		String growthBounus = "Growth Bonus Chance: " + tileEntityFarmer.GROWTH_BONUS_CHANCE + "%";	
		fontRenderer.drawString(growthBounus, xSize / 2 +39, 119, GuiUtilities.getColor(20, 20, 20));
		GL11.glScalef(1/scale, 1/scale, 1/scale);
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BASIC_FARMER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		//Energy Bar
		powerBar.drawPowerBar(guiLeft + 27, guiTop + 68, 6, 60, 1, f);
		fluidBar.drawFluidBar(guiLeft + 37, guiTop + 68, 16, 60, this.zLevel);
		
		infoTab.setText("Farmer", "Farms plants in a " + EnumTextFormatting.YELLOW +  tileEntityFarmer.RANGE + " block=radius.==Requires " + EnumTextFormatting.DARK_AQUA + "water" + EnumTextFormatting.REGULAR + " to operate=but other fluids may yield=better growth results...==Current Growth Factor: " + EnumTextFormatting.GOLD + tileEntityFarmer.GROWTH_BONUS_CHANCE + "%");
	}
}


