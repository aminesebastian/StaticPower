package theking530.staticpower.tileentities.cables.power;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.tileentities.cables.AbstractCableBlock;
import theking530.staticpower.tileentities.components.CableWrapperProviderComponent;
import theking530.staticpower.tileentities.network.factories.cables.CableTypes;

public class BlockPowerCable extends AbstractCableBlock {

	public BlockPowerCable(String name) {
		super(name, "power");
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityPowerCable();
	}

	@Override
	protected boolean isConnectedToCableInDirection(IWorld world, BlockPos pos, Direction direction) {
		CableWrapperProviderComponent wrapperComponent = getCableWrapperComponent(world, pos.offset(direction));
		if (wrapperComponent != null) {
			return wrapperComponent.getCableType() == CableTypes.BASIC_POWER;
		}
		return false;
	}

	@Override
	protected boolean isConnectedToAttachableInDirection(IWorld world, BlockPos pos, Direction direction) {
		if (world.getTileEntity(pos.offset(direction)) != null) {
			return world.getTileEntity(pos.offset(direction)).getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).isPresent();
		}
		return false;
	}
}
