package theking530.staticpower.machines.poweredgrinder;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;

public class GuiPoweredGrinder extends BaseGuiContainer{
	
	private TileEntityPoweredGrinder grinderTileEntity;
	private GuiInfoTab infoTab;
	private GuiMachinePowerInfoTab powerInfoTab;
	
	public GuiPoweredGrinder(InventoryPlayer invPlayer, TileEntityPoweredGrinder teGrinder) {
		super(new ContainerPoweredGrinder(invPlayer, teGrinder), 176, 166);
		grinderTileEntity = teGrinder;
		
		registerWidget(new GuiPowerBarFromEnergyStorage(teGrinder, 8, 62, 16, 54));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 60));
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teGrinder));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teGrinder));
		
		getTabManager().registerTab(powerInfoTab = new GuiMachinePowerInfoTab(80, 80, grinderTileEntity));
		powerInfoTab.setTabSide(TabSide.LEFT);		
	}	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
		
		//drawRect(guiLeft + 82, guiTop + 38, 176, 69, 3394815);	
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String NAME = I18n.format(this.grinderTileEntity.getName());
		this.fontRenderer.drawString(NAME, this.xSize / 2 - this.fontRenderer.getStringWidth(NAME) / 2, 6, 4210752);
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.GRINDER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = grinderTileEntity.getProgressScaled(17);
		drawTexturedModalRect(guiLeft + 76, guiTop + 38, 176, 69, 24, j1);	

		String text = ("Grinds items into=their base components. ==" + "Bonus Chance: " + EnumTextFormatting.GREEN + grinderTileEntity.getBonusOutputChance() * 100.0f + "%");
		infoTab.setText(grinderTileEntity.getBlockType().getLocalizedName(), text);
	}	
}


