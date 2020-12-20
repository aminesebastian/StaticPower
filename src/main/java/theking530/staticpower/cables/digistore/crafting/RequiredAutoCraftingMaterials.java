package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle;
import theking530.staticpower.data.crafting.IngredientUtilities;

/**
 * Manifest class that contains an aggregation of all the material required for
 * the list of steps provided. Provides an easy way to check for missing
 * materials as well (not live missing materials, this class does not check
 * against a real digistore network instead, only takes the values for missing
 * provided in the steps).
 * 
 * @author amine
 *
 */
public class RequiredAutoCraftingMaterials {
	private final List<AutoCraftingStep> steps;
	private final List<RequiredAutoCraftingMaterial> materials;
	private boolean isMissingMaterials;

	public RequiredAutoCraftingMaterials(CraftingStepsBundle steps) {
		this.steps = steps.getSteps();
		this.materials = new ArrayList<RequiredAutoCraftingMaterial>();
		update();
	}

	/**
	 * Gets an aggregate list of all required ingredients.
	 * 
	 * @return
	 */
	public List<RequiredAutoCraftingMaterial> getMaterials() {
		return materials;
	}

	/**
	 * Updates the material list and aggregates requirements per material.
	 */
	protected void update() {
		// Check all the steps.
		for (AutoCraftingStep step : steps) {
			// Allocate the material.
			RequiredAutoCraftingMaterial material = null;

			// If this is a crafting pattern step, add the output of the crafting pattern to
			// the material list. Otherwise just add the ingredient.
			if (step.getCraftingPattern() != null) {
				material = this.getMaterialForItem(step.getCraftingPattern().getOutput());
				if (material == null) {
					material = new RequiredAutoCraftingMaterial(step.getCraftingPattern().getOutput(), step.getTotalRequiredAmount(), step.getAmountRemainingToCraft(),
							step.getStoredAmount());
					materials.add(material);
				} else {
					material.addAmountToCraft(step.getAmountRemainingToCraft());
					material.addRequiredAmount(step.getTotalRequiredAmount());
					material.addStoredAmount(step.getStoredAmount());
				}
			} else {
				material = getMaterialForItem(step.getIngredientToCraft());
				if (material == null) {
					material = new RequiredAutoCraftingMaterial(step.getIngredientToCraft(), step.getTotalRequiredAmount(), step.getAmountRemainingToCraft(), step.getStoredAmount());
					materials.add(material);
				} else {
					material.addAmountToCraft(step.getAmountRemainingToCraft());
					material.addRequiredAmount(step.getTotalRequiredAmount());
					material.addStoredAmount(step.getStoredAmount());
				}
			}

			// Check if we're missing materials. If so, mark this materials list as missing
			// materials.
			if (material.getMissingAmount() > 0) {
				isMissingMaterials = true;
			}
		}
	}

	/**
	 * If this material list is missing materials, this method will return true.
	 * 
	 * @return
	 */
	public boolean isMissingMaterials() {
		return isMissingMaterials;
	}

	@Nullable
	protected RequiredAutoCraftingMaterial getMaterialForItem(ItemStack item) {
		for (RequiredAutoCraftingMaterial material : materials) {
			if (material.getItem().test(item)) {
				return material;
			}
		}
		return null;
	}

	@Nullable
	protected RequiredAutoCraftingMaterial getMaterialForItem(Ingredient ing) {
		for (RequiredAutoCraftingMaterial material : materials) {
			if (IngredientUtilities.areIngredientsEqual(material.getItem(), ing)) {
				return material;
			}
		}
		return null;
	}

	public class RequiredAutoCraftingMaterial {
		private final Ingredient item;
		private int amountRequired;
		private int amountToCraft;
		private int amountStored;

		public RequiredAutoCraftingMaterial(ItemStack item, int amountRequired, int amountToCraft, int amountStored) {
			this.item = Ingredient.fromStacks(item);
			this.amountRequired = amountRequired;
			this.amountToCraft = amountToCraft;
			this.amountStored = amountStored;
		}

		public RequiredAutoCraftingMaterial(Ingredient item, int amountRequired, int amountToCraft, int amountStored) {
			this.item = item;
			this.amountRequired = amountRequired;
			this.amountToCraft = amountToCraft;
			this.amountStored = amountStored;
		}

		public void addRequiredAmount(int amount) {
			amountRequired += amount;
		}

		public void addAmountToCraft(int amount) {
			amountToCraft += amount;
		}

		public void addStoredAmount(int amount) {
			amountStored += amount;
		}

		public Ingredient getItem() {
			return item;
		}

		public int getAmountStored() {
			return amountStored;
		}

		public int getAmountRequired() {
			return amountRequired;
		}

		public int getAmountToCraft() {
			return amountToCraft;
		}

		public int getMissingAmount() {
			return Math.max(0, amountRequired - amountStored - amountToCraft);
		}

		@Override
		public String toString() {
			return "RequiredAutoCraftingMaterial [item=" + (item.hasNoMatchingItems() ? item : item.getMatchingStacks()[0]) + ", amountRequired=" + amountRequired + ", amountToCraft="
					+ amountToCraft + ", amountStored=" + amountStored + "]";
		}
	}
}