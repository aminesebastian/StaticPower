package theking530.staticpower.cables.scaffold;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.data.CableSideConnectionState.CableConnectionType;
import theking530.staticpower.cables.network.destinations.CableDestination;
import theking530.staticpower.cables.network.modules.CableNetworkModuleTypes;

public class ScaffoldCableComponent extends AbstractCableProviderComponent {
	public ScaffoldCableComponent(String name) {
		super(name, CableNetworkModuleTypes.SCAFFOLD_NETWORK_MODULE);
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {

	}

	protected CableConnectionType getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getLevel(), blockPosition);
		if (otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionType.CABLE;
			}
		}
		return CableConnectionType.NONE;
	}
}
