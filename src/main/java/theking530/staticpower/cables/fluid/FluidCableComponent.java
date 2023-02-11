package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.api.fluid.CapabilityStaticFluid;
import theking530.api.fluid.IStaticPowerFluidHandler;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.fluid.ISidedFluidHandler;
import theking530.staticcore.fluid.SidedFluidHandlerCapabilityWrapper;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.drain.DrainAttachment;
import theking530.staticpower.cables.attachments.extractor.ExtractorAttachment;
import theking530.staticpower.cables.attachments.sprinkler.SprinklerAttachment;
import theking530.staticpower.cables.fluid.BlockEntityFluidCable.FluidPipeType;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class FluidCableComponent extends AbstractCableProviderComponent implements ISidedFluidHandler, IStaticPowerFluidHandler {
	public static final String FLUID_INDUSTRIAL_DATA_TAG_KEY = "fluid_cable_industrial";
	public static final float FLUID_UPDATE_THRESHOLD = 10;
	public static final float MAX_TICKS_BEFORE_UPDATE = 20;

	private final SidedFluidHandlerCapabilityWrapper capabilityWrapper;

	private final int capacity;
	private final int transferRate;
	private final PipePressureProperties pressureProperties;
	private final FluidPipeType type;

	private FluidStack lastUpdateFluidStack;
	private float visualFluidAmount;
	private float clientPressure;

	private int updateTimer;

	public FluidCableComponent(String name, FluidPipeType type, int capacity, int transferRate, PipePressureProperties pressureProperties) {
		super(name, ModCableModules.Fluid.get());
		this.capacity = capacity;
		this.transferRate = transferRate;
		this.pressureProperties = pressureProperties;

		capabilityWrapper = new SidedFluidHandlerCapabilityWrapper(this);
		lastUpdateFluidStack = FluidStack.EMPTY;
		visualFluidAmount = 0.0f;
		this.type = type;

		// Only non-industrial pipes can have attachments.
		if (type != FluidPipeType.INDUSTRIAL) {
			addValidAttachmentClass(ExtractorAttachment.class);
			addValidAttachmentClass(SprinklerAttachment.class);
			addValidAttachmentClass(DrainAttachment.class);
		}
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (!isClientSide()) {
			Optional<FluidCableCapability> capability = getFluidCapability();
			if (capability.isEmpty()) {
				return;
			}

			boolean shouldUpdate = !capability.get().isFluidEqual(lastUpdateFluidStack);
			float delta = Math.abs(lastUpdateFluidStack.getAmount() - visualFluidAmount);
			shouldUpdate |= delta >= FLUID_UPDATE_THRESHOLD;
			shouldUpdate |= (updateTimer >= MAX_TICKS_BEFORE_UPDATE);

			if (shouldUpdate) {
				synchronizeServerToClient(capability.get().getFluid(), capability.get().getHeadPressure());
			} else {
				updateTimer++;
			}
		}
	}

	@Override
	public void updateBeforeRendering(float partialTicks) {
		if (lastUpdateFluidStack.getAmount() != visualFluidAmount) {
			float difference = visualFluidAmount - lastUpdateFluidStack.getAmount();
			visualFluidAmount -= difference * (partialTicks / MAX_TICKS_BEFORE_UPDATE);
		}
	}

	protected void synchronizeServerToClient(FluidStack fluid, float pressure) {
		if (!isClientSide()) {
			updateTimer = 0;
			lastUpdateFluidStack = fluid;
			visualFluidAmount = lastUpdateFluidStack.getAmount();
			FluidCableUpdatePacket packet = new FluidCableUpdatePacket(getPos(), lastUpdateFluidStack, pressure);
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 32, packet);
		}
	}

	public void recieveUpdateRenderValues(FluidStack fluid, float pressure) {
		if (isClientSide()) {
			// Make sure we're synced up with the amount from the previous sync.
			visualFluidAmount = lastUpdateFluidStack.getAmount();
			lastUpdateFluidStack = fluid;
			clientPressure = pressure;
			getBlockEntity().requestModelDataUpdate();
		}
	}

	public float getFilledPercentage() {
		if (isClientSide()) {
			return (float) lastUpdateFluidStack.getAmount() / capacity;
		} else {
			FluidNetworkModule module = getFluidModule().orElse(null);
			if (module != null) {
				Optional<FluidCableCapability> capability = getFluidCapability();
				if (capability.isPresent()) {
					return (float) capability.get().getFluidAmount() / capability.get().getCapacity();
				}
				return 0;
			}
			return 0;
		}
	}

	public float getVisualFilledPercentage() {
		return visualFluidAmount / capacity;
	}

	public Optional<FluidNetworkModule> getFluidModule() {
		return getNetworkModule(ModCableModules.Fluid.get());
	}

	public Optional<FluidCableCapability> getFluidCapability() {
		FluidNetworkModule module = getFluidModule().orElse(null);
		if (module == null) {
			return Optional.empty();
		}

		return module.getFluidCableCapability(getPos());
	}

	@Override
	protected void initializeCableProperties(Cable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		super.initializeCableProperties(cable, context, state, placer, stack);
		FluidCableCapability fluidCapability = ModCableCapabilities.Fluid.get().create(cable);
		fluidCapability.initialize(capacity, transferRate, pressureProperties, type);
		cable.registerCapability(fluidCapability);
	}

	@Override
	protected void onCableFirstAddedToNetwork(Cable cable, BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onCableFirstAddedToNetwork(cable, context, state, placer, stack);
		List<Direction> sidesToDisable = new ArrayList<>();

		FluidStack existingStack = FluidStack.EMPTY;
		boolean multipleFluids = false;
		for (Direction dir : Direction.values()) {
			BlockEntity te = getLevel().getBlockEntity(getPos().relative(dir));
			if (te == null) {
				continue;
			}

			IFluidHandler handler = te.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).orElse(null);
			if (handler == null || handler.getTanks() <= 0) {
				continue;

			}

			for (int i = 0; i < handler.getTanks(); i++) {
				if (handler.getFluidInTank(i).isEmpty()) {
					continue;
				}

				if (existingStack.isEmpty()) {
					existingStack = handler.getFluidInTank(0);
				} else if (!existingStack.isFluidEqual(handler.getFluidInTank(i))) {
					multipleFluids = true;
				}

				// We'll allow the pipe to connect to another fluid source if the player
				// indicated that's what they want.
				if (dir.getOpposite() != context.getClickedFace()) {
					sidesToDisable.add(dir);
				}
			}
		}

		if (multipleFluids) {
			for (Direction dir : sidesToDisable) {
				cable.setDisabledStateOnSide(dir, true);
			}
		}
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Fluid.get());
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the fluid capability if we are not disabled on that side.
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			if (side == null) {
				return LazyOptional.of(() -> this).cast();
			}

			boolean disabled = isSideDisabled(side);
			if (!disabled) {
				return LazyOptional.of(() -> capabilityWrapper.get(side)).cast();
			}
		} else if (cap == CapabilityStaticFluid.STATIC_FLUID_CAPABILITY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();

	}

	public CompoundTag serializeSaveNbt(CompoundTag nbt) {
		super.serializeSaveNbt(nbt);
		return nbt;
	}

	public void deserializeSaveNbt(CompoundTag nbt) {
		super.deserializeSaveNbt(nbt);
	}

	@Override
	public int fill(Direction direction, FluidStack resource, FluidAction action) {
		if (!getBlockEntity().getLevel().isClientSide) {
			FluidNetworkModule module = getFluidModule().orElse(null);
			if (module != null) {
				return module.fill(getPos(), resource, action);
			}
			return 0;
		} else {
			return 0;
		}
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		if (!getBlockEntity().getLevel().isClientSide) {
			Optional<FluidCableCapability> capability = getFluidCapability();
			if (capability.isPresent()) {
				return capability.get().getFluid();
			}
			return FluidStack.EMPTY;
		} else {
			return lastUpdateFluidStack;
		}
	}

	@Override
	public int getTankCapacity(int tank) {
		if (!getBlockEntity().getLevel().isClientSide) {
			Optional<FluidCableCapability> capability = getFluidCapability();
			if (capability.isPresent()) {
				return capability.get().getCapacity();
			}
			return 0;
		} else {
			return 0;
		}
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (!getBlockEntity().getLevel().isClientSide) {
			Optional<FluidCableCapability> capability = getFluidCapability();
			if (capability.isPresent()) {
				return capability.get().isFluidValid(stack);
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public int fill(FluidStack resource, float pressure, FluidAction action) {
		if (!getBlockEntity().getLevel().isClientSide) {
			FluidNetworkModule module = getFluidModule().orElse(null);
			if (module != null) {
				return module.fill(getPos(), resource, pressure, action);
			}
			return 0;
		} else {
			return 0;
		}
	}

	@Override
	public float getHeadPressure() {
		if (isClientSide()) {
			return clientPressure;
		}
		return getFluidCapability().get().getHeadPressure();
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
