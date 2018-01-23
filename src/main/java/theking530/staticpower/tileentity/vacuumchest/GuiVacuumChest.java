package theking530.staticpower.tileentity.vacuumchest;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.widgets.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.utils.GuiTextures;

public class GuiVacuumChest extends BaseGuiContainer{
	
	private TileEntityVacuumChest V_CHEST;

	public GuiInfoTab INFO_TAB;
	public GuiDrawItem DRAW_ITEM = new GuiDrawItem(true);
	
	public GuiVacuumChest(InventoryPlayer invPlayer, TileEntityVacuumChest teVChest) {
		super(new ContainerVacuumChest(invPlayer, teVChest), 176, 205);
		V_CHEST = teVChest;		

		INFO_TAB = new GuiInfoTab(100, 50);
		getTabManager().registerTab(INFO_TAB);
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teVChest));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teVChest));
	}
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.V_CHEST.getName());
		this.fontRenderer.drawString(name, this.xSize - 169, 6, 4210752 );
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {	
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.VCHEST_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
    	String text = ("Vacuums items in a  =nearby radius.");
    	String[] splitMsg = text.split("=");
		
        getTabManager().drawTabs(guiLeft+175, guiTop+10, width, height, f);
    	
    	GuiDrawItem.drawItem(ModItems.BasicItemFilter, guiLeft, guiTop, 8, 78, this.zLevel, 0.5f);
		INFO_TAB.setText(V_CHEST.getBlockType().getLocalizedName(), Arrays.asList(splitMsg));
	}	
}



