package theking530.api.heat;

public interface IHeatStorage {
	public enum HeatTransferAction {
		EXECUTE, SIMULATE, SIMULATE_MAX_EFFICIENCY
	}

	public static final int STONE_MELTING_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(1000);
	public static final int WATER_BOILING_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(100);
	public static final int ROOM_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(20);
	public static final int WATER_FREEZING_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(0);
	public static final int MINIMUM_TEMPERATURE = CapabilityHeatable.convertHeatToMilliHeat(-273);

	/**
	 * Returns the amount of heat currently stored in this heatable entity.
	 * 
	 * @return
	 */
	public int getCurrentHeat();

	/**
	 * Returns the maximum amount of heat that *should* be stored in this heatable
	 * entity. After this point, should no longer take heat. Think of this as the
	 * *danger* point after which nothing smart should actively put heat into this.
	 * Putting heat into this after hitting the overheat threshold could trigger
	 * unwanted effects (exploding, melting, etc).
	 * 
	 * @return
	 */
	public default int getOverheatThreshold() {
		return getMaximumHeat();
	}

	/**
	 * Returns the minimum amount of heat that *should* be stored in this heatable
	 * entity. After this point, whatever object this heat storage recommends should
	 * be able to function.
	 * 
	 * @return
	 */
	public default int getMinimumHeatThreshold() {
		return MINIMUM_TEMPERATURE;
	}

	/**
	 * Returns the maximum amount of heat that can be stored in this heatable
	 * entity. After this point, it can no longer take heat. Think of this as the
	 * transition point where the storage could no longer exist in its current
	 * state. Some possible uses for this could be to explode or change into another
	 * block/item.
	 * 
	 * @return
	 */
	public int getMaximumHeat();

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
	public int heat(int amountToHeat, HeatTransferAction action);

	/**
	 * Cools down this heatable entity.
	 * 
	 * @param amountToCool
	 * @param simulate
	 * @return
	 */
	public int cool(int amountToCool, HeatTransferAction action);
}