package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.tabs.GuiRedstoneTab;
import theking530.api.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiDigistoreManager extends StaticPowerTileEntityGui<ContainerDigistoreManager, TileEntityDigistoreManager> {
	private GuiInfoTab infoTab;

	public GuiDigistoreManager(ContainerDigistoreManager container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 150);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 65));
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, getTileEntity()));
		getTabManager().registerTab(new GuiSideConfigTab(80, 80, false, getTileEntity()));
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawPlayerInventorySlots(guiLeft + 8, guiTop + ySize - 83);

	}
}
