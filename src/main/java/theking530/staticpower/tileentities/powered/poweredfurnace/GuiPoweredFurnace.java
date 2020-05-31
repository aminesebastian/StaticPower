package theking530.staticpower.tileentities.powered.poweredfurnace;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.GuiTextures;
import theking530.api.gui.widgets.progressbars.ArrowProgressBar;
import theking530.api.gui.widgets.tabs.GuiRedstoneTab;
import theking530.api.gui.widgets.tabs.GuiSideConfigTab;
import theking530.api.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiPoweredFurnace extends StaticPowerTileEntityGui<ContainerPoweredFurnace, TileEntityPoweredFurnace> {

	public GuiPoweredFurnace(ContainerPoweredFurnace container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity(), 8, 8, 16, 54));
		registerWidget(new ArrowProgressBar(getTileEntity(), 73, 32, 32, 16));

		getTabManager().registerTab(new GuiRedstoneTab(100, 85, getTileEntity()));
		getTabManager().registerTab(new GuiSideConfigTab(80, 80, false, getTileEntity()));
		// getTabManager().registerTab(new GuiMachinePowerInfoTab(80, 80,
		// tileEntityFurnace).setTabSide(TabSide.LEFT));
		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawContainerSlots(getTileEntity(), container.inventorySlots);
		drawPlayerInventorySlots();

		// Flames
		if (getTileEntity().isProcessing()) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.VANILLA_FURNACE_GUI);
			int k = getTileEntity().getProgressScaled(13);
			this.blit(guiLeft + 84, guiTop + 50 - k, 176, 12 - k, 14, k + 1);
		}
	}
}
