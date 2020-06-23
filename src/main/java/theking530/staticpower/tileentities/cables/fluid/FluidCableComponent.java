package theking530.staticpower.tileentities.cables.fluid;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticpower.items.cableattachments.extractor.ExtractorAttachment;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.cables.ServerCable;
import theking530.staticpower.tileentities.cables.ServerCable.CableConnectionState;
import theking530.staticpower.tileentities.cables.network.CableNetworkManager;
import theking530.staticpower.tileentities.cables.network.modules.FluidCableUpdatePacket;
import theking530.staticpower.tileentities.cables.network.modules.FluidNetworkModule;
import theking530.staticpower.tileentities.cables.network.modules.factories.CableNetworkModuleTypes;

public class FluidCableComponent extends AbstractCableProviderComponent implements IFluidHandler {
	public static final int EXTRACTION_RATE = 10;
	public static final float UPDATE_THRESHOLD = 0.1f;
	private FluidStack lastUpdateFluidStack;
	private float lastUpdateFilledPercentage;

	public FluidCableComponent(String name) {
		super(name, CableNetworkModuleTypes.FLUID_NETWORK_MODULE);
		lastUpdateFluidStack = FluidStack.EMPTY;
		lastUpdateFilledPercentage = 0.0f;
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (!getWorld().isRemote) {
			getFluidNetworkModule().ifPresent(network -> {
				boolean shouldUpdate = !network.getFluidStorage().getFluid().isFluidEqual(lastUpdateFluidStack);
				shouldUpdate |= Math.abs(lastUpdateFilledPercentage - getFilledPercentage()) > UPDATE_THRESHOLD;
				if (shouldUpdate) {
					updateClientRenderValues();
				}
			});
		}
	}

	public void updateClientRenderValues() {
		if (!getWorld().isRemote) {
			getFluidNetworkModule().ifPresent(network -> {
				lastUpdateFluidStack = network.getFluidStorage().getFluid();
				lastUpdateFilledPercentage = getFilledPercentage();

				FluidCableUpdatePacket packet = new FluidCableUpdatePacket(getPos(), lastUpdateFluidStack, lastUpdateFilledPercentage);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 32, packet);
			});
		}
	}

	public void recieveUpdateRenderValues(FluidStack stack, float fluidAmount) {
		if (getWorld().isRemote) {
			lastUpdateFluidStack = stack;
			lastUpdateFilledPercentage = Math.min(1.0f, fluidAmount);
		}
	}

	public float getFilledPercentage() {
		if (getWorld().isRemote) {
			return lastUpdateFilledPercentage;
		} else {
			AtomicReference<Float> output = new AtomicReference<Float>(0.0f);
			getFluidNetworkModule().ifPresent(network -> {
				output.set((float) network.getFluidStorage().getFluidAmount() / network.getFluidStorage().getCapacity());
			});
			return output.get();
		}
	}

	@Override
	public int getTanks() {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicInteger recieve = new AtomicInteger(0);
			getFluidNetworkModule().ifPresent(network -> {
				recieve.set(network.getFluidStorage().getTanks());
			});
			return recieve.get();
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicReference<FluidStack> fluid = new AtomicReference<FluidStack>(FluidStack.EMPTY);
			getFluidNetworkModule().ifPresent(network -> {
				fluid.set(network.getFluidStorage().getFluidInTank(tank));
			});
			return fluid.get();
		} else {
			return lastUpdateFluidStack;
		}
	}

	@Override
	public int getTankCapacity(int tank) {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicInteger recieve = new AtomicInteger(0);
			getFluidNetworkModule().ifPresent(network -> {
				recieve.set(network.getFluidStorage().getTankCapacity(tank));
			});
			return recieve.get();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicBoolean recieve = new AtomicBoolean(false);
			getFluidNetworkModule().ifPresent(network -> {
				recieve.set(network.getFluidStorage().isFluidValid(tank, stack));
			});
			return recieve.get();
		} else {
			return false;
		}
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicInteger recieve = new AtomicInteger(0);
			getFluidNetworkModule().ifPresent(network -> {
				recieve.set(network.getFluidStorage().fill(resource, action));
			});
			return recieve.get();
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}

	/**
	 * Gets the fluid network module for the network this cable belongs to. We have
	 * to wrap it in an optional because while we can guarantee once this component
	 * is validated that the network is valid, since this component exposes external
	 * methods, other tile entity that are made valid before us may call some of our
	 * methods.
	 * 
	 * @return
	 */
	protected Optional<FluidNetworkModule> getFluidNetworkModule() {
		CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
		ServerCable cable = manager.getCable(getTileEntity().getPos());
		if (cable.getNetwork() != null) {
			return Optional.of(cable.getNetwork().getModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE));
		}
		return Optional.empty();
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, BlockPos blockPosition) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null && otherProvider.shouldConnectionToCable(this, side)) {
			return CableConnectionState.CABLE;
		} else if (getWorld().getTileEntity(blockPosition) != null && otherProvider == null) {
			TileEntity te = getWorld().getTileEntity(blockPosition);
			if (te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()).isPresent()) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return !attachment.isEmpty() && attachment.getItem() instanceof ExtractorAttachment;
	}
}
