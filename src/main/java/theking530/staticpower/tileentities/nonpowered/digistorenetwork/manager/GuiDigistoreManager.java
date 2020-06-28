package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;

public class GuiDigistoreManager extends StaticPowerTileEntityGui<ContainerDigistoreManager, TileEntityDigistoreManager> {
	// private GuiInfoTab infoTab;

	public GuiDigistoreManager(ContainerDigistoreManager container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 150);
	}

	@Override
	public void initializeGui() {
		// getTabManager().registerTab(infoTab = new GuiInfoTab(100, 65));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawPlayerInventorySlots(guiLeft + 8, guiTop + ySize - 83);

	}
}
