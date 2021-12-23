package theking530.staticpower.cables.digistore;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
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
import theking530.staticpower.tileentities.TileEntityUpdateRequest;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class DigistoreCableProviderComponent extends AbstractCableProviderComponent {
	public static final String POWER_USAGE_TAG = "power_usage";
	public static final int MANAGER_PRESENCE_UPDATE_TIME = 20;
	@UpdateSerialize
	private boolean managerPresent;
	private int managerPresenceUpdateTimer;
	private boolean shouldControlOnBlockState;
	private long powerUsage;

	/**
	 * This is for any tile entities or blocks that must attach to the digistore
	 * network.
	 * 
	 * @param name
	 */
	public DigistoreCableProviderComponent(String name) {
		this(name, 0);
	}

	/**
	 * This is for any cables. Do not use this constructor on a digitstore network
	 * tile entity or block.
	 * 
	 * @param name
	 * @param powerUsage
	 */
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
		if (!getWorld().isClientSide) {
			this.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(network -> {
				if (managerPresent != network.isManagerPresent()) {
					managerPresent = network.isManagerPresent();
					getTileEntity().addUpdateRequest(TileEntityUpdateRequest.syncDataOnly(true), false);
				}

				// Update the on/off state of the block.
				boolean shouldUpdateBlockState = false;
				if (shouldControlOnBlockState) {
					if (managerPresent && !getIsOnBlockState()) {
						shouldUpdateBlockState = true;
					} else if (!managerPresent && getIsOnBlockState()) {
						shouldUpdateBlockState = true;
					}
				}

				// If we should update the block state, wait the appropriate amount of time,
				// then do it.
				if (shouldUpdateBlockState) {
					if (managerPresenceUpdateTimer >= MANAGER_PRESENCE_UPDATE_TIME) {
						setIsOnBlockState(managerPresent);
					} else {
						managerPresenceUpdateTimer++;
					}
				} else {
					managerPresenceUpdateTimer = 0;
				}
			});
		}
	}

	@Override
	public boolean attachAttachment(ItemStack attachment, Direction side) {
		boolean superResult = super.attachAttachment(attachment, side);

		// Update the power usage on the server.
		if (!getWorld().isClientSide) {
			updatePowerUsage();
		}

		return superResult;
	}

	@Override
	public ItemStack removeAttachment(Direction side) {
		ItemStack superResult = super.removeAttachment(side);
		if (!getWorld().isClientSide) {
			updatePowerUsage();
		}
		return superResult;
	}

	public void updatePowerUsage() {
		// Update the power usage on the server.
		if (!getWorld().isClientSide()) {
			if (CableNetworkManager.get(getWorld()).isTrackingCable(getPos())) {
				updatePowerUsage(CableNetworkManager.get(getWorld()).getCable(getPos()));
			}
		} else {
			StaticPower.LOGGER
					.warn(String.format("Updating the power usage should only be performed on the server! A call from the client was made at position: %1$s.", getPos().toString()));
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
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
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
		if (!getWorld().isClientSide && shouldControlOnBlockState) {
			BlockState currentState = getWorld().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.getValue(StaticPowerMachineBlock.IS_ON) != on) {
					getWorld().setBlock(getPos(), currentState.setValue(StaticPowerMachineBlock.IS_ON, on), 2);
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
			return currentState.getValue(StaticPowerMachineBlock.IS_ON);
		}
		return false;
	}
}
