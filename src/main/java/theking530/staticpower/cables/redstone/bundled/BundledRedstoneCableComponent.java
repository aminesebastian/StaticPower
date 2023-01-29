package theking530.staticpower.cables.redstone.bundled;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.cablenetwork.data.CableConnectionState.CableConnectionType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;

public class BundledRedstoneCableComponent extends AbstractCableProviderComponent {
	public BundledRedstoneCableComponent(String name) {
		super(name, ModCableModules.BundledRedstone.get());
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
				Set<CableNetworkModuleType> intersection = new HashSet<CableNetworkModuleType>();
				for (MinecraftColor color : MinecraftColor.values()) {
					intersection.add(ModCableModules.RedstoneModules.get(color).get());
				}
				intersection.retainAll(otherProvider.getSupportedNetworkModuleTypes());
				return intersection.size() > 0 ? CableConnectionType.DESTINATION : CableConnectionType.NONE;
			}
		}
		return CableConnectionType.NONE;
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Redstone.get());
	}

	public Optional<BundledRedstoneNetworkModule> getRedstoneNetworkModule() {
		return getNetworkModule(ModCableModules.BundledRedstone.get());
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
