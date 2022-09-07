package theking530.staticpower.items.utilities;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.ItemStackStaticPowerEnergyCapability;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;

/**
 * Library class containing useful functions to interact with an static power
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
		return container.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).isPresent();
	}

	/**
	 * Gets the {@link IStaticPowerStorage} associated with the provided container.
	 * Note that this returns an instance of
	 * {@link ItemStackStaticPowerEnergyCapability}. If the itemstack was not
	 * initialized with an instance of {@link ItemStackStaticPowerEnergyCapability},
	 * this will fail.
	 * 
	 * @param container The itemstack to check.
	 * @return The {@link ItemStackStaticPowerEnergyCapability} associated with the
	 *         provided container.
	 */
	public static LazyOptional<ItemStackStaticPowerEnergyCapability> getEnergyContainer(ItemStack container) {
		return container.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).cast();
	}

	public static void setStoredPower(ItemStack container, double power) {
		EnergyHandlerItemStackUtilities.getEnergyContainer(container).ifPresent((cap) -> {
			cap.setStoredPower(Double.MAX_VALUE);
		});
	}

	public static StaticVoltageRange getInputVoltageRange(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return StaticVoltageRange.ZERO_VOLTAGE;
		}
		return cap.getInputVoltageRange();
	}

	public static double getMaximumCurrentInput(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return 0;
		}
		return cap.getMaximumCurrentInput();
	}

	public static double getStoredPower(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return 0;
		}
		return cap.getStoredPower();
	}

	public static double getCapacity(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return 0;
		}
		return cap.getCapacity();
	}

	public static double getVoltageOutput(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return 0;
		}
		return cap.getVoltageOutput();
	}

	public static double getMaximumCurrentOutput(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return 0;
		}
		return cap.getMaximumCurrentOutput();
	}

	public static double addPower(ItemStack container, double voltage, double power, boolean simulate) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return 0;
		}
		return cap.addPower(voltage, power, simulate);
	}

	public static double usePower(ItemStack container, double power, boolean simulate) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return 0;
		}
		return cap.drainPower(power, simulate);
	}

	public static boolean canAcceptPower(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return false;
		}
		return cap.canAcceptPower();
	}

	public static boolean doesProvidePower(ItemStack container) {
		ItemStackStaticPowerEnergyCapability cap = EnergyHandlerItemStackUtilities.getEnergyContainer(container).orElse(null);
		if (cap == null) {
			return false;
		}
		return cap.doesProvidePower();
	}
}
