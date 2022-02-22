package theking530.staticpower.tileentities.nonpowered.randomitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiRandomItemGenerator extends StaticPowerTileEntityGui<ContainerRandomItemGenerator, TileEntityRandomItemGenerator> {

	public GuiRandomItemGenerator(ContainerRandomItemGenerator container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		setOutputSlotSize(16);
	}

	@Override
	public void updateData() {
	}
}
