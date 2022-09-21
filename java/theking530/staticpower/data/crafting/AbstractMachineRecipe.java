package theking530.staticpower.data.crafting;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractMachineRecipe extends AbstractStaticPowerRecipe {
	protected final MachineRecipeProcessingSection processingSection;

	public AbstractMachineRecipe(ResourceLocation name, MachineRecipeProcessingSection processingSection) {
		super(name);
		this.processingSection = processingSection;
	}

	/**
	 * Returns the number of ticks it takes to complete this recipe.
	 * 
	 * @return
	 */
	public int getProcessingTime() {
		return processingSection.getProcessingTime();
	}

	/**
	 * Returns the power cost per tick.
	 * 
	 * @return
	 */
	public double getPowerCost() {
		return processingSection.getPowerCost();
	}

	public MachineRecipeProcessingSection getProcessingSection() {
		return processingSection;
	}
}
