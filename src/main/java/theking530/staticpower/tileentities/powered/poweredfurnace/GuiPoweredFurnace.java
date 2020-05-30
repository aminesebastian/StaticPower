package theking530.staticpower.tileentities.powered.poweredfurnace;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.tabs.GuiRedstoneTab;
import theking530.api.gui.widgets.tabs.GuiSideConfigTab;
import theking530.api.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiPoweredFurnace extends StaticPowerTileEntityGui<ContainerPoweredFurnace, TileEntityPoweredFurnace> {

	private TileEntityPoweredFurnace tileEntityFurnace;

	public GuiPoweredFurnace(ContainerPoweredFurnace container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity(), 8, 62, 16, 54));
		// registerWidget(new ArrowProgressBar(tileEntityFurnace, 73, 32, 32, 16));

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
//		if (tileEntityFurnace.isProcessing()) {
//			GL11.glColor3f(1.0f, 1.0f, 1.0f);
//			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.FURNACE_GUI);
//			GuiDrawUtilities.drawTexturedModalRect(guiLeft + 51, guiTop + 50, 176, 55, 14, 14, partialTicks, partialTicks);
//		}
	}
}
