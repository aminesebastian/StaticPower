package theking530.staticpower.tileentities.components.heat;

public interface IHeatStorage {
	/**
	 * Returns the amount of heat currently stored in this heatable entity.
	 * 
	 * @return
	 */
	public float getCurrentHeat();

	/**
	 * Returns the maximum amount of heat that can be stored in this heatable
	 * entity.
	 * 
	 * @return
	 */
	public float getMaximumHeat();

	/**
	 * Gets the maximum rate that this heatable entity can transfer thermal energy.
	 * 
	 * @return
	 */
	public float getMaximumHeatTransferRate();

	/**
	 * Adds heat to this heatable entity.
	 * 
	 * @param heatToRecieve
	 * @param simulate
	 * @return
	 */
	public float heat(float amountToHeat, boolean simulate);

	/**
	 * Cools down this heatable entity.
	 * 
	 * @param amountToCool
	 * @param simulate
	 * @return
	 */
	public float cool(float amountToCool, boolean simulate);
}