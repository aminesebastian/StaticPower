package theking530.staticpower.cables.redstone.bundled;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class BundledRedstoneCableComponent extends AbstractCableProviderComponent {
	public BundledRedstoneCableComponent(String name) {
		super(name, CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null) {
			if (otherProvider.areCableCompatible(this, side)) {
				if (!otherProvider.isSideDisabled(side.getOpposite())) {
					return CableConnectionState.CABLE;
				}
			} else {
				// Check the intersection between the basic redstone networks and the network of
				// the other provider.
				Set<ResourceLocation> intersection = new HashSet<ResourceLocation>(CableNetworkModuleTypes.REDSTONE_MODULES);
				intersection.retainAll(otherProvider.getSupportedNetworkModuleTypes());
				return intersection.size() > 0 ? CableConnectionState.TILE_ENTITY : CableConnectionState.NONE;
			}
		}
		return CableConnectionState.NONE;
	}

	public Optional<BundledRedstoneNetworkModule> getRedstoneNetworkModule() {
		return getNetworkModule(CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE);
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
}
