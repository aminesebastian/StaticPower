package theking530.staticpower.cables.digistore;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.attachments.digistore.AbstractDigistoreCableAttachment;
import theking530.staticpower.cables.attachments.digistore.DigistoreLight;
import theking530.staticpower.cables.attachments.digistore.DigistoreScreen;
import theking530.staticpower.cables.attachments.digistore.craftinginterface.DigistoreCraftingInterfaceAttachment;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.exporter.DigistoreExporterAttachment;
import theking530.staticpower.cables.attachments.digistore.importer.DigistoreImporterAttachment;
import theking530.staticpower.cables.attachments.digistore.iobus.DigistoreIOBusAttachment;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder;
import theking530.staticpower.cables.attachments.digistore.regulator.DigistoreRegulatorAttachment;
import theking530.staticpower.cables.attachments.digistore.terminal.DigistoreTerminal;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class DigistoreCableProviderComponent extends AbstractCableProviderComponent {
	public static final String POWER_USAGE_TAG = "power_usage";
	@UpdateSerialize
	private boolean managerPresent;
	private boolean shouldControlOnBlockState;
	private long powerUsage;

	public DigistoreCableProviderComponent(String name) {
		this(name, 0);
	}

	public DigistoreCableProviderComponent(String name, long powerUsage) {
		super(name, CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
		shouldControlOnBlockState = false;
		this.powerUsage = powerUsage;
		addValidAttachmentClass(DigistoreTerminal.class);
		addValidAttachmentClass(DigistoreCraftingTerminal.class);
		addValidAttachmentClass(DigistoreExporterAttachment.class);
		addValidAttachmentClass(DigistoreImporterAttachment.class);
		addValidAttachmentClass(DigistoreIOBusAttachment.class);
		addValidAttachmentClass(DigistoreRegulatorAttachment.class);
		addValidAttachmentClass(DigistorePatternEncoder.class);
		addValidAttachmentClass(DigistoreCraftingInterfaceAttachment.class);
		addValidAttachmentClass(DigistoreLight.class);
		addValidAttachmentClass(DigistoreScreen.class);
	}

	public void preProcessUpdate() {
		super.preProcessUpdate();
		// Check to see if the manager is present. If not, update the tile entity.
		if (!getWorld().isRemote) {
			this.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(network -> {
				if (managerPresent != network.isManagerPresent()) {
					managerPresent = network.isManagerPresent();
					getTileEntity().markTileEntityForSynchronization();
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

	@Override
	public boolean attachAttachment(ItemStack attachment, Direction side) {
		boolean superResult = super.attachAttachment(attachment, side);

		// Update the power usage on the server.
		if (!getWorld().isRemote) {
			updatePowerUsage();
		}

		return superResult;
	}

	@Override
	public ItemStack removeAttachment(Direction side) {
		ItemStack superResult = super.removeAttachment(side);
		updatePowerUsage();
		return superResult;
	}

	public void updatePowerUsage() {
		// Update the power usage on the server.
		if (CableNetworkManager.get(getWorld()).isTrackingCable(getPos())) {
			updatePowerUsage(CableNetworkManager.get(getWorld()).getCable(getPos()));
		}
	}

	protected void updatePowerUsage(ServerCable cable) {
		// Throw this in a try catch because I'm skeptical about doing this.
		try {
			// Update the cable value on the server.
			long attachmentPowerUsage = 0;
			for (ItemStack attachment : attachments) {
				if (attachment.getItem() instanceof AbstractDigistoreCableAttachment) {
					AbstractDigistoreCableAttachment attachmentItem = (AbstractDigistoreCableAttachment) attachment.getItem();
					attachmentPowerUsage += attachmentItem.getPowerUsage(attachment);
				}
			}

			// Update the power usage on the server.
			cable.setProperty(POWER_USAGE_TAG, attachmentPowerUsage + powerUsage);
		} catch (Exception e) {
			StaticPower.LOGGER
					.error(String.format("An error occured when attempting to update the power usage of a digistore cable provider owned by a tile entity at location: %1$s of type: %2$s",
							getPos(), getTileEntity().getClass()));
		}
	}

	public long getBasePowerUsage() {
		return powerUsage;
	}

	public DigistoreCableProviderComponent setBasePowerUsage(long usage) {
		this.powerUsage = usage;
		return this;
	}

	public boolean isManagerPresent() {
		return managerPresent;
	}

	public DigistoreCableProviderComponent setShouldControlOnState() {
		shouldControlOnBlockState = true;
		return this;
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		// Update the power usage.
		updatePowerUsage(cable);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
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
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
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
		if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
			return currentState.get(StaticPowerMachineBlock.IS_ON);
		}
		return false;
	}
}
