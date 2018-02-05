package theking530.staticpower.tileentity.vacuumchest;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.items.ModItems;

public class GuiVacuumChest extends BaseGuiContainer{
	
	private TileEntityVacuumChest vacuumChest;

	public GuiInfoTab inforTab;
	private GuiFluidBarFromTank fluidBar;
	
	public GuiVacuumChest(InventoryPlayer invPlayer, TileEntityVacuumChest teVChest) {
		super(new ContainerVacuumChest(invPlayer, teVChest), 176, 185);
		vacuumChest = teVChest;		
		if(vacuumChest.showTank()) {
			this.xSize = 200;
		}
		fluidBar = new GuiFluidBarFromTank(vacuumChest.getTank());
		inforTab = new GuiInfoTab(100, 65);
		getTabManager().registerTab(inforTab);
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teVChest));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teVChest));
		getTabManager().setInitiallyOpenTab(inforTab);
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.vacuumChest.getName());
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
	}	
	@Override
	protected void drawExtra(float f, int i, int j) {			
		//Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.VCHEST_GUI);
		//drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		drawGenericBackground();
		drawPlayerInventorySlots(guiLeft+8, guiTop+ySize-83);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				drawSlot(guiLeft + 8 + x * 18, guiTop + 20 + y * 18, 16, 16);
			}
		}
		drawSlot(guiLeft + 8, guiTop + 78, 16, 16);
		drawSlot(guiLeft + 116, guiTop + 78, 16, 16);
		drawSlot(guiLeft + 134, guiTop + 78, 16, 16);
		drawSlot(guiLeft + 152, guiTop + 78, 16, 16);
		
		DecimalFormat format = new DecimalFormat("##.###");
    	String text = ("Vacuums items in a  =nearby radius. ==" + EnumTextFormatting.RED + "Radius: " + EnumTextFormatting.AQUA + format.format(vacuumChest.getRadius()) + " Blocks");
    	String[] splitMsg = text.split("=");
    		
    	GuiDrawItem.drawItem(ModItems.BasicItemFilter, guiLeft, guiTop, 8, 78, this.zLevel, 0.5f);
		inforTab.setText(vacuumChest.getBlockType().getLocalizedName(), Arrays.asList(splitMsg));
		
		if(!vacuumChest.showTank()) {
			xSize = 176;
		}else{
			xSize = 200;
	    	drawSlot(guiLeft+176, guiTop+12, 16, 60);
	    	fluidBar.drawFluidBar(guiLeft+176, guiTop+72, 16, 60, zLevel);
		}
	}	
}



