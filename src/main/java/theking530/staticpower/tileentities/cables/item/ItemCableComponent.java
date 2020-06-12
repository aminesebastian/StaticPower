package theking530.staticpower.tileentities.cables.item;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.components.AbstractCableProviderComponent;

public class ItemCableComponent extends AbstractCableProviderComponent {

	public ItemCableComponent(String name, ResourceLocation type) {
		super(name, type);
	}

	@Override
	protected void updateConnectionStates() {
		for (Direction dir : Direction.values()) {
			BlockPos position = getTileEntity().getPos().offset(dir);
			AbstractCableProviderComponent overProvider = CableUtilities.getCableWrapperComponent(getWorld(), position);
			if (overProvider != null && overProvider.getCableType() == getCableType()) {
				ConnectionStates[dir.ordinal()] = CableConnectionState.CABLE;
			} else if (getWorld().getTileEntity(position) != null) {
				TileEntity te = getWorld().getTileEntity(position);
				if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
					ConnectionStates[dir.ordinal()] = CableConnectionState.TILE_ENTITY;
				}
			} else {
				ConnectionStates[dir.ordinal()] = CableConnectionState.NONE;
			}
		}
	}
}
