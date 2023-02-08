package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class FluidCableComponent extends AbstractCableProviderComponent implements ISidedFluidHandler, IStaticPowerFluidHandler {
	public static final String FLUID_INDUSTRIAL_DATA_TAG_KEY = "fluid_cable_industrial";
	// This is intentionally high, we only want to force update if the difference is
	// VAST, otherwise, let the MAX_TICKS driven update do the work.
	public static final float FLUID_UPDATE_THRESHOLD = 10;
	public static final float MAX_TICKS_BEFORE_UPDATE = 10;

	private final SidedFluidHandlerCapabilityWrapper capabilityWrapper;
	private final int capacity;
	private final int transferRate;
	private final boolean isIndustrial;
	private FluidStack lastUpdateFluidStack;
	private float lastUpdateFilledPercentage;
	private float visualFilledPercentage;
	private float clientPressure;
	private int subThresholdUpdateTime;
	public Map<Direction, Float> flowMap;

	public FluidCableComponent(String name, boolean isIndustrial, int capacity, int transferRate) {
		super(name, ModCableModules.Fluid.get());
		this.capacity = capacity;
		this.transferRate = transferRate;
		this.flowMap = new HashMap<>();
		for (Direction dir : Direction.values()) {
			flowMap.put(dir, 0.0f);
		}

		capabilityWrapper = new SidedFluidHandlerCapabilityWrapper(this);
		lastUpdateFluidStack = FluidStack.EMPTY;
		lastUpdateFilledPercentage = 0.0f;
		visualFilledPercentage = 0.0f;
		this.isIndustrial = isIndustrial;

		// Only non-industrial pipes can have attachments.
		if (!isIndustrial) {
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
			int delta = Math.abs(lastUpdateFluidStack.getAmount() - getFluidInTank(0).getAmount());
			shouldUpdate |= delta > FLUID_UPDATE_THRESHOLD;
			shouldUpdate |= subThresholdUpdateTime >= MAX_TICKS_BEFORE_UPDATE;
			subThresholdUpdateTime++;
			if (shouldUpdate) {
				synchronizeServerToClient();
				subThresholdUpdateTime = 0;
			}
		}
	}

	@Override
	public void updateBeforeRendering(float partialTicks) {
		if (visualFilledPercentage != lastUpdateFilledPercentage) {
			float difference = visualFilledPercentage - lastUpdateFilledPercentage;
			visualFilledPercentage -= difference * (partialTicks / 10.0f);
		}
	}

	protected void synchronizeServerToClient() {
		if (!isClientSide()) {
			Optional<FluidCableCapability> capability = getFluidCapability();
			if (capability.isEmpty()) {
				return;
			}

			lastUpdateFluidStack = capability.get().getFluid();
			lastUpdateFilledPercentage = Math.min(1.0f, getFilledPercentage());

			CompoundTag data = new CompoundTag();
			CompoundTag fluid = new CompoundTag();
			lastUpdateFluidStack.writeToNBT(fluid);
			data.put("f", fluid);
			data.putFloat("%", lastUpdateFilledPercentage);
			data.putFloat("p", capability.get().getHeadPressure());
			FluidCableUpdatePacket packet = new FluidCableUpdatePacket(getPos(), data);
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 32, packet);
		}
	}

	public void recieveUpdateRenderValues(CompoundTag data) {
		if (isClientSide()) {
			lastUpdateFluidStack = FluidStack.loadFluidStackFromNBT(data.getCompound("f"));
			lastUpdateFilledPercentage = Math.min(1.0f, data.getFloat("%"));
			clientPressure = data.getFloat("p");
			getTileEntity().requestModelDataUpdate();
		}
	}

	public float getFilledPercentage() {
		if (isClientSide()) {
			return lastUpdateFilledPercentage;
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
		return visualFilledPercentage;
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
		fluidCapability.initialize(capacity, transferRate, isIndustrial);
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

	@Override
	public int fill(Direction direction, FluidStack resource, FluidAction action) {
		if (!getTileEntity().getLevel().isClientSide) {
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
		if (!getTileEntity().getLevel().isClientSide) {
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
		if (!getTileEntity().getLevel().isClientSide) {
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
		if (!getTileEntity().getLevel().isClientSide) {
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
		if (!getTileEntity().getLevel().isClientSide) {
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
