package theking530.staticpower.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStaticVoltHandler extends INBTSerializable<CompoundNBT> {
	/**
	 * Returns the amount of power currently stored in this IStaticVoltHandler
	 * 
	 * @return
	 */
	public int getStoredCharge();

	/**
	 * Returns the maximum amount of power that can be stored in this
	 * IStaticVoltHandler
	 * 
	 * @return
	 */
	public int getMaximumCharge();

	public int recievePower(int voltage, int current, boolean simulate);

	public int drainPower(int charge, boolean simulate);

	public boolean canRecievePower();

	public boolean canDrainPower();

	public int getMinimumInputVoltage();

	public int getMaximumInputVoltage();
}