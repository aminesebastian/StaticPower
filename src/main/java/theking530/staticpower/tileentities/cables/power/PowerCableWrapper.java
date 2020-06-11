package theking530.staticpower.tileentities.cables.power;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.network.CableNetwork;
import theking530.staticpower.tileentities.network.factories.cables.CableTypes;
import theking530.staticpower.tileentities.network.factories.modules.CableNetworkModuleRegistry;
import theking530.staticpower.tileentities.network.factories.modules.CableNetworkModuleTypes;

public class PowerCableWrapper extends AbstractCableWrapper {

	public PowerCableWrapper(World world, BlockPos position) {
		super(world, position, CableTypes.BASIC_POWER);
	}

	@Override
	public void onNetworkJoined(CableNetwork network) {
		super.onNetworkJoined(network);
		if (!network.hasModule(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT)) {
			network.addModule(CableNetworkModuleRegistry.get().create(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT));
		}
	}

	@Override
	public CableAttachmentType getSideAttachmentType(Direction direction) {
		if (World.getTileEntity(getPos().offset(direction)) instanceof TileEntityPowerCable) {
			return CableAttachmentType.CABLE;
		} else {
			TileEntity te = World.getTileEntity(getPos());
			if (te != null && !te.isRemoved() && te.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
				return CableAttachmentType.TILE_ENTITY;
			}
		}
		return CableAttachmentType.NONE;
	}
}
