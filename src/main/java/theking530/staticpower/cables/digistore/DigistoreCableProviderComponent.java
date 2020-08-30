package theking530.staticpower.cables.digistore;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.items.cableattachments.digistoreterminal.AbstractDigistoreTerminalAttachment;
import theking530.staticpower.items.cableattachments.exporter.DigistoreExporterAttachment;
import theking530.staticpower.items.cableattachments.importer.DigistoreImporterAttachment;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class DigistoreCableProviderComponent extends AbstractCableProviderComponent {
	@UpdateSerialize
	private boolean managerPresent;
	private boolean shouldControlOnBlockState;

	public DigistoreCableProviderComponent(String name) {
		super(name, CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
		shouldControlOnBlockState = false;
	}

	public void preProcessUpdate() {
		super.preProcessUpdate();
		// Check to see if the manager is present. If not, update the tile entity.
		if (!getWorld().isRemote) {
			this.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(network -> {
				if (managerPresent != network.isManagerPresent()) {
					managerPresent = network.isManagerPresent();
					this.getTileEntity().markTileEntityForSynchronization();
				}

				// Update the on/off state of the block.
				if (shouldControlOnBlockState) {
					if (managerPresent && !getIsOnBlockState()) {
						setIsOnBlockState(true);
					} else if (!managerPresent && getIsOnBlockState()) {
						setIsOnBlockState(false);
					}
				}
			});
		}
	}

	public boolean isManagerPresent() {
		return managerPresent;
	}

	public DigistoreCableProviderComponent setShouldControlOnState() {
		shouldControlOnBlockState = true;
		return this;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		if (attachment.getItem() instanceof AbstractDigistoreTerminalAttachment) {
			return true;
		}
		if (attachment.getItem() instanceof DigistoreExporterAttachment) {
			return true;
		}
		if (attachment.getItem() instanceof DigistoreImporterAttachment) {
			return true;
		}
		return false;
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);

		if (te instanceof TileEntityDigistoreWire && otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionState.CABLE;
			}
		} else if (te instanceof TileEntityBase) {
			TileEntityBase baseTe = (TileEntityBase) te;
			if (baseTe.hasComponentOfType(DigistoreCableProviderComponent.class)) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	protected void setIsOnBlockState(boolean on) {
		if (!getWorld().isRemote && shouldControlOnBlockState) {
			BlockState currentState = getWorld().getBlockState(getPos());
			if (currentState.has(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.get(StaticPowerMachineBlock.IS_ON) != on) {
					getWorld().setBlockState(getPos(), currentState.with(StaticPowerMachineBlock.IS_ON, on), 2);
				}
			}
		}
	}

	public boolean getIsOnBlockState() {
		if (!shouldControlOnBlockState) {
			return false;
		}
		BlockState currentState = getWorld().getBlockState(getPos());
		if (currentState.has(StaticPowerMachineBlock.IS_ON)) {
			return currentState.get(StaticPowerMachineBlock.IS_ON);
		}
		return false;
	}

}
