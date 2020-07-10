package theking530.staticpower.cables.fluid;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;

public class FluidNetworkModule extends AbstractCableNetworkModule {
	private final FluidTank FluidTank;

	public FluidNetworkModule() {
		super(CableNetworkModuleTypes.FLUID_NETWORK_MODULE);
		FluidTank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
	}

	@Override
	public void tick(World world) {
		// If we have no fluid, do nothing.
		if (FluidTank.getFluid().isEmpty()) {
			return;
		}

		// Get a map of all the applicable destination that support the fluid we have in
		// our tank and are not full.
		HashMap<BlockPos, DestinationWrapper> destinations = new HashMap<BlockPos, DestinationWrapper>();
		Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
			if (wrapper.supportsType(DestinationType.FLUID)) {
				IFluidHandler handler = wrapper.getTileEntity().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, wrapper.getDestinationSide()).orElse(null);
				if (handler != null && handler.fill(FluidTank.getFluid(), FluidAction.SIMULATE) > 0) {
					destinations.put(pos, wrapper);
				}
			}
		});

		// If the list of destinations is empty, do nothing.
		if (destinations.isEmpty()) {
			return;
		}

		// Calculate how we should split the output amount.
		int outputPerDestination = Math.max(1, FluidTank.getFluidAmount() / destinations.size());
		for (DestinationWrapper destination : destinations.values()) {
			// Skip destinations that don't support fluid interaction.
			if (!destination.supportsType(DestinationType.FLUID)) {
				continue;
			}

			// Get the destination tile and skip if it is null.
			TileEntity tile = destination.getTileEntity();
			if (tile == null) {
				continue;
			}

			// Get the target fluid handler, skip if it is null.
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.getDestinationSide()).orElse(null);
			if (handler == null) {
				continue;
			}

			// Get the transfer rate.
			int transferRate = 0;
			for (AbstractCableProviderComponent cableComp : CableNetworkManager.get(Network.getWorld()).getCable(destination.getConnectedCable()).getCableProviderComponents()) {
				if (cableComp instanceof FluidCableComponent) {
					// Add the capacity to the total.
					transferRate = ((FluidCableComponent) cableComp).getCapacity();
				}
			}

			// Calculate how much fluid we can offer. If it is less than or equal to 0, do
			// nothing.
			int toOfferAmount = Math.min(transferRate, FluidTank.getFluidAmount());
			toOfferAmount = Math.min(outputPerDestination, toOfferAmount);
			if (toOfferAmount <= 0) {
				break;
			}

			// Drain the fluid stack from our tank.
			FluidStack toOffer = FluidTank.drain(toOfferAmount, IFluidHandler.FluidAction.EXECUTE);
			if (toOffer.isEmpty()) {
				break;
			}

			// Insert it into the destination.
			int accepted = handler.fill(toOffer, IFluidHandler.FluidAction.EXECUTE);

			// If there is any left over, put it back into our tank.
			int remainder = toOffer.getAmount() - accepted;
			if (remainder > 0) {
				FluidStack remainderStack = toOffer.copy();
				remainderStack.setAmount(remainder);

				FluidTank.fill(remainderStack, IFluidHandler.FluidAction.EXECUTE);
			}
		}
	}

	public void onNetworksJoined(CableNetwork other) {
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
			for (AbstractCableProviderComponent cableComp : cable.getCableProviderComponents()) {
				if (cableComp instanceof FluidCableComponent) {
					// Add the capacity to the total.
					total += ((FluidCableComponent) cableComp).getCapacity();
				}
			}
		}

		// Set the capacity of the tank to the provided capacity.
		FluidTank.setCapacity(total);
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		FluidTank.readFromNBT(tag);
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		FluidTank.writeToNBT(tag);
		return tag;
	}

	public FluidTank getFluidStorage() {
		return FluidTank;
	}
}
