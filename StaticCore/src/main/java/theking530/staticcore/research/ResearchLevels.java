package theking530.staticcore.research;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.utilities.math.Vector2D;

public class ResearchLevels {
	public final static float UNIT_SCALE = 100;
	private final List<ResearchLevel> levels;

	protected ResearchLevels() {
		levels = new ArrayList<>();
	}

	public List<ResearchLevel> getLevels() {
		return levels;
	}

	@Override
	public String toString() {
		return "ResearchLevels [" + levels + "]";
	}

	public static ResearchLevels getAllResearchLevels(Level level) {
		ResearchLevels output = new ResearchLevels();
		Map<ResourceLocation, Research> allResearch = getAllResearch(level);
		Set<ResourceLocation> handled = new HashSet<ResourceLocation>();
		List<ResearchNode> allNodes = new ArrayList<ResearchNode>();

		// Build all the levels.
		ResearchLevel currentLevel = output.new ResearchLevel();
		while (allResearch.size() > 0) {
			List<Research> satisfied = getAllResearchWithPrerequisitesInSet(allResearch.values(), handled);
			for (Research research : satisfied) {
				allResearch.remove(research.getId());
				handled.add(research.getId());
				ResearchNode newNode = output.new ResearchNode(research);
				currentLevel.research.add(newNode);
				allNodes.add(newNode);
			}
			currentLevel.finalize();
			output.levels.add(currentLevel);
			currentLevel = output.new ResearchLevel();

			if (satisfied.size() == 0 && allResearch.size() > 0) {
				StaticCore.LOGGER.error("The following research is unreachable due to an issue with it's pre-requisite requirements: " + allResearch.keySet());
				break;
			}
		}

		// Populate the parents for each level.
		for (ResearchNode node : allNodes) {
			setParent(node, output);
		}

		// Finalize the sorts for all the nodes.
		// This has to be done after ALL the nodes have their parents setup as per the
		// above.
		for (ResearchNode node : allNodes) {
			node.finalize();
		}

		// Populate the relative positions for each node.
		populatePositions(output, allNodes);
		return output;
	}

	protected static int getTotalChildren(ResearchNode node) {
		int output = node.getChildren().size();
		for (ResearchNode child : node.getChildren()) {
			output += getTotalChildren(child);
		}
		return output;
	}

	protected static int maxWidthChildBranch(ResearchNode node) {
		int output = node.getChildren().size();
		for (ResearchNode child : node.getChildren()) {
			output = Math.max(output, maxWidthChildBranch(child));
		}
		return output;
	}

	private static void populatePositions(ResearchLevels levels, List<ResearchNode> allNodes) {
		ResearchLevel level = levels.getLevels().get(0);
		Queue<ResearchNode> queue = new LinkedList<ResearchNode>();
		for (ResearchNode research : level.getResearch()) {
			queue.add(research);
		}

		while (!queue.isEmpty()) {
			ResearchNode research = queue.poll();

			// Capture the amount of TYPICAL children (children where this is the only
			// parent).
			int childCount = 0; // Math.max(0, research.getChildren().size() - 1);
			for (ResearchNode child : research.getChildren()) {
				if (child.getAllParents().size() == 1) {
					childCount++;
				}
			}
			childCount = Math.max(0, childCount - 1);

			// Base our offsets based on the amount of SOLO children we have.
			float initialOffset = childCount / -2.0f;
			float currentOffset = initialOffset;

			for (int i = 0; i < research.getChildren().size(); i++) {
				ResearchNode child = research.getChildren().get(i);

				float minParentX = Integer.MAX_VALUE;
				float maxParentX = Integer.MIN_VALUE;
				for (ResearchNode parent : child.getAllParents()) {
					if (parent.getRelativePosition().getX() < minParentX) {
						minParentX = parent.getRelativePosition().getX();
					}
					if (parent.getRelativePosition().getX() > maxParentX) {
						maxParentX = parent.getRelativePosition().getX();
					}
				}

				// The following is a useful debug line.
				// System.out.println("Parent: " + research.getResearch().getId() + " Child: " +
				// child.getResearch().getId() + " " + currentOffset);

				float targetX = (minParentX + maxParentX) / 2;

				// Only apply the current offset if this is a single parent node (ie. the
				// current research we pulled off the queue is its ONLY parent).
				if (child.getAllParents().size() == 1) {
					targetX += currentOffset;
				}
				child.setRelativeX(targetX + child.getResearch().getVisualOffset().getX());

				float yOffset = 0;
				if (research.getChildren().size() > 2) {
					yOffset += (i % 2 == 0 ? -0.05f : 0.05f);
				}
				child.setRelativeY(research.getRelativePosition().getY() + 1 + child.getResearch().getVisualOffset().getY() + yOffset);
				queue.add(child);
				currentOffset += 1.0f;
			}
		}
	}

