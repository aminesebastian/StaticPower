package theking530.staticpower.tileentities.powered.pump;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiPump extends StaticPowerTileEntityGui<ContainerPump, TileEntityPump> {
	private GuiInfoTab infoTab;

	public GuiPump(ContainerPump container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 44, 20, 32, 74));
		registerWidget(new ArrowProgressBar(84, 31, 32, 16));
		tabManager.registerTab(infoTab = new GuiInfoTab(100, 65));

		getTabManager().registerTab(new GuiSideConfigTab(true, getTileEntity()));
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);
		infoTab.setText(getTitle().getFormattedText(), "test");
	}
}
