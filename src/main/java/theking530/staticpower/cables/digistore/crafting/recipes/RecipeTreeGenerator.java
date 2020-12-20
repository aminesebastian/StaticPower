package theking530.staticpower.cables.digistore.crafting.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;

public class RecipeTreeGenerator {
	public static final int MAX_TREES = 100;
	private Queue<Request> requests = new LinkedList<Request>();

	public List<AutoCraftingTree> generateTree(ItemStack target, int count, DigistoreInventorySnapshot snapshot) {
		// Reset the current request tree.
		requests.clear();

		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(Ingredient.fromStacks(target));

		// If we have no patterns for this item, return an empty list of trees.
		if (patterns.size() == 0) {
			return Collections.emptyList();
		}

		// Allocate the output.
		List<AutoCraftingTree> outputs = new ArrayList<AutoCraftingTree>();

		// Add the initial request.
		requests.add(new Request(target, count));

		// Process all reuqests.
		while (!requests.isEmpty()) {
			// Respect the maximum amount of trees. This should save us in the case of
			// infinite loops.
			if (requests.size() > MAX_TREES) {
				StaticPower.LOGGER.warn(String.format("Maximum tree count of: %1$s was met!", MAX_TREES));
				break;
			}

			// Get the request off of the queue.
			Request currentRequest = requests.poll();

			// Create the encoded ingredient.
			EncodedIngredient startingIngredient = new EncodedIngredient(target, count);

			// Create the root.
			AutoCraftingNode root = new AutoCraftingNode(startingIngredient, count);
			AutoCraftingTree owningTree = new AutoCraftingTree(root);

			// Generate a tree for the request.
			generate(startingIngredient, currentRequest.count, snapshot, null, root, currentRequest);

			// Add the tree.
			outputs.add(owningTree);
		}

		// Return all the generated trees.
		return outputs;
	}

	private boolean generate(EncodedIngredient ing, int amountRequired, DigistoreInventorySnapshot snapshot, @Nullable EncodedDigistorePattern sourcePattern, AutoCraftingNode parent,
			Request request) {
		// Just in case someone asks for fewer than 0 items, return.
		if (ing.getIngredient() == Ingredient.EMPTY) {
			return false;
		}

		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(ing.getIngredient());

		// If we have no patterns for this item, return false.
		if (patterns.size() == 0) {
			return false;
		}

		// Increment the depth.
		request.incrementDepth();

		// Get the pattern index to use.
		int patternIndex = request.getNextIndex();

		// We have to clamp this as in some cases we may have already reached the
		// maximum.
		patternIndex = Math.min(patterns.size() - 1, patternIndex);

		// Add the index.
		request.addBranchDecision(patternIndex);

		// If there are more patterns, copy the current request. Then, increment the
		// last decision index. Then, enqueue the new request.
		if (patternIndex < patterns.size() - 1) {
			Request newRequest = request.duplicate();
			requests.add(newRequest);
		}

		// Get the pattern.
		EncodedDigistorePattern pattern = patterns.get(patternIndex);

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
			generate(requiredItem, requiredIngredientAmount, snapshot, sourcePattern, requiredItemNode, request);
		}

		// Update parent's pattern.
		parent.setPattern(pattern);

		return true;
	}

	public class Request {
		private final ItemStack target;
		private final int count;
		private final List<Integer> branchIndexDecisions;
		private int currentDepth;

		public Request(ItemStack target, int count) {
			this.target = target;
			this.count = count;
			this.branchIndexDecisions = new ArrayList<Integer>();
			this.currentDepth = -1;
		}

		public void addBranchDecision(int decision) {
			if (currentDepth > branchIndexDecisions.size() - 1) {
				this.branchIndexDecisions.add(decision);
			} else {
				branchIndexDecisions.set(currentDepth, decision);
			}
		}

		public int getNextIndex() {
			if (currentDepth > branchIndexDecisions.size() - 1) {
				return 0;
			} else {
				return branchIndexDecisions.get(currentDepth) + 1;
			}
		}

		public void incrementDepth() {
			currentDepth++;
		}

		public Request duplicate() {
			Request output = new Request(target, count);
			for (int decision : branchIndexDecisions) {
				output.branchIndexDecisions.add(decision);
			}
			return output;
		}
	}

	public class AutoCraftingTree {
		private AutoCraftingNode root;

		public AutoCraftingTree(AutoCraftingNode root) {
			this.root = root;
		}

		public AutoCraftingTree copy() {
			return new AutoCraftingTree(root.copy());
		}

		public void printInLevelOrder() {
			System.out.println("-----------------------------------------");
			traverseInLevelOrder((node) -> System.out.println(node));
			System.out.println("-----------------------------------------");
		}

		public void traverseInLevelOrder(Consumer<AutoCraftingNode> consumer) {
			int height = getHeight(root);
			for (int i = height; i >= 1; i--) {
				traverseGivenLevel(root, i, consumer);
			}
		}

		private void traverseGivenLevel(AutoCraftingNode root, int level, Consumer<AutoCraftingNode> consumer) {
			if (root == null) {
				return;
			}
			if (level == 1) {
				consumer.accept(root);
			} else if (level > 1) {
				for (AutoCraftingNode child : root.children) {
					traverseGivenLevel(child, level - 1, consumer);
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
		public final EncodedIngredient ingredientToCraft;
		public final int count;
		public final List<AutoCraftingNode> children;
		public EncodedDigistorePattern pattern;

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
