package theking530.staticpower.blockentities.nonpowered.solderingtable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiSolderingTable extends AbstractGuiSolderingTable<BlockEntitySolderingTable, ContainerSolderingTable> {

	public GuiSolderingTable(ContainerSolderingTable container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 195);
	}

	@Override
	public void updateData() {
		super.updateData();
	}
}
