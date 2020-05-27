package theking530.staticpower.tileentity.vacuumchest;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentity.BlockMachineBase;

public class BlockVacuumChest extends BlockMachineBase {

	public BlockVacuumChest(String name) {
		super(name);
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.VACCUM_CHEST.create();
	}
}
