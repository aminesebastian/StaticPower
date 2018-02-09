package theking530.staticpower.machines.former;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.items.ModItems;

public class GuiFormer extends BaseGuiContainer{

	private TileEntityFormer former;
	private GuiInfoTab infoTab;
	
	public GuiFormer(InventoryPlayer invPlayer, TileEntityFormer tileEntityFormer) {
		super(new ContainerFormer(invPlayer, tileEntityFormer), 176, 166);
		former = tileEntityFormer;
		registerWidget(new GuiPowerBarFromEnergyStorage(tileEntityFormer, 8, 62, 16, 54));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 60));
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, tileEntityFormer));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, tileEntityFormer));
		
		GuiMachinePowerInfoTab powerInfoTab;
		getTabManager().registerTab(powerInfoTab = new GuiMachinePowerInfoTab(80, 80, tileEntityFormer));
		powerInfoTab.setTabSide(TabSide.LEFT);	
	}	
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String NAME = I18n.format(this.former.getName());
		this.fontRenderer.drawString(NAME, this.xSize / 2 - this.fontRenderer.getStringWidth(NAME) / 2, 6, 4210752);
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.FORMER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int j1 = former.getProgressScaled(24);
		drawTexturedModalRect(guiLeft + 82, guiTop + 35, 176, 69, j1, 16);	
		
		GuiDrawItem.drawItem(ModItems.PlateMould, guiLeft, guiTop, 37, 34, zLevel, 0.3f);			

		String formerInfo = "The former transforms=items into other items=by shaping them against=moulds.";
		infoTab.setText("Former", formerInfo);
	}	
}


