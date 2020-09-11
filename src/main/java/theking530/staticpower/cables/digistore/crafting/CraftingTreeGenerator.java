package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;

public class CraftingTreeGenerator {
	public static final int MAX_CRAFT_QUERY_DEPTH = 128;
	public static final Logger LOGGER = LogManager.getLogger(CraftingTreeGenerator.class);

	public CraftingTreeNode generateTreeForItem(ItemStack targetItem, DigistoreInventorySnapshot snapshot) {
		CraftingTreeNode root = getPatternTreeForItem(Ingredient.fromStacks(targetItem), 1, snapshot, 0);
		return root;
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
		CraftingTreeNode node = new CraftingTreeNode(CraftingTreeNodeType.TERMINAL, ingredient, amountRequired, pattern);

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

		public String getDebugTreeOutput() {
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

		public void printForDebug() {
			System.out.println("----------------------------------");
			System.out.println();
			printForDebug(0);
			System.out.println();
			System.out.println("----------------------------------");
		}

		private void printForDebug(int level) {
			getDebugTreeOutput();
			System.out.println();

			for (CraftingTreeNode child : children) {
				child.printForDebug(level + 1);
			}
		}

		@Override
		public String toString() {
			return "CraftingTreeNode [type=" + type + ", targetIngredient=" + targetIngredient + ", craftingPattern=" + craftingPattern + ", children=" + children + "]";
		}
	}
}
