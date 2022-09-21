package theking530.staticpower.cables.redstone.bundled;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.data.CableSideConnectionState.CableConnectionType;
import theking530.staticpower.cables.network.destinations.CableDestination;
import theking530.staticpower.cables.network.destinations.ModCableDestinations;
import theking530.staticpower.cables.network.modules.CableNetworkModuleTypes;

public class BundledRedstoneCableComponent extends AbstractCableProviderComponent {
	public BundledRedstoneCableComponent(String name) {
		super(name, CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE);
	}

	protected CableConnectionType getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getLevel(), blockPosition);
		if (otherProvider != null) {
			if (otherProvider.areCableCompatible(this, side)) {
				if (!otherProvider.isSideDisabled(side.getOpposite())) {
					return CableConnectionType.CABLE;
				}
			} else {
				// Check the intersection between the basic redstone networks and the network of
				// the other provider.
				Set<ResourceLocation> intersection = new HashSet<ResourceLocation>(CableNetworkModuleTypes.REDSTONE_MODULES);
				intersection.retainAll(otherProvider.getSupportedNetworkModuleTypes());
				return intersection.size() > 0 ? CableConnectionType.TILE_ENTITY : CableConnectionType.NONE;
			}
		}
		return CableConnectionType.NONE;
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Redstone.get());
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
