package theking530.staticpower.items;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/***
 * Template capability provider for itemstacks that require energy storage.
 * 
 * @author Amine Sebastian
 *
 */
public class EnergyHandlerItemStack implements IEnergyStorage, ICapabilityProvider {
	public static final String STORED_ENERGY_NBT_KEY = "StoredEnergy";
	public static final String MAX_ENERGY_NBT_KEY = "MaxEnergy";
	public static final String MAX_RECEIVE_ENERGY_NBT_KEY = "MaxReceieveEnery";
	public static final String MAX_DRAIN_ENERGY_NBT_KEY = "MaxDrainEnergy";

	private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);

	@Nonnull
	protected ItemStack container;

	public EnergyHandlerItemStack(@Nonnull ItemStack container, int capacity, int maxRecieve, int maxDrain) {
		this.container = container;
		this.container.getTag().putInt(STORED_ENERGY_NBT_KEY, 0);
		this.container.getTag().putInt(MAX_ENERGY_NBT_KEY, capacity);
		this.container.getTag().putInt(MAX_RECEIVE_ENERGY_NBT_KEY, maxRecieve);
		this.container.getTag().putInt(MAX_DRAIN_ENERGY_NBT_KEY, maxDrain);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
		return CapabilityEnergy.ENERGY.orEmpty(capability, holder);
	}

	/**
	 * Receives energy into this container.
	 */
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int max = getMaxEnergyStored();
		int current = getEnergyStored();
		int candidateReceive = Math.min(maxReceive, max - current);
		int received = Math.min(candidateReceive, getMaxReceiveRate());
		if (!simulate) {
			container.getTag().putInt(STORED_ENERGY_NBT_KEY, getEnergyStored() + received);
		}
		return received;
	}

	/**
	 * Drains energy from this container.
	 */
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		// Get the amount of stored energy.
		int current = getEnergyStored();

		// If the amount of energy we currently have stored is 0, just return early with
		// a 0.
		if (current == 0) {
			return 0;
		}

		// Otherwise, calculate the amount of energy we can drain, and perform the drain
		// if simulate is false.
		int candidateDrain = Math.min(maxExtract, current);
		int drained = Math.min(candidateDrain, getMaxDrainRate());
		if (!simulate) {
			container.getTag().putInt(STORED_ENERGY_NBT_KEY, getEnergyStored() - drained);
		}
		return drained;
	}

	/**
	 * Gets the current amount of energy inside this container.
	 */
	@Override
	public int getEnergyStored() {
		CompoundNBT tagCompound = container.getTag();
		if (tagCompound != null && tagCompound.contains(STORED_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(STORED_ENERGY_NBT_KEY);
		}
		return 0;
	}

	/**
	 * Gets the maximum amount of energy this container can store.
	 */
	@Override
	public int getMaxEnergyStored() {
		CompoundNBT tagCompound = container.getTag();
		if (tagCompound != null && tagCompound.contains(MAX_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(MAX_ENERGY_NBT_KEY);
		}
		return 0;
	}

	/**
	 * Indicates if this container can be drained of energy.
	 */
	@Override
	public boolean canExtract() {
		return getEnergyStored() > 0;
	}

	/**
	 * Indicates if this container can receive energy.
	 */
	@Override
	public boolean canReceive() {
		return getEnergyStored() < getMaxEnergyStored();
	}

	/**
	 * Gets the maximum amount of energy this container can receive per tick.
	 * 
	 * @return
	 */
	public int getMaxReceiveRate() {
		CompoundNBT tagCompound = container.getTag();
		if (tagCompound != null && tagCompound.contains(MAX_RECEIVE_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(MAX_RECEIVE_ENERGY_NBT_KEY);
		}
		return 0;
	}

	/**
	 * Gets the maximum amount of energy that can be drained by this container per
	 * tick.
	 * 
	 * @return
	 */
	public int getMaxDrainRate() {
		CompoundNBT tagCompound = container.getTag();
		if (tagCompound != null && tagCompound.contains(MAX_DRAIN_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(MAX_DRAIN_ENERGY_NBT_KEY);
		}
		return 0;
	}
	
	
	
	
	/**
	 * Checks to see if the provided {@link ItemStack} is a valid energy container.
	 * 
	 * @param container The itemstack to check.
	 * @return True if the itemstack is a valid energy containing itemstack, false
	 *         otherwise.
	 */
	public static boolean isValidEnergyContainingItemstack(ItemStack container) {
		return container.hasTag() && container.getTag().contains(EnergyHandlerItemStack.MAX_DRAIN_ENERGY_NBT_KEY) && container.getTag().contains(EnergyHandlerItemStack.MAX_RECEIVE_ENERGY_NBT_KEY)
				&& container.getTag().contains(EnergyHandlerItemStack.MAX_ENERGY_NBT_KEY) && container.getTag().contains(EnergyHandlerItemStack.STORED_ENERGY_NBT_KEY);
	}

	/**
	 * Helper method to set the amount of energy in an energy containing itemstack.
	 * This respects the itemstack's max stored energy property.
	 * 
	 * @param container The itemstack to add the energy to.
	 * @param energy    The amount of energy to set this itemstack's stored energy
	 *                  to.
	 */
	public static void setEnergy(ItemStack container, int energy) {
		int maxEnergy = container.getTag().getInt(EnergyHandlerItemStack.MAX_ENERGY_NBT_KEY);
		container.getTag().putInt(EnergyHandlerItemStack.STORED_ENERGY_NBT_KEY, Math.min(maxEnergy, energy));
		updateDamageUsingEnergyStorage(container);
	}

	/**
	 * Helper method to get the amount of energy in an energy containing itemstack.
	 * 
	 * @param container The itemstack to check.
	 */
	public static int getEnergyStored(ItemStack container) {
		AtomicInteger energy = new AtomicInteger(0);
		container.getCapability(CapabilityEnergy.ENERGY).ifPresent((IEnergyStorage instance) -> {
			energy.set(instance.getEnergyStored());
		});
		return energy.get();
	}

	/**
	 * Helper method to get the maximum amount of energy in an energy containing
	 * itemstack.
	 * 
	 * @param container The itemstack to check.
	 */
	public static int getEnergyStorageCapacity(ItemStack container) {
		AtomicInteger energy = new AtomicInteger(0);
		container.getCapability(CapabilityEnergy.ENERGY).ifPresent((IEnergyStorage instance) -> {
			energy.set(instance.getMaxEnergyStored());
		});
		return energy.get();
	}

	/**
	 * Helper method to add energy to an energy containing itemstack.
	 * 
	 * @param container  The itemstack to add the energy to.
	 * @param maxReceive The amount of energy to add.
	 * @param simulate   If true, the process will only be simulated.
	 * @return The actual amount of energy added.
	 */
	public static int addEnergyToItemstack(ItemStack container, int maxReceive, boolean simulate) {
		AtomicInteger received = new AtomicInteger(0);
		container.getCapability(CapabilityEnergy.ENERGY).ifPresent((IEnergyStorage instance) -> {
			received.set(instance.receiveEnergy(maxReceive, simulate));
			if (!simulate) {
				updateDamageUsingEnergyStorage(container);
			}
		});
		return received.get();
	}

	/**
	 * Helper method to use energy from an energy containing itemstack.
	 * 
	 * @param container  The itemstack to add the energy to.
	 * @param maxExtract The amount of energy to drain.
	 * @param simulate   If true, the process will only be simulated.
	 * @return The actual amount of energy drained.
	 */
	public static int useEnergyFromItemstack(ItemStack container, int maxExtract, boolean simulate) {
		AtomicInteger extracted = new AtomicInteger(0);
		container.getCapability(CapabilityEnergy.ENERGY).ifPresent((IEnergyStorage instance) -> {
			extracted.set(instance.receiveEnergy(maxExtract, simulate));
			if (!simulate) {
				updateDamageUsingEnergyStorage(container);
			}
		});
		return extracted.get();
	}

	/**
	 * Updates the amount of damage on the itemstack based on the amount of energy
	 * stored vs the maximum amount that can be stored on this itemstack.
	 * 
	 * @param container The itemstack to update.
	 */
	public static void updateDamageUsingEnergyStorage(ItemStack container) {
		int maxEnergy = getEnergyStorageCapacity(container);
		int energy = getEnergyStored(container);
		container.setDamage((maxEnergy - energy) / container.getMaxDamage());
	}
}