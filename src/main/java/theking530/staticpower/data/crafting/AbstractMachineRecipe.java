package theking530.staticpower.data.crafting;

import net.minecraft.util.ResourceLocation;

public abstract class AbstractMachineRecipe extends AbstractStaticPowerRecipe {
	protected final int processingTime;
	protected final long powerCost;

	public AbstractMachineRecipe(ResourceLocation name, int processingTime, long powerCost) {
		super(name);
		this.processingTime = processingTime;
		this.powerCost = powerCost;
	}

	/**
	 * Returns the number of ticks it takes to complete this recipe.
	 * 
	 * @return
	 */
	public int getProcessingTime() {
		return processingTime;
	}

	/**
	 * Returns the power cost per tick.
	 * 
	 * @return
	 */
	public long getPowerCost() {
		return powerCost;
	}
}
