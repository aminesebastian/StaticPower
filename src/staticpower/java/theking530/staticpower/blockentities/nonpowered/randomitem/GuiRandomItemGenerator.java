package theking530.staticpower.blockentities.nonpowered.randomitem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;

public class GuiRandomItemGenerator
		extends StaticCoreBlockEntityScreen<ContainerRandomItemGenerator, BlockEntityRandomItemGenerator> {

	public GuiRandomItemGenerator(ContainerRandomItemGenerator container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		setOutputSlotSize(16);
	}

	@Override
	public void updateData() {
	}
}
