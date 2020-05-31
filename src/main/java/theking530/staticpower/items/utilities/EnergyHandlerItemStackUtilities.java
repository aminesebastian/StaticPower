package theking530.staticpower.items.utilities;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Library class containing useful functions to interact with an energy storing
 * {@link ItemStack} from StaticPower.
 * 
 * @author Amine Sebastian
 *
 */
public class EnergyHandlerItemStackUtilities {
	/** Top level tag name for energy capability serialization. */
	public static final String ENERGY_STORAGE_NBT_KEY = "StaticPowerEnergy";
	/** Tag for the amount of energy stored in the item. */
	public static final String CURRENT_ENERGY_NBT_KEY = "StoredEnergy";
	/** Tag for the maximum amount of energy that can be stored in the item. */
	public static final String MAX_ENERGY_NBT_KEY = "MaxEnergy";
	/** Tag for the maximum amount of energy that can be received per tick. */
	public static final String MAX_RECEIVE_ENERGY_NBT_KEY = "MaxReceieveEnery";
	/** Tag for the maximum amount of energy that can be drained per tick. */
	public static final String MAX_DRAIN_ENERGY_NBT_KEY = "MaxDrainEnergy";

	/**
	 * Gets the energy storage nbt tag.
	 * 
	 * @return The energy NBT tag.
	 */
	public static CompoundNBT getEnergyStorageNBTTag(ItemStack itemstack) {
		if (itemstack.hasTag()) {
			if (itemstack.getTag().contains(ENERGY_STORAGE_NBT_KEY)) {
				return itemstack.getTag().getCompound(ENERGY_STORAGE_NBT_KEY);
			}
		}
		return null;
	}

	/**
	 * Checks to see if the provided {@link ItemStack} is a valid energy container.
	 * 
	 * @param container The itemstack to check.
	 * @return True if the itemstack is a valid energy containing itemstack, false
	 *         otherwise.
	 */
	public static boolean isValidStaticPowerEnergyContainingItemstack(ItemStack container) {
		return container.hasTag() && container.getTag().contains(MAX_DRAIN_ENERGY_NBT_KEY) && container.getTag().contains(MAX_RECEIVE_ENERGY_NBT_KEY) && container.getTag().contains(MAX_ENERGY_NBT_KEY)
				&& container.getTag().contains(CURRENT_ENERGY_NBT_KEY);
	}

	/**
	 * Checks and returns if the provided {@link ItemStack} has the Energy storage
	 * capability.
	 * 
	 * @param container The itemstack to check.
	 * @return True if the itemstack contains the energy capability, false otherwise.
	 */
	public static boolean isEnergyContainer(ItemStack container) {
		AtomicBoolean exists = new AtomicBoolean(false);
		container.getCapability(CapabilityEnergy.ENERGY).ifPresent((IEnergyStorage instance) -> {
			exists.set(true);
		});
		return exists.get();
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
		int maxEnergy = container.getTag().getInt(MAX_ENERGY_NBT_KEY);
		container.getTag().putInt(CURRENT_ENERGY_NBT_KEY, Math.min(maxEnergy, energy));
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
			extracted.set(instance.extractEnergy(maxExtract, simulate));
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
		// Ensure the container has the appropriate energy tag.
		CompoundNBT energyTag = getEnergyStorageNBTTag(container);
		if (energyTag == null) {
			return;
		}
		// Set the damage to the max amount of energy - the current amount (when fully
		// charged, the damage will be 0, and vice versa).
		container.setDamage(energyTag.getInt(MAX_ENERGY_NBT_KEY) - energyTag.getInt(CURRENT_ENERGY_NBT_KEY));
	}
}
