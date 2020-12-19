package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;

public class TestRecipeTreeGenerator {
	public List<AutoCraftingTree> generateTree(ItemStack target, int count, DigistoreInventorySnapshot snapshot) {
		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(Ingredient.fromStacks(target));

		// If we have no patterns for this item, return an empty list of trees.
		if (patterns.size() == 0) {
			return Collections.emptyList();
		}

		// Allocate the output.
		List<AutoCraftingTree> outputs = new ArrayList<AutoCraftingTree>();

		// Generate a tree for each pattern.
		for (EncodedDigistorePattern pattern : patterns) {
			// Create the encoded ingredient.
			EncodedIngredient startingIngredient = new EncodedIngredient(target, count);

			// Create the root.
			AutoCraftingNode root = new AutoCraftingNode(startingIngredient, count);
			generate(startingIngredient, count, snapshot, null, root, outputs);

			// Add the tree.
			outputs.add(new AutoCraftingTree(root));
		}

		return outputs;
	}

	private boolean generate(EncodedIngredient ing, int amountRequired, DigistoreInventorySnapshot snapshot, @Nullable EncodedDigistorePattern sourcePattern, AutoCraftingNode parent,
			List<AutoCraftingTree> treeList) {
		// Just in case someone asks for fewer than 0 items, return.
		if (ing.getIngredient() == Ingredient.EMPTY) {
			return false;
		}

		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(ing.getIngredient());

		// If we have no patterns for this item, return false.
		if (patterns.size() == 0) {
			patterns.clear();
			return false;
		}

		// Handle for local parent.
		AutoCraftingNode realParent = parent;
		
		// Get the pattern.
		for (int i = 0; i < patterns.size(); i++) {
			if (i > 0) {
				realParent
			}

			EncodedDigistorePattern pattern = patterns.get(0);

			// Use this to avoid a single cycle in a loop.
			if (pattern == sourcePattern) {
				return false;
			}

			// Check to see if we have the items required to craft with.
			for (EncodedIngredient requiredItem : pattern.getRequiredItems()) {
				// Calculate the amount of steps required.
				double iterationsRequired = (double) amountRequired / (double) pattern.getOutput().getCount();

				// Calculate the required amount.
				int requiredIngredientAmount = (int) (requiredItem.getCount() * Math.ceil(iterationsRequired));

				AutoCraftingNode requiredItemNode = new AutoCraftingNode(requiredItem, requiredIngredientAmount);
				parent.addChildNode(requiredItemNode);
				generate(requiredItem, requiredIngredientAmount, snapshot, sourcePattern, requiredItemNode);
			}

			// Update parent's pattern.
			realParent.setPattern(pattern);
		}

		return true;
	}

	public class AutoCraftingTree {
		private final AutoCraftingNode root;

		public AutoCraftingTree(AutoCraftingNode root) {
			this.root = root;
		}

		public AutoCraftingTree copy() {
			return new AutoCraftingTree(root.copy());
		}

		public void printInCraftingOrder() {
			System.out.println("-----------------------------------------");
			int height = getHeight(root);
			for (int i = height; i >= 1; i--) {
				printGivenLevel(root, i);
			}
			System.out.println("-----------------------------------------");
		}

		private void printGivenLevel(AutoCraftingNode root, int level) {
			if (root == null) {
				return;
			}
			if (level == 1) {
				System.out.println(root);
			} else if (level > 1) {
				for (AutoCraftingNode child : root.children) {
					printGivenLevel(child, level - 1);
				}
			}
		}

		public int getHeight() {
			return getHeight(root);
		}

		private int getHeight(AutoCraftingNode node) {
			if (node == null) {
				return 0;
			} else {
				int maxHeight = 0;
				for (AutoCraftingNode child : node.children) {
					int childHeight = getHeight(child);
					if (childHeight > maxHeight) {
						maxHeight = childHeight;
					}
				}
				return maxHeight + 1;
			}
		}

		@Override
		public String toString() {
			return "AutoCraftingTree [root=" + root + "]";
		}
	}

	public class AutoCraftingNode {
		private final EncodedIngredient ingredientToCraft;
		private final int count;
		private final List<AutoCraftingNode> children;
		private EncodedDigistorePattern pattern;

		public AutoCraftingNode(EncodedIngredient ingredientToCraft, int count) {
			this.ingredientToCraft = ingredientToCraft;
			this.children = new ArrayList<>();
			this.count = count;
		}

		public void addChildNode(AutoCraftingNode child) {
			children.add(child);
		}

		public AutoCraftingNode copy() {
			AutoCraftingNode output = new AutoCraftingNode(ingredientToCraft, count);
			output.setPattern(pattern);
			for (AutoCraftingNode child : children) {
				output.addChildNode(child.copy());
			}
			return output;
		}

		public void setPattern(EncodedDigistorePattern pattern) {
			this.pattern = pattern;
		}

		public List<AutoCraftingNode> getChildren() {
			return children;
		}

		public EncodedDigistorePattern getPattern() {
			return pattern;
		}

		@Override
		public String toString() {
			return "Node [ingredient=" + ingredientToCraft + ", count=" + count + ", children=" + children + ", hasPattern=" + (pattern != null) + "]";
		}
	}
}
