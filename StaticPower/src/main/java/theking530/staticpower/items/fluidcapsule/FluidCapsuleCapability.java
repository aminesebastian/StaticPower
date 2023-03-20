package theking530.staticpower.items.fluidcapsule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.staticcore.StaticCoreConfig;

public class FluidCapsuleCapability implements IFluidHandlerItem, ICapabilityProvider {

	/** Top level tag name for fluid capability serialization. */
	public static final String FLUID_STORAGE_NBT_KEY = "StaticPowerFluid";
	/** The fluid stack currently stored in the item. */
	public static final String STORED_FLUID_NBT_KEY = "StoredFluid";
	/** Tag for the tier of the capsule. */
	public static final String TIER_NBT_KEY = "Tier";
	/** Tag to indicate if this is a creative capability. */
	public static final String CREATIVE_NBT_KEY = "Creative";

	private final LazyOptional<IFluidHandlerItem> lazyOfThis = LazyOptional.of(() -> this);
	@Nonnull
	protected ItemStack container;
	protected boolean isCreative;

	public FluidCapsuleCapability(@Nonnull ItemStack container, ResourceLocation tier, boolean isCreative) {
		this.container = container;
		this.isCreative = isCreative;

		// If the item does not have the proper tags, initialize them.
		if (!container.getOrCreateTag().contains(FLUID_STORAGE_NBT_KEY)) {
			CompoundTag fluidContainerNBT = new CompoundTag();

			CompoundTag fluidTag = new CompoundTag();
			FluidStack.EMPTY.writeToNBT(fluidTag);
			fluidContainerNBT.put(STORED_FLUID_NBT_KEY, fluidTag);
			fluidContainerNBT.putString(TIER_NBT_KEY, tier.toString());
			fluidContainerNBT.putBoolean(CREATIVE_NBT_KEY, isCreative);
			container.getTag().put(FLUID_STORAGE_NBT_KEY, fluidContainerNBT);
		}
	}

	@Nonnull
	@Override
	public ItemStack getContainer() {
		return container;
	}

	public boolean canFillFluidType(FluidStack fluidIn) {
		return getFluid().isEmpty() || fluidIn.isFluidEqual(getFluid());
	}

	@Nonnull
	public FluidStack getFluid() {
		FluidStack output = FluidStack.loadFluidStackFromNBT(container.getTag().getCompound(FLUID_STORAGE_NBT_KEY).getCompound(STORED_FLUID_NBT_KEY));
		if (isCreative) {
			if (!output.isEmpty()) {
				output.setAmount(Integer.MAX_VALUE);
			}
		}
		return output;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Nonnull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		ResourceLocation tier = new ResourceLocation(container.getTag().getCompound(FLUID_STORAGE_NBT_KEY).getString(TIER_NBT_KEY));
		return StaticCoreConfig.getTier(tier).capsuleCapacity.get();
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return canFillFluidType(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		FluidStack containedFluid = getFluid();

		if (resource == null || resource.isEmpty() || !containedFluid.isEmpty() && !resource.isFluidEqual(containedFluid)) {
			return 0;
		}

		int maxFill = getTankCapacity(0) - containedFluid.getAmount();
		int resourceUsedAmount = Math.min(maxFill, resource.getAmount());

		if (action.execute()) {
			if (containedFluid.isEmpty()) {
				containedFluid = resource.copy();
				if (!isCreative) {
					containedFluid.setAmount(resourceUsedAmount);
				} else {
					containedFluid.setAmount(Integer.MAX_VALUE);
				}
			} else {
				containedFluid.grow(resourceUsedAmount);
			}

			// Update the fluid in the NBT.
			CompoundTag fluidTag = new CompoundTag();
			containedFluid.writeToNBT(fluidTag);
			container.getTag().getCompound(FLUID_STORAGE_NBT_KEY).put(STORED_FLUID_NBT_KEY, fluidTag);
			updateDamageUsingFluid();
		}

		return resourceUsedAmount;
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		FluidStack containedFluid = getFluid();

		if (resource == null || resource.isEmpty() || containedFluid.isEmpty()) {
			return FluidStack.EMPTY;
		}

		if (!resource.isFluidEqual(containedFluid)) {
			return FluidStack.EMPTY;
		}

		int drainAmount = Math.min(containedFluid.getAmount(), resource.getAmount());

		FluidStack output = containedFluid.copy();
		output.setAmount(drainAmount);
		if (action.execute() && !isCreative) {
			containedFluid.shrink(drainAmount);

			// Update the fluid in the NBT.
			CompoundTag fluidTag = new CompoundTag();
			containedFluid.writeToNBT(fluidTag);
			container.getTag().getCompound(FLUID_STORAGE_NBT_KEY).put(STORED_FLUID_NBT_KEY, fluidTag);
			updateDamageUsingFluid();
		}

		return output;
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		FluidStack containedFluid = getFluid();

		if (maxDrain == 0 || containedFluid.isEmpty()) {
			return FluidStack.EMPTY;
		}

		int drainAmount = Math.min(containedFluid.getAmount(), maxDrain);

		FluidStack output = containedFluid.copy();
		output.setAmount(drainAmount);
		if (action.execute() && !isCreative) {
			containedFluid.shrink(drainAmount);

			// Update the fluid in the NBT.
			CompoundTag fluidTag = new CompoundTag();
			containedFluid.writeToNBT(fluidTag);
			container.getTag().getCompound(FLUID_STORAGE_NBT_KEY).put(STORED_FLUID_NBT_KEY, fluidTag);
			updateDamageUsingFluid();
		}

		return output;
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability != null && capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, lazyOfThis);
		}
		return LazyOptional.empty();
	}

	public void updateDamageUsingFluid() {

	}
}