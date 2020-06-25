package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.tabs.GuiSideConfigTab;
import theking530.api.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiTank extends StaticPowerTileEntityGui<ContainerTank, TileEntityTank> {
	private GuiInfoTab infoTab;

	public GuiTank(ContainerTank container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 72, 20, 32, 74));
		tabManager.registerTab(infoTab = new GuiInfoTab(100, 65));

		getTabManager().registerTab(new GuiSideConfigTab(true, getTileEntity()));
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);
		infoTab.setText(getTitle().getFormattedText(), "test");
	}
}
