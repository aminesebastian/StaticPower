package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class RequiredAutoCraftingMaterials {
	private final List<AutoCraftingStep> steps;
	private final List<RequiredAutoCraftingMaterial> materials;

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

		for (int i = 0; i < steps.size(); i++) {
			AutoCraftingStep step = steps.get(i);
			if (step.getCraftingPattern() != null) {
				RequiredAutoCraftingMaterial material = this.getMaterialForItem(step.getCraftingPattern().getOutput());
				if (material == null) {
					material = new RequiredAutoCraftingMaterial(step.getCraftingPattern().getOutput(), step.getTotalRequiredAmount(), step.getAmountRemainingToCraft());
					materials.add(material);
				} else {
					material.addAmountToCraft(step.getAmountRemainingToCraft());
					material.addRequiredAmount(step.getTotalRequiredAmount());
				}
			} else {
				RequiredAutoCraftingMaterial material = getMaterialForItem(step.getIngredientToCraft());
				if (material == null) {
					material = new RequiredAutoCraftingMaterial(step.getIngredientToCraft(), step.getTotalRequiredAmount(), step.getAmountRemainingToCraft());
					materials.add(material);
				} else {
					material.addAmountToCraft(step.getAmountRemainingToCraft());
					material.addRequiredAmount(step.getTotalRequiredAmount());
				}
			}
		}
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

		public RequiredAutoCraftingMaterial(ItemStack item, int amountRequired, int amountToCraft) {
			this.item = Ingredient.fromStacks(item);
			this.amountRequired = amountRequired;
			this.amountToCraft = amountToCraft;
		}

		public RequiredAutoCraftingMaterial(Ingredient item, int amountRequired, int amountToCraft) {
			this.item = item;
			this.amountRequired = amountRequired;
			this.amountToCraft = amountToCraft;
		}

		public void addRequiredAmount(int amount) {
			amountRequired += amount;
		}

		public void addAmountToCraft(int amount) {
			amountToCraft += amount;
		}

		public Ingredient getItem() {
			return item;
		}

		public int getAmountRequired() {
			return amountRequired;
		}

		public int getAmountToCraft() {
			return amountToCraft;
		}
	}
}