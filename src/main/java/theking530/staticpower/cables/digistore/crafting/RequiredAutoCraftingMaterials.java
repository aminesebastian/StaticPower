package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class RequiredAutoCraftingMaterials {
	private final List<AutoCraftingStep> steps;
	private final List<RequiredAutoCraftingMaterial> materials;
	private boolean isMissingMaterials;

	public RequiredAutoCraftingMaterials(List<AutoCraftingStep> steps) {
		this.steps = steps;
		this.materials = new ArrayList<RequiredAutoCraftingMaterial>();
		update();
	}

	public List<RequiredAutoCraftingMaterial> getMaterials() {
		return materials;
	}

	protected void update() {
		if (steps.size() == 1) {
			return;
		}

		for (int i = 1; i < steps.size(); i++) {
			AutoCraftingStep step = steps.get(i);
			RequiredAutoCraftingMaterial material = null;
			if (step.getCraftingPattern() != null) {
				material = this.getMaterialForItem(step.getCraftingPattern().getOutput());
				if (material == null) {
					material = new RequiredAutoCraftingMaterial(step.getCraftingPattern().getOutput(), step.getTotalRequiredAmount(), step.getAmountRemainingToCraft(), step.getStoredAmount());
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

			// Check if we're missing materials.
			if (material.getMissingAmount() > 0) {
				isMissingMaterials = true;
			}
		}
	}

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
			if (material.getItem() == ing) {
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
	}
}