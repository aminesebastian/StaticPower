package theking530.api.heat;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHeatStorage {
	/**
	 * Returns the amount of heat currently stored in this heatable entity.
	 * 
	 * @return
	 */
	public double getCurrentHeat();

	/**
	 * Returns the maximum amount of heat that can be stored in this heatable
	 * entity.
	 * 
	 * @return
	 */
	public double getMaximumHeat();

	/**
	 * Gets the maximum rate that this heatable entity can transfer thermal energy.
	 * 
	 * @return
	 */
	public double getConductivity();

	/**
	 * Adds heat to this heatable entity.
	 * 
	 * @param heatToRecieve
	 * @param simulate
	 * @return
	 */
	public double heat(double amountToHeat, boolean simulate);

	/**
	 * Cools down this heatable entity.
	 * 
	 * @param amountToCool
	 * @param simulate
	 * @return
	 */
	public double cool(double amountToCool, boolean simulate);

	/**
	 * Transfers the heat stored in this storage to adjacent blocks. The transfered
	 * amount is equal to the thermal conductivity of the adjacent block multiplied
	 * by the maximum heat transfer rate of this storage.
	 * 
	 * @param reader     The world access.
	 * @param currentPos The position of this heat storage.
	 */
	public default void transferWithSurroundings(World world, BlockPos currentPos) {
		for (Direction dir : Direction.values()) {
			HeatStorageUtilities.transferHeatPassivelyWithBlockFromDirection(world, currentPos, dir, this);
			HeatStorageUtilities.transferHeatActivelyWithBlockFromDirection(world, currentPos, dir, this);
		}
	}
}