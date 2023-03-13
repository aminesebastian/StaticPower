package theking530.staticpower.data.crafting;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractMachineRecipe extends AbstractStaticPowerRecipe {
	protected final MachineRecipeProcessingSection processingSection;

	public AbstractMachineRecipe(ResourceLocation id, MachineRecipeProcessingSection processingSection) {
		this(id, 0.0f, processingSection);
	}

	public AbstractMachineRecipe(ResourceLocation id, float experience, MachineRecipeProcessingSection processingSection) {
		super(id, experience);
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
		return getProcessingSection().getPower();
	}

	public MachineRecipeProcessingSection getProcessingSection() {
		if (processingSection == null) {
			return getDefaultProcessingSection();
		}
		return processingSection;
	}

	protected abstract MachineRecipeProcessingSection getDefaultProcessingSection();
}
