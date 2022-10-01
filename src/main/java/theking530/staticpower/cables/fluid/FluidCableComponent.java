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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.drain.DrainAttachment;
import theking530.staticpower.cables.attachments.extractor.ExtractorAttachment;
import theking530.staticpower.cables.attachments.sprinkler.SprinklerAttachment;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class FluidCableComponent extends AbstractCableProviderComponent implements IFluidHandler {
	public static final String FLUID_INDUSTRIAL_DATA_TAG_KEY = "fluid_cable_industrial";
	// This is intentionally high, we only want to force update if the difference is
	// VAST, otherwise, let the MAX_TICKS driven update do the work.
	public static final float FLUID_UPDATE_THRESHOLD = 100;
	public static final float MAX_TICKS_BEFORE_UPDATE = 10;

	private final int capacity;
	private final int transferRate;
	private final boolean isIndustrial;
	private FluidStack lastUpdateFluidStack;
	private float lastUpdateFilledPercentage;
	private float visualFilledPercentage;
	private int subThresholdUpdateTime;

	public FluidCableComponent(String name, boolean isIndustrial, int capacity, int transferRate) {
		super(name, ModCableModules.Fluid.get());
		this.capacity = capacity;
		this.transferRate = transferRate;
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
			boolean shouldUpdate = !capability.get().getFluidStorage().getFluid().isFluidEqual(lastUpdateFluidStack);
			int delta = Math.abs(lastUpdateFluidStack.getAmount() - getFluidInTank(0).getAmount());
			if (delta > FLUID_UPDATE_THRESHOLD) {
				shouldUpdate = true;
			} else if (!capability.get().getFluidStorage().getFluid().isEmpty()) {
				subThresholdUpdateTime++;
			}

			shouldUpdate |= subThresholdUpdateTime >= MAX_TICKS_BEFORE_UPDATE;
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
			visualFilledPercentage -= difference * (partialTicks / 20.0f);
		}
	}

	protected void synchronizeServerToClient() {
		if (!isClientSide()) {
			Optional<FluidCableCapability> capability = getFluidCapability();
			if (capability.isEmpty()) {
				return;
			}

			lastUpdateFluidStack = capability.get().getFluidStorage().getFluid().copy();
			lastUpdateFilledPercentage = Math.min(1.0f, getFilledPercentage());
			FluidCableUpdatePacket packet = new FluidCableUpdatePacket(getPos(), lastUpdateFluidStack, lastUpdateFilledPercentage);
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 32, packet);
		}
	}

	public void recieveUpdateRenderValues(FluidStack stack, float fluidAmount) {
		if (isClientSide()) {
			lastUpdateFluidStack = stack;
			lastUpdateFilledPercentage = Math.min(1.0f, fluidAmount);
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
					return (float) capability.get().getFluidStorage().getFluidAmount() / capability.get().getFluidStorage().getCapacity();
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
	protected void initializeCableProperties(ServerCable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		FluidCableCapability fluidCapability = ModCableCapabilities.Fluid.get().create(cable);
		fluidCapability.initialize(capacity, transferRate, isIndustrial);
		cable.registerCapability(fluidCapability);
	}

	@Override
	protected void onCableFirstAddedToNetwork(ServerCable cable, BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onCableFirstAddedToNetwork(cable, context, state, placer, stack);
		List<Direction> sidesToDisable = new ArrayList<>();

		FluidStack existingStack = FluidStack.EMPTY;
		boolean multipleFluids = false;
		for (Direction dir : Direction.values()) {
			BlockEntity te = getLevel().getBlockEntity(getPos().relative(dir));
			if (te == null) {
				continue;
			}

			IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite()).orElse(null);
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
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		if (!getTileEntity().getLevel().isClientSide) {
			Optional<FluidCableCapability> capability = getFluidCapability();
			if (capability.isPresent()) {
				return capability.get().getFluidStorage().getFluid();
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
				return capability.get().getFluidStorage().getCapacity();
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
				return capability.get().getFluidStorage().isFluidValid(stack);
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
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
				if (isClientSide()) {
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
