package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderSolderingTable;
import theking530.staticpower.init.ModBlocks;

public class TileEntitySolderingTable extends AbstractSolderingTable {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolderingTable> TYPE = new BlockEntityTypeAllocator<TileEntitySolderingTable>((type) -> new TileEntitySolderingTable(),
			ModBlocks.SolderingTable);
	
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderSolderingTable::new);
		}
	}

	public TileEntitySolderingTable() {
		super(TYPE);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSolderingTable(windowId, inventory, this);
	}
}
