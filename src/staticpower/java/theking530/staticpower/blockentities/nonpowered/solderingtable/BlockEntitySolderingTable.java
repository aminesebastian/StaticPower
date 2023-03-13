package theking530.staticpower.blockentities.nonpowered.solderingtable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderSolderingTable;
import theking530.staticpower.init.ModBlocks;

public class BlockEntitySolderingTable extends AbstractSolderingTable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolderingTable> TYPE = new BlockEntityTypeAllocator<BlockEntitySolderingTable>("soldering_table",
			(type, pos, state) -> new BlockEntitySolderingTable(pos, state), ModBlocks.SolderingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderSolderingTable::new);
		}
	}

	public BlockEntitySolderingTable(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSolderingTable(windowId, inventory, this);
	}
}
