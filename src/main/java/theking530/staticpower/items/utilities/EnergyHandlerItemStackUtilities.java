package theking530.staticpower.items.utilities;

import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;

/**
 * Library class containing useful functions to interact with an static volt
 * storing {@link ItemStack} from StaticPower.
 * 
 * @author Amine Sebastian
 *
 */
public class EnergyHandlerItemStackUtilities {

	public static int getRGBDurabilityForDisplay(ItemStack stack) {
		double hue = (170.0f / 360.0f) + (stack.getDamageValue()) * (40.0f / 360.0f);
		return Mth.hsvToRgb((float) hue, 1.0F, 1.0F);
	}

	/**
	 * Checks and returns if the provided {@link ItemStack} has the Energy storage
	 * capability.
	 * 
	 * @param container The itemstack to check.
	 * @return True if the itemstack contains the energy capability, false
	 *         otherwise.
	 */
	public static boolean isEnergyContainer(ItemStack container) {
		return container.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).isPresent();
	}

	/**
	 * Gets the IStaticVoltHandler associated with the provided container.
	 * 
	 * @param container The itemstack to check.
	 * @return The IStaticVoltHandler associated with the provided container.
	 */
	public static LazyOptional<IStaticVoltHandler> getEnergyContainer(ItemStack container) {
		return container.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY);
	}

	/**
	 * Helper method to set the amount of energy in an energy containing itemstack.
	 * This respects the itemstack's max stored energy property.
	 * 
	 * @param container The itemstack to add the energy to.
	 * @param energy    The amount of energy to set this itemstack's stored energy
	 *                  to.
	 */
	public static void setEnergy(ItemStack container, long energy) {
		container.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent((IStaticVoltHandler instance) -> {
			instance.receivePower(energy, false);
		});
	}

	/**
	 * Helper method to get the amount of energy in an energy containing itemstack.
	 * 
	 * @param container The itemstack to check.
	 */
	public static long getStoredPower(ItemStack container) {
		AtomicLong energy = new AtomicLong(0);
		container.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent((IStaticVoltHandler instance) -> {
			energy.set(instance.getStoredPower());
		});
		return energy.get();
	}

	/**
	 * Helper method to get the maximum amount of energy in an energy containing
	 * itemstack.
	 * 
	 * @param container The itemstack to check.
	 */
	public static long getCapacity(ItemStack container) {
		AtomicLong energy = new AtomicLong(0);
		container.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent((IStaticVoltHandler instance) -> {
			energy.set(instance.getCapacity());
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
	public static long receivePower(ItemStack container, long maxReceive, boolean simulate) {
		AtomicLong received = new AtomicLong(0);
		container.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent((IStaticVoltHandler instance) -> {
			received.set(instance.receivePower(maxReceive, simulate));
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
	public static long drainPower(ItemStack container, long maxExtract, boolean simulate) {
		AtomicLong extracted = new AtomicLong(0);
		container.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent((IStaticVoltHandler instance) -> {
			extracted.set(instance.drainPower(maxExtract, simulate));
		});
		return extracted.get();
	}
}
