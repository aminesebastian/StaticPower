package theking530.staticpower.tileentity.barrel;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.assists.utilities.GuiUtilities;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;

public class GuiBarrel extends BaseGuiContainer {
	
	private TileEntityBarrel barrel;
	private GuiInfoTab infoTab;
	
	public GuiBarrel(InventoryPlayer invPlayer, TileEntityBarrel teBarrel) {
		super(new ContainerBarrel(invPlayer, teBarrel), 176, 150);
		barrel = teBarrel;	
		
		infoTab = new GuiInfoTab(100, 65);
		getTabManager().registerTab(infoTab);
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, barrel));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, barrel));
		getTabManager().setInitiallyOpenTab(infoTab);
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(barrel.getName());
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6,4210752 );
	}	
	@Override
	protected void drawExtra(float f, int i, int j) {			
		drawGenericBackground();
		drawPlayerInventorySlots(guiLeft+8, guiTop+ySize-83);

		drawSlot(guiLeft + 76, guiTop + 21, 24, 24);
		
    	GuiDrawItem.drawItem(barrel.getStoredItem(), guiLeft, guiTop, 80, 25, this.zLevel, 1.0f);
    	
    	String text2 = "" + barrel.getStoredAmount();
    	float scale = 0.6f;
    	
		int textX = (int)((guiLeft + 100 - fontRenderer.getStringWidth(text2) * scale) / scale) - 1;
		int textY = (int)((guiTop + 45 - 7 * scale) / scale) - 1;
		
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		
		fontRenderer.drawStringWithShadow(text2, textX, textY, GuiUtilities.getColor(255, 255, 255));
		
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
    	
		DecimalFormat format = new DecimalFormat("##.###");
    	String text = ("Stores a large=amount of a single=item. ==" + EnumTextFormatting.RED + "Max: " + EnumTextFormatting.AQUA + format.format(barrel.getMaxStoredAmount()) + " Items");
    	String[] splitMsg = text.split("=");
		infoTab.setText(barrel.getBlockType().getLocalizedName(), Arrays.asList(splitMsg));
	}	
}



