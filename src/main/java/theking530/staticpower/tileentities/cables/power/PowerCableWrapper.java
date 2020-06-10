package theking530.staticpower.tileentities.cables.power;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.CableType;

public class PowerCableWrapper extends AbstractCableWrapper {

	public PowerCableWrapper(World world, BlockPos position) {
		super(world, position, CableType.BASIC_POWER);
	}

	@Override
	public boolean isAttachedOnSide(Direction direction) {
		TileEntity te = World.getTileEntity(getPos());
		if (te != null && !te.isRemoved()) {
			return te.getCapability(CapabilityEnergy.ENERGY).isPresent();
		}
		return false;
	}

	@Override
	public boolean isConnectedToCableOnSide(Direction direction) {
		return World.getTileEntity(getPos().offset(direction)) instanceof TileEntityPowerCable;
	}
}
