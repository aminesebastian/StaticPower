	package theking530.staticpower.machines.mechanicalsqueezer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;

public class GuiMechanicalSqueezer extends BaseGuiContainer{

	private TileEntityMechanicalSqueezer cSqueezer;

	public GuiMechanicalSqueezer(InventoryPlayer invPlayer, TileEntityMechanicalSqueezer teCropSqueezer) {
		super(new ContainerMechanicalSqueezer(invPlayer, teCropSqueezer), 195, 166);
		cSqueezer = teCropSqueezer;
		registerWidget(new GuiFluidBarFromTank(teCropSqueezer.fluidTank, 30, 68, 16, 60));

		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teCropSqueezer));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teCropSqueezer));
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.cSqueezer.getName());	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 + 18, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 27, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawExtra(float f, int i, int j) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.MSQUEEZER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = cSqueezer.getProgressScaled(15);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.MSQUEEZER_GUI);
		drawTexturedModalRect(guiLeft + 107, guiTop + 34, 198, 71, 14, 1+j1);
	}
}


