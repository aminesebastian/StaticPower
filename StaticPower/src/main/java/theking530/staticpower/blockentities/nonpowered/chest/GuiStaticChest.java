package theking530.staticpower.blockentities.nonpowered.chest;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.utilities.math.Vector2D;

public class GuiStaticChest extends StaticCoreBlockEntityScreen<ContainerStaticChest, BlockEntityStaticChest> {

	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiStaticChest(ContainerStaticChest container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 185);
		this.outputSlotSize = 16;

		Vector2D size = BlockEntityStaticChest.getInventorySize(getTileEntity());
		this.setDesieredGuiSize(size.getXi(), size.getYi());
	}

	@Override
	public void initializeGui() {

	}

	@Override
	public void updateData() {

	}
}
