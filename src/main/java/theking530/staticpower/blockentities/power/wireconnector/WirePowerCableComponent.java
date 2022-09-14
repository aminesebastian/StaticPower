package theking530.staticpower.blockentities.power.wireconnector;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.cables.power.wire.PowerWireNetworkModule;

public class WirePowerCableComponent extends AbstractCableProviderComponent {

	public WirePowerCableComponent(String name) {
		super(name, CableNetworkModuleTypes.POWER_WIRE_NETWORK_MODULE);
	}

	protected Optional<PowerWireNetworkModule> getPowerNetworkModule() {
		return getNetworkModule(CableNetworkModuleTypes.POWER_WIRE_NETWORK_MODULE);
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {

	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		return CableConnectionState.NONE;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	protected ServerCable createCable() {
		return new ServerCable(getLevel(), getPos(), true, getSupportedNetworkModuleTypes());
	}

	public boolean isConnectedTo(BlockPos location) {
		if (!isClientSide()) {
			ServerCable trakcedCable = CableNetworkManager.get(getLevel()).getCable(location);
			if (trakcedCable != null) {
				return trakcedCable.isConnectedTo(location);
			}
		}
		return false;
	}

	public void addConnectedConnector(BlockPos location) {
		if (!isClientSide()) {
			ServerCable trakcedCable = this.getCable().get();
			if (trakcedCable != null) {
				trakcedCable.addConnection(location);
			}
		}
	}

	public boolean removeConnectedConnector(BlockPos location) {
		if (!isClientSide()) {
			ServerCable trakcedCable = CableNetworkManager.get(getLevel()).getCable(location);
			if (trakcedCable != null) {
				return trakcedCable.removeConnection(location);
			}
		}
		return false;
	}
}
