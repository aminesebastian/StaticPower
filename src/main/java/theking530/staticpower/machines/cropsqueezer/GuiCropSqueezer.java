	package theking530.staticpower.machines.cropsqueezer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.utils.GuiTextures;

public class GuiCropSqueezer extends BaseGuiContainer {	

	private TileEntityCropSqueezer cSqueezer;
	private GuiPowerBarFromEnergyStorage POWERBAR;
	private GuiFluidBarFromTank FLUIDBAR;
	
	public GuiCropSqueezer(InventoryPlayer invPlayer, TileEntityCropSqueezer teCropSqueezer) {
		super(new ContainerCropSqueezer(invPlayer, teCropSqueezer), 195, 166);
		cSqueezer = teCropSqueezer;
		POWERBAR = new GuiPowerBarFromEnergyStorage(teCropSqueezer);
		FLUIDBAR = new GuiFluidBarFromTank(teCropSqueezer.TANK);
		
		getTabManager().registerTab(new GuiRedstoneTab(100, 100, teCropSqueezer));
		getTabManager().registerTab(new GuiSideConfigTab(100, 100, teCropSqueezer));

		this.xSize = 195;
		this.ySize = 166;		
	}

	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		int var1 = (this.width - this.xSize) / 2;
		int var2 = (this.height - this.ySize) / 2;
		if(par1 >= 30 + var1 && par2 >= 8 + var2 && par1 <= 46 + var1 && par2 <= 68 + var2) {	
			drawHoveringText(FLUIDBAR.drawText(), par1, par2, fontRenderer); 
		}
		if(par1 >= 50 + var1 && par2 >= 8 + var2 && par1 <= 56 + var1 && par2 <= 68 + var2) {
			drawHoveringText(POWERBAR.drawText(), par1, par2, fontRenderer); 
		}		
		this.renderHoveredToolTip(par1, par2);
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.cSqueezer.getName());	
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2 +18, 6,4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 27, this.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.SQUEEZER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		int j1 = cSqueezer.getProgressScaled(15);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.MSQUEEZER_GUI);
		drawTexturedModalRect(guiLeft + 107, guiTop + 33, 198, 71, 14, 1+j1);
			
		
        getTabManager().drawTabs(guiLeft+175, guiTop+10, width, height, f);
		
		POWERBAR.drawPowerBar(guiLeft + 50, guiTop + 68, 6, 60, this.zLevel, f);
		FLUIDBAR.drawFluidBar(guiLeft + 30, guiTop + 68, 16, 60, this.zLevel);
	}
}


