package theking530.staticpower.cables.digistore;

import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityUpdateRequest;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
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
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;

public class DigistoreCableProviderComponent extends AbstractCableProviderComponent {
	public static final String POWER_USAGE_TAG = "power_usage";
	public static final int MANAGER_PRESENCE_UPDATE_TIME = 20;
	@UpdateSerialize
	private boolean managerPresent;
	private int managerPresenceUpdateTimer;
	private boolean shouldControlOnBlockState;
	private double powerUsage;

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
	public DigistoreCableProviderComponent(String name, double powerUsage) {
		super(name, ModCableModules.Digistore.get());
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
		if (!getLevel().isClientSide()) {
			this.<DigistoreNetworkModule>getNetworkModule(ModCableModules.Digistore.get()).ifPresent(network -> {
				if (managerPresent != network.isManagerPresent()) {
					managerPresent = network.isManagerPresent();
					getBlockEntity().addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(true), false);
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
		if (!getLevel().isClientSide()) {
			updatePowerUsage();
		}

		return superResult;
	}

	@Override
	public ItemStack removeAttachment(Direction side) {
		ItemStack superResult = super.removeAttachment(side);
		if (!getLevel().isClientSide()) {
			updatePowerUsage();
		}
		return superResult;
	}

	public void updatePowerUsage() {
		// Update the power usage on the server.
		if (!getLevel().isClientSide()) {
			if (CableNetworkAccessor.get(getLevel()).isTrackingCable(getPos())) {
				updatePowerUsage(CableNetworkAccessor.get(getLevel()).getCable(getPos()));
			}
		} else {
			StaticPower.LOGGER.warn(
					String.format("Updating the power usage should only be performed on the server! A call from the client was made at position: %1$s.", getPos().toString()));
		}
	}

	protected void updatePowerUsage(Cable cable) {
		// Throw this in a try catch because I'm skeptical about doing this.
		try {
			// Update the cable value on the server.
			double attachmentPowerUsage = 0;
			for (Direction dir : Direction.values()) {
				if (hasAttachment(dir)) {
					ItemStack attachment = getAttachment(dir);
					if (attachment.getItem() instanceof AbstractDigistoreCableAttachment) {
						AbstractDigistoreCableAttachment attachmentItem = (AbstractDigistoreCableAttachment) attachment.getItem();
						attachmentPowerUsage += attachmentItem.getPowerUsage(attachment);
					}
				}
			}

			// Update the power usage on the server.
			cable.getDataTag().putDouble(POWER_USAGE_TAG, attachmentPowerUsage + powerUsage);
		} catch (Exception e) {
			StaticPower.LOGGER.error(
					String.format("An error occured when attempting to update the power usage of a digistore cable provider owned by a tile entity at location: %1$s of type: %2$s",
							getPos(), getBlockEntity().getClass()));
		}
	}

	public double getBasePowerUsage() {
		return powerUsage;
	}

	public DigistoreCableProviderComponent setBasePowerUsage(double usage) {
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
	protected void initializeCableProperties(Cable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		super.initializeCableProperties(cable, context, state, placer, stack);
		// Update the power usage.
		updatePowerUsage(cable);
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Digistore.get());
	}

	protected void setIsOnBlockState(boolean on) {
		if (!getLevel().isClientSide() && shouldControlOnBlockState) {
			BlockState currentState = getLevel().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.getValue(StaticPowerMachineBlock.IS_ON) != on) {
					getLevel().setBlock(getPos(), currentState.setValue(StaticPowerMachineBlock.IS_ON, on), 2);
				}
			}
		}
	}

	public boolean getIsOnBlockState() {
		if (!shouldControlOnBlockState) {
			return false;
		}
		BlockState currentState = getLevel().getBlockState(getPos());
		if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
			return currentState.getValue(StaticPowerMachineBlock.IS_ON);
		}
		return false;
	}
}
