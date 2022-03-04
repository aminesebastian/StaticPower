package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;

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

	public static ResearchLevels getAllResearchLevels() {
		ResearchLevels output = new ResearchLevels();
		Map<ResourceLocation, Research> allResearch = getResearch();
		Set<ResourceLocation> cached = new HashSet<ResourceLocation>();
		List<ResearchNode> allNodes = new ArrayList<ResearchNode>();

		// Build all the levels.
		ResearchLevel currentLevel = output.new ResearchLevel();
		while (allResearch.size() > 0) {
			List<Research> satisfied = getAllResearchWithPrerequisitesInSet(allResearch.values(), cached);
			for (Research research : satisfied) {
				allResearch.remove(research.getId());
				cached.add(research.getId());
				ResearchNode newNode = output.new ResearchNode(research);
				currentLevel.research.add(newNode);
				allNodes.add(newNode);
			}
			output.levels.add(currentLevel);
			currentLevel = output.new ResearchLevel();

			if (satisfied.size() == 0 && allResearch.size() > 0) {
				StaticPower.LOGGER.error("The following research is unreachable due to an issue with it's pre-requisite requirements: " + allResearch.keySet());
				break;
			}
		}

		// Populate the parents for each level.
		for (ResearchNode node : allNodes) {
			setParent(node, output);
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

			// Add the positions to the hash set and then set them.
			for (ResearchNode child : research.getChildren()) {
				float targetX = 0;
				for (ResearchNode parent : child.getAllParents()) {
					targetX += parent.getRelativePosition().getX();
				}
				targetX /= child.getAllParents().size();

				child.setRelativeX(targetX + child.getResearch().getVisualOffset().getX());
				child.setRelativeY(research.getRelativePosition().getY() + 1 + child.getResearch().getVisualOffset().getY());
				queue.add(child);
			}
		}
	}

	private static void setParent(ResearchNode lookingForParent, ResearchLevels cachedLevels) {
		for (int i = cachedLevels.levels.size() - 1; i >= 0; i--) {
			for (ResearchNode parent : cachedLevels.levels.get(i).getResearch()) {
				if (lookingForParent.research.getPrerequisites().contains(parent.research.getId())) {
					if (lookingForParent.closestParent == null) {
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

	private static Map<ResourceLocation, Research> getResearch() {
		Map<ResourceLocation, Research> output = new HashMap<ResourceLocation, Research>();
		StaticPowerRecipeRegistry.getRecipesOfType(Research.RECIPE_TYPE).forEach((re) -> {
			output.put(re.getId(), re);
		});
		return output;
	}

	public class ResearchLevel {
		private final List<ResearchNode> research;

		public ResearchLevel() {
			research = new ArrayList<ResearchNode>();
		}

		public List<ResearchNode> getResearch() {
			return research;
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

		public Vector2D getScaledVector(float scale) {
			return new Vector2D(x * scale, y * scale);
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
	}
}
