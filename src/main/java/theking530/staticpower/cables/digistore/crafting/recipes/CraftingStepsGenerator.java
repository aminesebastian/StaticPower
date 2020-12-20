package theking530.staticpower.cables.digistore.crafting.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.crafting.AutoCraftingStep;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;
import theking530.staticpower.cables.digistore.crafting.recipes.RecipeTreeGenerator.AutoCraftingNode;
import theking530.staticpower.cables.digistore.crafting.recipes.RecipeTreeGenerator.AutoCraftingTree;

public class CraftingStepsGenerator {

	public List<AutoCraftingStep> generateSteps(AutoCraftingTree tree, DigistoreInventorySnapshot inventory) {
		// Allocate the output.
		List<AutoCraftingStep> output = new ArrayList<AutoCraftingStep>();

		// Traverse the tree.
		tree.traverseInLevelOrder((node) -> {
			AutoCraftingStep generatedStep = generateStepForNode(node, inventory);
			generatedStep.setCraftingPattern(node.getPattern());
			output.add(generatedStep);
		});

		// Return the output.
		return output;
	}

	private AutoCraftingStep generateStepForNode(AutoCraftingNode node, DigistoreInventorySnapshot inventory) {
		// See how many of the ingredient we have.
		int extracted = inventory.extractWithIngredient(node.ingredientToCraft.getIngredient(), node.count, false);

		// Calculate how much we have missing.
		int missing = node.count - extracted;

		// If we're not missing any, or we are missing some but this item is not
		// craftable, return a step indicating 0 craftable.
		if (missing <= 0) {
			return new AutoCraftingStep(node.ingredientToCraft.getIngredient(), extracted, 0, node.count);
		}
		if (node.getPattern() == null) {
			return new AutoCraftingStep(node.ingredientToCraft.getIngredient(), extracted, 0, node.count);
		}
		// Set the craftable amount initially to the required amount.
		int craftable = missing;

		// Calculate the craftable amount.
		for (EncodedIngredient requiredItem : node.getPattern().getRequiredItems()) {
			// Calculate the amount of steps required.
			double iterationsRequired = (double) craftable / (double) node.getPattern().getOutput().getCount();

			// Calculate the required amount.
			int requiredIngredientAmount = (int) (requiredItem.getCount() * Math.ceil(iterationsRequired));

			// Simulate extract and capture the extract amount.
			int storedIngredientAmount = inventory.extractWithIngredient(requiredItem.getIngredient(), requiredIngredientAmount, false);

			// Calculate the missing amount between the required and extracted.
			int missingIngredientAmount = requiredIngredientAmount - storedIngredientAmount;

			if (missingIngredientAmount > 0) {
				craftable = storedIngredientAmount;
			}
		}

		// If we are able to craft some, add it to the inventory.
		if (craftable > 0) {
			ItemStack craftedItem = node.getPattern().getOutput().copy();
			craftedItem.setCount(craftable);
			inventory.insertItemStack(craftedItem, false);
		}
		// Check if this node is craftable.
		return new AutoCraftingStep(node.ingredientToCraft.getIngredient(), extracted, craftable, node.count);
	}
}
