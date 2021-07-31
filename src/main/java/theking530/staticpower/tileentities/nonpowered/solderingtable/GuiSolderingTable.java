package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiSolderingTable extends AbstractGuiSolderingTable<TileEntitySolderingTable, ContainerSolderingTable> {

	public GuiSolderingTable(ContainerSolderingTable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void updateData() {
		super.updateData();
	}
}
