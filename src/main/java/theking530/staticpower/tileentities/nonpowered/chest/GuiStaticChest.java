package theking530.staticpower.tileentities.nonpowered.chest;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiStaticChest extends StaticPowerTileEntityGui<ContainerStaticChest, TileEntityStaticChest> {

	private GuiInfoTab infoTab;

	public GuiStaticChest(ContainerStaticChest container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 185);
		this.outputSlotSize = 16;

		Vector2D size = TileEntityStaticChest.getInventorySize(getTileEntity());
		this.setDesieredGuiSize(size.getXi(), size.getYi());
	}

	@Override
	public void initializeGui() {

	}

	@Override
	public void updateData() {

	}
}
