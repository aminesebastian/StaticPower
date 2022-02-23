package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;

public class ResearchLevels {
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

		return output;
	}

	private static void setParent(ResearchNode lookingForParent, ResearchLevels cachedLevels) {
		for (int i = cachedLevels.levels.size() - 1; i >= 0; i--) {
			for (ResearchNode parent : cachedLevels.levels.get(i).getResearch()) {
				if (lookingForParent.research.getPrerequisites().contains(parent.research.getId())) {
					lookingForParent.setParent(parent);
					return;
				}
			}
		}
	}

	public static List<Research> getAllResearchWithPrerequisitesInSet(Collection<Research> remaining, Set<ResourceLocation> cached) {
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

	public class ResearchNode {
		private final Research research;
		private final List<ResearchNode> children;
		private ResearchNode parent;

		public ResearchNode(Research research) {
			this.research = research;
			this.children = new ArrayList<ResearchNode>();
		}

		public void setParent(ResearchNode newParent) {
			if (parent != null) {
				parent.children.remove(this);
			}

			parent = newParent;
			parent.children.add(this);
		}

		public Research getResearch() {
			return research;
		}

		public ResearchNode getParent() {
			return parent;
		}

		public List<ResearchNode> getChildren() {
			return children;
		}

	}
}
