package theking530.staticpower.items;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

/***
 * Template capability provider for any {@link ItemStack] that requires energy storage.
 * 
 * @author Amine Sebastian
 *
 */
public class EnergyHandlerItemStack implements IEnergyStorage, ICapabilityProvider {
	/** Lazy optional wrapper for this class. */
	private final LazyOptional<IEnergyStorage> holder;

	/** Handle to the container {@link ItemStack} this class represents. */
	@Nonnull
	protected ItemStack container;

	/**
	 * Initializes the provided container {@link ItemStack} with energy
	 * capabilities.
	 * 
	 * @param container  The {@link ItemStack} to add energy storage capabilities
	 *                   to.
	 * @param capacity   The default initial capacity of the energy storage item.
	 * @param maxReceive The maximum amount of energy that can be drained from the
	 *                   item per tick.
	 * @param maxDrain   The maximum amount of energy that be received by the item.
	 */
	public EnergyHandlerItemStack(@Nonnull ItemStack container, int capacity, int maxReceive, int maxDrain) {
		this.holder = LazyOptional.of(() -> this);
		this.container = container;
		initializeContainer(capacity, maxReceive, maxDrain);
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
			EnergyHandlerItemStackUtilities.getEnergyStorageNBTTag(container).putInt(EnergyHandlerItemStackUtilities.CURRENT_ENERGY_NBT_KEY, getEnergyStored() + received);
			EnergyHandlerItemStackUtilities.updateDamageUsingEnergyStorage(container);
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
			EnergyHandlerItemStackUtilities.getEnergyStorageNBTTag(container).putInt(EnergyHandlerItemStackUtilities.CURRENT_ENERGY_NBT_KEY, getEnergyStored() - drained);
			EnergyHandlerItemStackUtilities.updateDamageUsingEnergyStorage(container);
		}
		return drained;
	}

	/**
	 * Gets the current amount of energy inside this container.
	 */
	@Override
	public int getEnergyStored() {
		CompoundNBT tagCompound = EnergyHandlerItemStackUtilities.getEnergyStorageNBTTag(container);
		if (tagCompound != null && tagCompound.contains(EnergyHandlerItemStackUtilities.CURRENT_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(EnergyHandlerItemStackUtilities.CURRENT_ENERGY_NBT_KEY);
		}
		return 0;
	}

	/**
	 * Gets the maximum amount of energy this container can store.
	 */
	@Override
	public int getMaxEnergyStored() {
		CompoundNBT tagCompound = EnergyHandlerItemStackUtilities.getEnergyStorageNBTTag(container);
		if (tagCompound != null && tagCompound.contains(EnergyHandlerItemStackUtilities.MAX_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(EnergyHandlerItemStackUtilities.MAX_ENERGY_NBT_KEY);
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
		CompoundNBT tagCompound = EnergyHandlerItemStackUtilities.getEnergyStorageNBTTag(container);
		if (tagCompound != null && tagCompound.contains(EnergyHandlerItemStackUtilities.MAX_RECEIVE_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(EnergyHandlerItemStackUtilities.MAX_RECEIVE_ENERGY_NBT_KEY);
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
		CompoundNBT tagCompound = EnergyHandlerItemStackUtilities.getEnergyStorageNBTTag(container);
		if (tagCompound != null && tagCompound.contains(EnergyHandlerItemStackUtilities.MAX_DRAIN_ENERGY_NBT_KEY)) {
			return tagCompound.getInt(EnergyHandlerItemStackUtilities.MAX_DRAIN_ENERGY_NBT_KEY);
		}
		return 0;
	}

	/**
	 * Creates the default nbt tag to be added to the itemstack container.
	 * 
	 * @return The energy NBT tag.
	 */
	private CompoundNBT createDefaultNBT(int initialEnergy, int capacity, int maxReceive, int maxDrain) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(EnergyHandlerItemStackUtilities.CURRENT_ENERGY_NBT_KEY, 0);
		nbt.putInt(EnergyHandlerItemStackUtilities.MAX_ENERGY_NBT_KEY, capacity);
		nbt.putInt(EnergyHandlerItemStackUtilities.MAX_RECEIVE_ENERGY_NBT_KEY, maxReceive);
		nbt.putInt(EnergyHandlerItemStackUtilities.MAX_DRAIN_ENERGY_NBT_KEY, maxDrain);
		return nbt;
	}

	/**
	 * Initializes the container with the nbt required to store energy information
	 * if it is not already present. If already present, does nothing.
	 * 
	 * @param capacity   The initial capacity.
	 * @param maxReceive The maximum amount of energy that can be gained per tick.
	 * @param maxDrain   The maximum amount of energy that can be drained per tick.
	 */
	private void initializeContainer(int capacity, int maxReceive, int maxDrain) {
		// If this ItemStack does not have an nbt tag, make one.
		if (container.getTag() == null) {
			container.setTag(new CompoundNBT());
		}

		// Create the default energy tag if one doesnt exist on this item, and then add
		// it as a sub tag to the item's
		// nbt.
		if (EnergyHandlerItemStackUtilities.getEnergyStorageNBTTag(container) == null) {
			CompoundNBT nbt = createDefaultNBT(0, capacity, maxReceive, maxDrain);
			container.getTag().put(EnergyHandlerItemStackUtilities.ENERGY_STORAGE_NBT_KEY, nbt);
			EnergyHandlerItemStackUtilities.updateDamageUsingEnergyStorage(container);
		}
	}
}