	private static void setParent(ResearchNode lookingForParent, ResearchLevels cachedLevels) {
		for (int i = cachedLevels.levels.size() - 1; i >= 0; i--) {
			for (ResearchNode parent : cachedLevels.levels.get(i).getResearch()) {
				if (lookingForParent.research.getPrerequisites().contains(parent.research.getId())) {
					// Set the closest parent as the first parent in the list of prereqs.
					if (lookingForParent.research.getPrerequisites().get(0).equals(parent.research.getId())) {
						lookingForParent.setClosestParent(parent);
					}
					lookingForParent.addAdditionalParent(parent);
				}
			}
		}
	}

	private static List<Research> getAllResearchWithPrerequisitesInSet(Collection<Research> remaining, Set<ResourceLocation> cached) {
		List<Research> researchList = new ArrayList<Research>();

		for (Research research : remaining) {
			boolean matched = true;
			for (ResourceLocation preReq : research.getPrerequisites()) {
				if (!cached.contains(preReq)) {
					matched = false;
					break;
				}
			}

			if (matched) {
				researchList.add(research);
			}
		}

		return researchList;
	}

	public static Map<ResourceLocation, Research> getAllResearch(Level level) {
		Map<ResourceLocation, Research> output = new HashMap<ResourceLocation, Research>();
		level.getRecipeManager().getAllRecipesFor(StaticCoreRecipeTypes.RESEARCH_RECIPE_TYPE.get()).forEach((re) -> {
			output.put(re.getId(), re);
		});
		return output;
	}

	private static void sortNodes(List<ResearchNode> nodes) {
		Collections.sort(nodes, new Comparator<ResearchNode>() {
			@Override
			public int compare(ResearchNode lhs, ResearchNode rhs) {
				// First try to sort by sort order. If it's zero, then move to sort by the ID.
				int sortOrderComparison = Integer.compare(lhs.getResearch().getSortOrder(), rhs.getResearch().getSortOrder());
				if (sortOrderComparison != 0) {
					return sortOrderComparison;
				}
				return lhs.getResearch().getId().compareTo(rhs.getResearch().getId());
			}
		});
	}

	public class ResearchLevel {
		private final List<ResearchNode> research;

		public ResearchLevel() {
			research = new ArrayList<ResearchNode>();
		}

		public List<ResearchNode> getResearch() {
			return research;
		}

		public void finalize() {
			sortNodes(research);
		}

		@Override
		public String toString() {
			return "ResearchLevel [" + research + "]";
		}
	}

	public class RelativeNodePosition {
		private float x;
		private float y;

		public RelativeNodePosition(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

		public Vector2D getScaledVector(float scaleX, float scaleY) {
			return new Vector2D(x * scaleX, y * scaleY);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof RelativeNodePosition) {
				RelativeNodePosition other = (RelativeNodePosition) obj;
				return x == other.x && y == other.y;
			}
			return false;
		}

		@Override
		public String toString() {
			return "[x=" + x + ", y=" + y + "]";
		}
	}

	public class ResearchNode {
		private final Research research;
		private final List<ResearchNode> children;
		private final List<ResearchNode> allParents;
		private ResearchNode closestParent;
		private RelativeNodePosition relativePosition;

		public ResearchNode(Research research) {
			this.research = research;
			this.children = new ArrayList<ResearchNode>();
			this.allParents = new ArrayList<ResearchNode>();
			this.relativePosition = new RelativeNodePosition(0, 0);
		}

		public void setClosestParent(ResearchNode newParent) {
			if (closestParent != null) {
				closestParent.children.remove(this);
			}

			closestParent = newParent;
			closestParent.children.add(this);
		}

		public void addAdditionalParent(ResearchNode addParent) {
			this.allParents.add(addParent);
		}

		public Research getResearch() {
			return research;
		}

		public ResearchNode getParent() {
			return closestParent;
		}

		public List<ResearchNode> getAllParents() {
			return allParents;
		}

		public List<ResearchNode> getChildren() {
			return children;
		}

		public RelativeNodePosition getRelativePosition() {
			return relativePosition;
		}

		public void setRelativeX(float x) {
			this.relativePosition.setX(x);
		}

		public void setRelativeY(float y) {
			this.relativePosition.setY(y);
		}

		public void finalize() {
			sortNodes(children);
		}
	}
}
