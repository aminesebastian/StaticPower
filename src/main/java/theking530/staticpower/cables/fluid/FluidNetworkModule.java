package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import theking530.staticpower.cables.attachments.extractor.ExtractorAttachment;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.utilities.MetricConverter;

public class FluidNetworkModule extends AbstractCableNetworkModule {
	private final FluidTank networkTank;

	public FluidNetworkModule() {
		super(CableNetworkModuleTypes.FLUID_NETWORK_MODULE);
		networkTank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
	}

	@Override
	public void tick(World world) {
		// If we have no fluid, do nothing.
		if (networkTank.getFluid().isEmpty()) {
			return;
		}

		// Get a map of all the applicable destination that support the fluid we have in
		// our tank and are not full.
		HashMap<BlockPos, DestinationWrapper> destinations = new HashMap<BlockPos, DestinationWrapper>();
		Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
			if (wrapper.supportsType(DestinationType.FLUID)) {
				IFluidHandler handler = wrapper.getTileEntity().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, wrapper.getFirstConnectedDestinationSide()).orElse(null);
				if (handler != null && handler.fill(networkTank.getFluid(), FluidAction.SIMULATE) > 0) {
					destinations.put(pos, wrapper);
				}
			}
		});

		// If the list of destinations is empty, do nothing.
		if (destinations.isEmpty()) {
			return;
		}

		// Allocate a list of all the fluid handlers to fill.
		List<FluidInterfaceWrapper> fluidHandlers = new ArrayList<FluidInterfaceWrapper>();

		// Iterate through all the destinations.
		for (DestinationWrapper destination : destinations.values()) {
			// Iterate through all the connected cables.
			for (BlockPos cablePos : destination.getConnectedCables().keySet()) {
				// Get the connected side.
				Direction cableSide = destination.getConnectedCables().get(cablePos);

				// Skip destinations that don't support fluid interaction.
				if (!destination.supportsTypeOnSide(cableSide, DestinationType.FLUID)) {
					continue;
				}

				// Get the destination tile and skip if it is null.
				TileEntity tile = destination.getTileEntity();
				if (tile == null) {
					continue;
				}

				// Get the target fluid handler, skip if it is null.
				IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, cableSide).orElse(null);
				if (handler == null) {
					continue;
				}

				// Get the cable and skip if its industrial.
				ServerCable cable = CableNetworkManager.get(world).getCable(cablePos);
				if (cable.getBooleanProperty(FluidCableComponent.FLUID_INDUSTRIAL_DATA_TAG_KEY)) {
					continue;
				}

				// Don't insert on block sides that have an extractor.
				if (cable.getAttachmentDataForSide(cableSide.getOpposite()).hasProperty(ExtractorAttachment.INPUT_BLOCKED)) {
					if (cable.getAttachmentDataForSide(cableSide.getOpposite()).getBooleanProperty(ExtractorAttachment.INPUT_BLOCKED)) {
						continue;
					}
				}

				// If we made it this far, add the handler to the list.
				fluidHandlers.add(new FluidInterfaceWrapper(handler, cable));
			}
		}

		// If we found no valid fluid handlers, return.
		if (fluidHandlers.isEmpty()) {
			return;
		}

		// Calculate how we should split the output amount.
		int outputPerDestination = Math.max(1, networkTank.getFluidAmount() / fluidHandlers.size());

		// Supply the fluid.
		for (FluidInterfaceWrapper tank : fluidHandlers) {
			// Get the transfer rate.
			int transferRate = tank.cable.getIntProperty(FluidCableComponent.FLUID_RATE_DATA_TAG_KEY);

			// Calculate how much fluid we can offer. If it is less than or equal to 0, do
			// nothing.
			int toOfferAmount = Math.min(transferRate, networkTank.getFluidAmount());
			toOfferAmount = Math.min(outputPerDestination, toOfferAmount);
			if (toOfferAmount <= 0) {
				break;
			}

			// Drain the fluid stack from our tank.
			FluidStack toOffer = networkTank.drain(toOfferAmount, IFluidHandler.FluidAction.EXECUTE);
			if (toOffer.isEmpty()) {
				break;
			}

			// Insert it into the destination.
			int accepted = tank.fluidHandler.fill(toOffer, IFluidHandler.FluidAction.EXECUTE);

			// If there is any left over, put it back into our tank.
			int remainder = toOffer.getAmount() - accepted;
			if (remainder > 0) {
				FluidStack remainderStack = toOffer.copy();
				remainderStack.setAmount(remainder);

				networkTank.fill(remainderStack, IFluidHandler.FluidAction.EXECUTE);
			}
		}
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
		if (other.hasModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE)) {
			FluidNetworkModule module = (FluidNetworkModule) other.getModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE);
			module.getFluidStorage().fill(getFluidStorage().getFluid(), FluidAction.EXECUTE);
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		// Allocate the total capacity.
		int total = 0;

		// Get all the cables in the network and get their cable components.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// If they have a fluid cable component, get the capacity.
			if (cable.containsProperty(FluidCableComponent.FLUID_CAPACITY_DATA_TAG_KEY)) {
				total += cable.getIntProperty(FluidCableComponent.FLUID_CAPACITY_DATA_TAG_KEY);
			}
		}

		// Set the capacity of the tank to the provided capacity.
		networkTank.setCapacity(total);
	}

	@Override
	public void getReaderOutput(List<ITextComponent> output) {
		String storedFluid = new MetricConverter(networkTank.getFluidAmount()).getValueAsString(true);
		String maximumFluid = new MetricConverter(networkTank.getCapacity()).getValueAsString(true);
		output.add(new StringTextComponent(
				String.format("Contains: %1$smB of %2$s out of a maximum of %3$smB.", storedFluid, networkTank.getFluid().getDisplayName().getString(), maximumFluid)));
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		networkTank.readFromNBT(tag);
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		networkTank.writeToNBT(tag);
		return tag;
	}

	public FluidTank getFluidStorage() {
		return networkTank;
	}

	protected class FluidInterfaceWrapper {
		protected IFluidHandler fluidHandler;
		protected ServerCable cable;

		protected FluidInterfaceWrapper(IFluidHandler fluidHandler, ServerCable cable) {
			this.fluidHandler = fluidHandler;
			this.cable = cable;
		}
	}
}
