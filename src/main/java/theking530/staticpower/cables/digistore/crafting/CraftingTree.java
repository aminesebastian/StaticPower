package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
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
	protected CraftingNode root;

	public CraftingTree generate(ItemStack targetItem, int amount, DigistoreInventorySnapshot snapshot) {
		List<RawCraftingNode> rawTrees = cachePatternTreeForItem(Ingredient.fromStacks(targetItem), amount, snapshot, 0);
		List<CraftingNode> trees = flattenTrees(rawTrees);

		System.out.println("------------------New Set----------------");
		for (int j = 0; j < trees.size(); j++) {
			CraftingNode node = trees.get(j);
			System.out.println(j);
			for (int i = 1; i <= getHeight(node); i++) {
				System.out.print("Level " + i + ":  ");
				traverseCraftingOrder(treeNode -> {
					treeNode.printDebug();
					System.out.println();
					return true;
				}, node, i);
			}
			System.out.println();
			System.out.println("----------------------------------");
		}
		System.out.println("------------------End Set----------------");
		root = trees.get(0);
		return this;
	}

	public CraftingNode getRoot() {
		return root;
	}

	public void traverseCraftingOrder(Predicate<CraftingNode> callback) {
		for (int i = 1; i <= getHeight(); i++) {
			traverseCraftingOrder(callback, root, i);
		}
	}

	public static void traverseCraftingOrder(Predicate<CraftingNode> callback, CraftingNode node, int level) {
		if (node == null) {
			return;
		}
		if (level == 1) {
			callback.test(node);
		} else if (level > 1) {
			for (CraftingNode child : node.children) {
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

	private static int getHeight(CraftingNode node) {
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
	protected List<RawCraftingNode> cachePatternTreeForItem(Ingredient ingredient, int amountRequired, DigistoreInventorySnapshot snapshot, int depth) {
		// If we surpassed the max search depth, return false.
		if (depth > MAX_CRAFT_QUERY_DEPTH) {
			LOGGER.warn(String.format("Reached the maximum crafitng query depth of: $1%d when attempting to craft required ingredient: %2$s.", MAX_CRAFT_QUERY_DEPTH, ingredient.toString()));
			return Collections.emptyList();
		}

		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(ingredient);

		// If we have no patterns for this item, this is a terminal node.
		if (patterns.size() == 0) {
			return Arrays.asList(new RawCraftingNode(CraftingTreeNodeType.TERMINAL, ingredient, amountRequired));
		}

		// Allocate the output.
		List<RawCraftingNode> output = new ArrayList<RawCraftingNode>();

		// Iterate through all the patterns.
		for (EncodedDigistorePattern pattern : patterns) {
			// Create a node for this.
			RawCraftingNode node = new RawCraftingNode(CraftingTreeNodeType.CRAFT, ingredient, amountRequired);
			output.add(node);

			// Add all the items required to craft with.
			for (EncodedIngredient requiredItem : pattern.getRequiredItems()) {
				double ingredientRequired = requiredItem.getCount() * amountRequired;
				ingredientRequired = Math.ceil(ingredientRequired / pattern.getOutput().getCount());
				List<RawCraftingNode> nodes = cachePatternTreeForItem(requiredItem.getIngredient(), (int) ingredientRequired, snapshot, depth + 1);
				node.addChildren(pattern, nodes);
			}
		}

		// Return all the outputs.
		return output;
	}

	protected List<CraftingNode> flattenTrees(List<RawCraftingNode> rawTrees) {
		int variations = 1;
		for (RawCraftingNode root : rawTrees) {
			Queue<RawCraftingNode> toVisit = new LinkedList<RawCraftingNode>();
			toVisit.add(root);

			while (!toVisit.isEmpty()) {
				RawCraftingNode target = toVisit.poll();
				variations *= target.children.size() > 0 ? target.children.size() : 1;
				for (Entry<EncodedDigistorePattern, List<RawCraftingNode>> entry : target.children.entrySet()) {
					for (RawCraftingNode child : entry.getValue()) {
						toVisit.add(child);
					}
				}
			}
		}

		List<CraftingNode> output = new ArrayList<CraftingNode>();
		for (int i = 1; i < variations + 1; i++) {
			RawCraftingNode root = rawTrees.get(rawTrees.size() % i);
			EncodedDigistorePattern pattern = (EncodedDigistorePattern) root.children.keySet().toArray()[root.children.keySet().size() % i];
			CraftingNode newRoot = new CraftingNode(root.type, root.targetIngredient, root.amountRequired, pattern);
			output.add(newRoot);
			convertRawToRegular(newRoot, root, i);
		}
		return output;
	}

	private void convertRawToRegular(CraftingNode newNode, RawCraftingNode oldNode, int variation) {
		if (oldNode.children.size() > 0) {
			EncodedDigistorePattern pattern = (EncodedDigistorePattern) oldNode.children.keySet().toArray()[oldNode.children.keySet().size() % variation];
			for (RawCraftingNode child : oldNode.children.get(pattern)) {
				CraftingNode newTarget = new CraftingNode(child.type, child.targetIngredient, child.amountRequired, pattern);
				newNode.addChild(newTarget);
				convertRawToRegular(newTarget, child, variation);
			}
		} else {
			CraftingNode newTarget = new CraftingNode(oldNode.type, oldNode.targetIngredient, oldNode.amountRequired, null);
			newNode.addChild(newTarget);
		}
	}

	public enum CraftingTreeNodeType {
		TARGET, CRAFT, TERMINAL
	}

	public class CraftingNode {
		public final CraftingTreeNodeType type;
		public final Ingredient targetIngredient;
		public final int amountRequired;
		public final EncodedDigistorePattern pattern;
		public final List<CraftingNode> children;

		public CraftingNode(CraftingTreeNodeType type, Ingredient targetIngredient, int amountRequired, EncodedDigistorePattern pattern) {
			this.type = type;
			this.targetIngredient = targetIngredient;
			this.amountRequired = amountRequired;
			this.children = new ArrayList<CraftingNode>();
			this.pattern = pattern;
		}

		public void addChild(CraftingNode child) {
			this.children.add(child);
		}

		public String printDebug() {
			String item = targetIngredient.hasNoMatchingItems() ? targetIngredient.toString() : targetIngredient.getMatchingStacks()[0].getDisplayName().getFormattedText();
			String required = (" \tRequired: " + amountRequired);
			if (pattern != null) {
				String output = " \tCrafting Output: " + pattern.getOutput().getCount();
				String iterations = " \tMaximum Crafting Iterations: " + (int) Math.ceil((double) amountRequired / pattern.getOutput().getCount());
				System.out.format("%s%30s%10s%10s", item, required, output, iterations);
			} else {
				System.out.format("%s%30s", item, required);
			}

			return "";
		}

		@Override
		public String toString() {
			return "CraftingNode [type=" + type + ", targetIngredient=" + targetIngredient + ", amountRequired=" + amountRequired + ", children=" + children + "]";
		}

	}

	public class RawCraftingNode {
		public final CraftingTreeNodeType type;
		public final Ingredient targetIngredient;
		public final int amountRequired;
		public final Map<EncodedDigistorePattern, List<RawCraftingNode>> children;

		public RawCraftingNode(CraftingTreeNodeType type, Ingredient targetIngredient, int amountRequired) {
			this.type = type;
			this.targetIngredient = targetIngredient;
			this.children = new HashMap<EncodedDigistorePattern, List<RawCraftingNode>>();
			this.amountRequired = amountRequired;
		}

		public void addChildren(EncodedDigistorePattern pattern, List<RawCraftingNode> node) {
			children.put(pattern, node);
		}

		public String printDebug() {
			return "";
		}

		@Override
		public String toString() {
			return "CraftingTreeNode [type=" + type + ", targetIngredient=" + targetIngredient + ", amountRequired=" + amountRequired + ", children=" + children + "]";
		}
	}
}
