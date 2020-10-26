package theking530.staticpower.cables.scaffold;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class ScaffoldCableComponent extends AbstractCableProviderComponent {
	public ScaffoldCableComponent(String name) {
		super(name, CableNetworkModuleTypes.SCAFFOLD_NETWORK_MODULE);
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	protected ServerCable createCable() {
		return new ServerCable(getWorld(), getPos(), getSupportedNetworkModuleTypes(), (cable) -> {
		});
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionState.CABLE;
			}
		}
		return CableConnectionState.NONE;
	}
}
