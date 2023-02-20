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
		return getProcessingSection().getProcessingTime();
	}

	/**
	 * Returns the power cost per tick.
	 * 
	 * @return
	 */
	public double getPowerCost() {
		return getProcessingSection().getPowerCost();
	}

	public MachineRecipeProcessingSection getProcessingSection() {
		if (processingSection == null) {
			return getDefaultProcessingSection();
		}
		return processingSection;
	}

	// TODO: Make abstract
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return null;
	}
}
