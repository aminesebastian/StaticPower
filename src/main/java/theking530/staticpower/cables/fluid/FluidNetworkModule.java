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
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;

public class FluidNetworkModule extends AbstractCableNetworkModule {
	private final FluidTank FluidTank;

	public FluidNetworkModule() {
		super(CableNetworkModuleTypes.FLUID_NETWORK_MODULE);
		FluidTank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
	}

	@Override
	public void tick(World world) {
		HashMap<BlockPos, DestinationWrapper> destinations = Network.getGraph().getDestinations();

		if (FluidTank.getFluid().isEmpty() || destinations.isEmpty()) {
			return;
		}

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

			// Calculate how much fluid we can offer. If it is less than or equal to 0, do
			// nothing.
			int toOfferAmount = Math.min(10, FluidTank.getFluidAmount());
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
		FluidTank.setCapacity(mapper.getDiscoveredCables().stream().filter(p -> p.supportsNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE)).mapToInt(p -> 100).sum());
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
