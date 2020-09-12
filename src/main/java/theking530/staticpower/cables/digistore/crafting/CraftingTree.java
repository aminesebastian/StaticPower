package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;

public class CraftingTree {
	public static final int MAX_CRAFT_QUERY_DEPTH = 128;
	public static final Logger LOGGER = LogManager.getLogger(CraftingTree.class);
	protected CraftingTreeNode root;

	public CraftingTree generate(ItemStack targetItem, int amount, DigistoreInventorySnapshot snapshot) {
		root = getPatternTreeForItem(Ingredient.fromStacks(targetItem), amount, snapshot, 0);
		return this;
	}

	public CraftingTreeNode getRoot() {
		return root;
	}

	public void traverseCraftingOrder(Predicate<CraftingTreeNode> callback) {
		for (int i = 1; i <= getHeight(); i++) {
			traverseCraftingOrder(callback, root, i);
		}
	}

	public void traverseCraftingOrder(Predicate<CraftingTreeNode> callback, CraftingTreeNode node, int level) {
		if (node == null) {
			return;
		}
		if (level == 1) {
			callback.test(node);
		} else if (level > 1) {
			for (CraftingTreeNode child : node.children) {
				traverseCraftingOrder(callback, child, level - 1);
			}
		}
	}

	public void printForDebug() {
		System.out.println("----------------------------------");
		System.out.println();

		// Traverse reverse inorder.
		traverseCraftingOrder((node) -> {
			node.printDebug();
			System.out.println();
			return true;
		});
		System.out.println();
		System.out.println("----------------------------------");
	}

	public int getHeight() {
		return getHeight(root);
	}

	private int getHeight(CraftingTreeNode node) {
		// Base Case
		if (node == null) {
			return 0;
		} else {
			// Allocate an array for all the heights.
			int[] heights = new int[node.children.size()];

			// Get all the heights for all the sub trees.
			for (int i = 0; i < node.children.size(); i++) {
				heights[i] = getHeight(node.children.get(i));
			}

			// Get the maximum height.
			int maxHeight = 0;
			for (int height : heights) {
				if (height > maxHeight) {
					maxHeight = height;
				}
			}

			// Return the max height plus 1.
			return maxHeight + 1;
		}
	}

	@Nullable
	protected CraftingTreeNode getPatternTreeForItem(Ingredient ingredient, int amountRequired, DigistoreInventorySnapshot snapshot, int depth) {
		// If we surpassed the max search depth, return false.
		if (depth > MAX_CRAFT_QUERY_DEPTH) {
			LOGGER.warn(String.format("Reached the maximum crafitng query depth of: $1%d when attempting to craft required ingredient: %2$s.", MAX_CRAFT_QUERY_DEPTH, ingredient.toString()));
			return null;
		}

		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(ingredient);

		// If we have no patterns for this item, this is a terminal node.
		if (patterns.size() == 0) {
			return new CraftingTreeNode(CraftingTreeNodeType.TERMINAL, ingredient, amountRequired, null);
		}

		// For the first pattern, recurse.
		EncodedDigistorePattern pattern = patterns.get(0);

		// Create a node for this step and add it.
		CraftingTreeNode node = new CraftingTreeNode(CraftingTreeNodeType.CRAFT, ingredient, amountRequired, pattern);

		// Add all the items required to craft with.
		for (EncodedIngredient requiredItem : pattern.getRequiredItems()) {
			double ingredientRequired = requiredItem.getCount() * amountRequired;
			ingredientRequired = Math.ceil(ingredientRequired / pattern.getOutput().getCount());
			CraftingTreeNode childTree = getPatternTreeForItem(requiredItem.getIngredient(), (int) ingredientRequired, snapshot, depth + 1);
			node.addChildNode(childTree);
		}

		return node;
	}

	public enum CraftingTreeNodeType {
		TARGET, CRAFT, TERMINAL
	}

	public class CraftingTreeNode {
		public final CraftingTreeNodeType type;
		public final Ingredient targetIngredient;
		public final int amountRequired;
		public final EncodedDigistorePattern craftingPattern;
		public final List<CraftingTreeNode> children;

		public CraftingTreeNode(CraftingTreeNodeType type, Ingredient targetIngredient, int amountRequired, EncodedDigistorePattern craftingPattern) {
			super();
			this.type = type;
			this.targetIngredient = targetIngredient;
			this.craftingPattern = craftingPattern;
			this.children = new ArrayList<CraftingTreeNode>();
			this.amountRequired = amountRequired;
		}

		public void addChildNode(CraftingTreeNode node) {
			this.children.add(node);
		}

		public String printDebug() {
			String item = targetIngredient.hasNoMatchingItems() ? targetIngredient.toString() : targetIngredient.getMatchingStacks()[0].getDisplayName().getFormattedText();
			String required = (" \tRequired: " + amountRequired);
			if (craftingPattern != null) {
				String output = " \tCrafting Output: " + craftingPattern.getOutput().getCount();
				String iterations = " \tMaximum Crafting Iterations: " + (int) Math.ceil((double) amountRequired / craftingPattern.getOutput().getCount());
				System.out.format("%s%30s%10s%10s", item, required, output, iterations);
			} else {
				System.out.format("%s%30s", item, required);
			}

			return "";
		}

		@Override
		public String toString() {
			return "CraftingTreeNode [type=" + type + ", targetIngredient=" + targetIngredient + ", craftingPattern=" + craftingPattern + ", children=" + children + "]";
		}
	}
}
