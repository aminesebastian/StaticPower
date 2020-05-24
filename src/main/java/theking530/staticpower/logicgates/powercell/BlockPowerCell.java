package theking530.staticpower.logicgates.powercell;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.logicgates.BlockLogicGate;

public class BlockPowerCell extends BlockLogicGate {

	public BlockPowerCell(String name) {
		super(name, GuiIDRegistry.guiPowerCell);
	}
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPowerCell();
	}

}
