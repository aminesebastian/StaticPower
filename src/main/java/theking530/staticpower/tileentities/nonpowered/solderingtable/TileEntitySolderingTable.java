package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderSolderingTable;
import theking530.staticpower.init.ModBlocks;

public class TileEntitySolderingTable extends AbstractSolderingTable {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntitySolderingTable> TYPE = new TileEntityTypeAllocator<TileEntitySolderingTable>((type) -> new TileEntitySolderingTable(),
			TileEntityRenderSolderingTable::new, ModBlocks.SolderingTable);

	public TileEntitySolderingTable() {
		super(TYPE);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerSolderingTable(windowId, inventory, this);
	}
}
