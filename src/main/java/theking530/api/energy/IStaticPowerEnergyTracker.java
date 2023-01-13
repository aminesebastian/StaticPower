package theking530.api.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStaticPowerEnergyTracker extends INBTSerializable<CompoundTag> {
	/**
	 * Ticks this tracker. Implementers should ensure this is only called once per
	 * tick.
	 * 
	 * @param level
	 */
	public void tick(Level level);

	default public void powerTransfered(PowerStack stack) {
		powerAdded(stack);
		powerDrained(stack.getPower());
	}

	/**
	 * Increments the amount of power that was added to the owning energy storage
	 * this tick.
	 * 
	 * @param stack
	 */
	public void powerAdded(PowerStack stack);

	/**
	 * Increments the amount of power that was drained from the owning energy
	 * storage this tick.
	 * 
	 * @param power
	 */
	public void powerDrained(double power);

	/**
	 * Returns the average amount of power that was added per tick by the owning
	 * energy storage.
	 * 
	 * @return
	 */
	public double getAveragePowerAddedPerTick();

	/**
	 * Returns the average amount of power that was used per tick by the owning
	 * energy storage.
	 * 
	 * @return
	 */
	public double getAveragePowerDrainedPerTick();

	/**
	 * Gets the last received voltage by the owning energy storage. This represents
	 * the AVERAGE voltage that was received.
	 * 
	 * @return
	 */
	public StaticPowerVoltage getLastRecievedVoltage();

	/**
	 * Gets the amount of current that was received by the owning energy storage.
	 * 
	 * @return
	 */
	public double getLastRecievedCurrent();

	/**
	 * Gets the type of current that was last received by the owning energy storage.
	 * 
	 * @return
	 */
	public CurrentType getLastRecievedCurrentType();
}
