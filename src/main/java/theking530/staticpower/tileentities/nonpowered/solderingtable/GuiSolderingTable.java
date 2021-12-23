package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

public class GuiSolderingTable extends AbstractGuiSolderingTable<TileEntitySolderingTable, ContainerSolderingTable> {

	public GuiSolderingTable(ContainerSolderingTable container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void updateData() {
		super.updateData();
	}
}
