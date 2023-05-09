package theking530.api.heat;

public interface IHeatStorage {
	public enum HeatTransferAction {
		EXECUTE, SIMULATE, SIMULATE_MAX_EFFICIENCY
	}

	public static final float WATER_BOILING_TEMPERATURE = 100;
	public static final float ROOM_TEMPERATURE = 20;
	public static final float WATER_FREEZING_TEMPERATURE = 0;
	public static final float MINIMUM_TEMPERATURE = -273;
	public static final float DEFAULT_BLOCK_MASS = 100;

	/**
	 * Returns the amount of heat currently stored in this heatable entity.
	 * 
	 * @return
	 */
	public float getCurrentTemperature();

	/**
	 * Returns the maximum amount of heat that *should* be stored in this heatable
	 * entity. After this point, should no longer take heat. Think of this as the
	 * *danger* point after which nothing smart should actively put heat into this.
	 * Putting heat into this after hitting the overheat threshold could trigger
	 * unwanted effects (exploding, melting, etc).
	 * 
	 * @return
	 */
	public default float getOverheatThreshold() {
		return getMaximumHeat();
	}

	/**
	 * Returns the minimum amount of heat that *should* be stored in this heatable
	 * entity. After this point, the owner should exhibit some new effects (stop
	 * working if a machine, etc).
	 * 
	 * @return
	 */
	public default float getMinimumHeatThreshold() {
		return WATER_FREEZING_TEMPERATURE;
	}

	/**
	 * Returns true if this heat storage is overheated.
	 * 
	 * @return
	 */
	public default boolean isOverheated() {
		return getCurrentTemperature() >= getOverheatThreshold();
	}

	/**
	 * Returns true if this heat storage is frozen.
	 * 
	 * @return
	 */
	public default boolean isFrozen() {
		return getCurrentTemperature() <= getMinimumHeatThreshold();
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
	public float getMaximumHeat();

	/**
	 * Gets the maximum rate that this heatable entity can transfer thermal energy.
	 * 
	 * @return
	 */
	public default float getConductivity() {
		return 1.0f;
	}

	/**
	 * Returns the mass of this heat storage. The higher the mass, the slower then
	 * transfer of heat.
	 * 
	 * @return
	 */
	public float getMass();

	/**
	 * Adds heat to this heatable entity.
	 * 
	 * @param heatToRecieve
	 * @param simulate
	 * @return
	 */
	public float heat(float amountToHeat, HeatTransferAction action);

	/**
	 * Cools down this heatable entity.
	 * 
	 * @param amountToCool
	 * @param simulate
	 * @return
	 */
	public float cool(float amountToCool, HeatTransferAction action);

	public HeatTicker getTicker();
}