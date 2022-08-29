package theking530.api.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IHeatStorage {
	public static final int STONE_MELTING_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(1000);
	public static final int WATER_BOILING_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(100);
	public static final int ROOM_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(20);
	public static final int WATER_FREEZING_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(0);

	/**
	 * Returns the amount of heat currently stored in this heatable entity.
	 * 
	 * @return
	 */
	public int getCurrentHeat();

	/**
	 * Returns the maximum amount of heat that can be stored in this heatable
	 * entity. After this point, it can no longer take heat. Think of this as the
	 * transition point where the storage could no longer exist in its current
	 * state. Some possible uses for this could be to explode when over-heated or to
	 * transition states.
	 * 
	 * @return
	 */
	public int getOverheatThreshold();
	
	/**
	 * Gets the maximum rate that this heatable entity can transfer thermal energy.
	 * 
	 * @return
	 */
	public default float getConductivity() {
		return 1.0f;
	}

	/**
	 * Adds heat to this heatable entity.
	 * 
	 * @param heatToRecieve
	 * @param simulate
	 * @return
	 */
	public int heat(int amountToHeat, boolean simulate);

	/**
	 * Cools down this heatable entity.
	 * 
	 * @param amountToCool
	 * @param simulate
	 * @return
	 */
	public int cool(int amountToCool, boolean simulate);

	/**
	 * Transfers the heat stored in this storage to adjacent blocks. The transfered
	 * amount is equal to the thermal conductivity of the adjacent block multiplied
	 * by the maximum heat transfer rate of this storage.
	 * 
	 * @param reader     The world access.
	 * @param currentPos The position of this heat storage.
	 */
	public default void transferWithSurroundings(Level world, BlockPos currentPos) {
		HeatStorageUtilities.transferHeatWithSurroundings(this, world, currentPos);
	}
}