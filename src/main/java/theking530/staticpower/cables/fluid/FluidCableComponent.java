package theking530.staticpower.cables.fluid;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.attachments.extractor.ExtractorAttachment;
import theking530.staticpower.cables.attachments.sprinkler.SprinklerAttachment;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class FluidCableComponent extends AbstractCableProviderComponent implements IFluidHandler {
	public static final String FLUID_CAPACITY_DATA_TAG_KEY = "fluid_capacity";
	public static final String FLUID_RATE_DATA_TAG_KEY = "fluid_transfer_rate";
	public static final String FLUID_INDUSTRIAL_DATA_TAG_KEY = "fluid_cable_industrial";
	public static final float UPDATE_THRESHOLD = 0.1f;
	private final int capacity;
	private final boolean isIndustrial;
	private FluidStack lastUpdateFluidStack;
	private float lastUpdateFilledPercentage;
	private float visualFilledPercentage;

	public FluidCableComponent(String name, boolean isIndustrial, int capacity) {
		super(name, CableNetworkModuleTypes.FLUID_NETWORK_MODULE);
		this.capacity = capacity;
		lastUpdateFluidStack = FluidStack.EMPTY;
		lastUpdateFilledPercentage = 0.0f;
		visualFilledPercentage = 0.0f;
		this.isIndustrial = isIndustrial;

		// Only non-industrial pipes can have attachments.
		if (!isIndustrial) {
			addValidAttachmentClass(ExtractorAttachment.class);
			addValidAttachmentClass(SprinklerAttachment.class);
		}
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (!getWorld().isClientSide) {
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				boolean shouldUpdate = !network.getFluidStorage().getFluid().isFluidEqual(lastUpdateFluidStack);
				shouldUpdate |= Math.abs(lastUpdateFilledPercentage - getFilledPercentage()) > UPDATE_THRESHOLD;
				shouldUpdate |= lastUpdateFilledPercentage > 0 && this.getFluidInTank(0).isEmpty();
				if (shouldUpdate) {
					updateClientRenderValues();
				}
			});
		}
	}

	@Override
	public void updateBeforeRendering(float partialTicks) {
		if (visualFilledPercentage != lastUpdateFilledPercentage) {
			float difference = visualFilledPercentage - lastUpdateFilledPercentage;
			visualFilledPercentage -= difference * (partialTicks / 20.0f);

		}
	}

	public void updateClientRenderValues() {
		if (!getWorld().isClientSide) {
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				lastUpdateFluidStack = network.getFluidStorage().getFluid();
				lastUpdateFilledPercentage = Math.min(1.0f, getFilledPercentage());

				// Only send the packet to nearby players since these packets get sent
				// frequently.
				FluidCableUpdatePacket packet = new FluidCableUpdatePacket(getPos(), lastUpdateFluidStack, lastUpdateFilledPercentage);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 64, packet);
			});
		}
	}

	public void recieveUpdateRenderValues(FluidStack stack, float fluidAmount) {
		if (getWorld().isClientSide) {
			lastUpdateFluidStack = stack;
			lastUpdateFilledPercentage = Math.min(1.0f, fluidAmount);
		}
	}

	public float getFilledPercentage() {
		if (getWorld().isClientSide) {
			return lastUpdateFilledPercentage;
		} else {
			AtomicReference<Float> output = new AtomicReference<Float>(0.0f);
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				output.set((float) network.getFluidStorage().getFluidAmount() / network.getFluidStorage().getCapacity());
			});
			return output.get();
		}
	}

	public float getVisualFilledPercentage() {
		return visualFilledPercentage;
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getTanks() {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicInteger recieve = new AtomicInteger(0);
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				recieve.set(network.getFluidStorage().getTanks());
			});
			return recieve.get();
		} else {
			return lastUpdateFluidStack.isEmpty() ? 0 : 1;
		}
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicReference<FluidStack> fluid = new AtomicReference<FluidStack>(FluidStack.EMPTY);
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				fluid.set(network.getFluidStorage().getFluidInTank(tank));
			});
			return fluid.get();
		} else {
			return lastUpdateFluidStack;
		}
	}

	@Override
	public int getTankCapacity(int tank) {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicInteger recieve = new AtomicInteger(0);
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				recieve.set(network.getFluidStorage().getTankCapacity(tank));
			});
			return recieve.get();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicBoolean recieve = new AtomicBoolean(false);
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				recieve.set(network.getFluidStorage().isFluidValid(tank, stack));
			});
			return recieve.get();
		} else {
			return false;
		}
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicInteger recieve = new AtomicInteger(0);
			this.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
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
		// Only provide the fluid capability if we are not disabled on that side.
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			boolean disabled = false;
			if (side != null) {
				if (getWorld().isClientSide) {
					disabled = isSideDisabled(side);
				} else {
					// If the cable is not valid, just assume disabled. Could be that the cable is
					// not yet initialized server side.
					Optional<ServerCable> cable = getCable();
					disabled = cable.isPresent() ? cable.get().isDisabledOnSide(side) : true;
				}
			}

			if (!disabled) {
				return LazyOptional.of(() -> this).cast();
			}
		}
		return LazyOptional.empty();

	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		cable.setProperty(FLUID_CAPACITY_DATA_TAG_KEY, capacity);
		cable.setProperty(FLUID_RATE_DATA_TAG_KEY, capacity);
		cable.setProperty(FLUID_INDUSTRIAL_DATA_TAG_KEY, isIndustrial);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionState.CABLE;
			}
		} else if (te != null && otherProvider == null) {
			if (te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()).isPresent()) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	public CompoundTag serializeSaveNbt(CompoundTag nbt) {
		super.serializeSaveNbt(nbt);
		// Save the filled percent.
		nbt.putFloat("filled_percentage", lastUpdateFilledPercentage);

		// Put the fluid stack.
		CompoundTag fluidNbt = new CompoundTag();
		lastUpdateFluidStack.writeToNBT(fluidNbt);
		nbt.put("fluid", fluidNbt);
		return nbt;
	}

	public void deserializeSaveNbt(CompoundTag nbt) {
		super.deserializeSaveNbt(nbt);
		// Load the filled percent.
		lastUpdateFilledPercentage = nbt.getFloat("filled_percentage");

		// Load the last update fluidstack.
		lastUpdateFluidStack = FluidStack.loadFluidStackFromNBT((CompoundTag) nbt.get("fluid"));
	}
}
