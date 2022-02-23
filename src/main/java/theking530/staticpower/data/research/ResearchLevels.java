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

	/**
	 * Tries to place child research as close to the same index as the preReq as
	 * possible.
	 */
	private void balance() {
		// Capture all the indecides for all research.
		HashMap<ResourceLocation, Integer> indexMap = new HashMap<>();
		for (ResearchLevel level : levels) {
			for (int i = 0; i < level.getResearch().size(); i++) {
				indexMap.put(level.getResearch().get(i).getId(), i);
			}
		}

		// Go through all levels after the first one.
		for (int i = 1; i < levels.size(); i++) {
			for (int j = 0; j < levels.get(i).getResearch().size(); j++) {
				// Capture the first index for the prereqs.
				int index = -1;
				for (ResourceLocation preReq : levels.get(i).getResearch().get(j).getPrerequisites()) {
					index = indexMap.get(preReq);
					break;
				}

				// Perform a swap if an index was found and its not already in the right place
				// and prevent going out of bounds.
				index = Math.min(index, levels.get(i).getResearch().size() - 1);
				if (index >= 0 && index != j) {
					indexMap.put(levels.get(i).getResearch().get(index).getId(), j);
					indexMap.put(levels.get(i).getResearch().get(j).getId(), index);
					swap(levels.get(i), j, index);
				}
			}
		}
	}

	private void swap(ResearchLevel level, int from, int to) {
		Research temp = level.getResearch().get(from);
		level.getResearch().set(from, level.getResearch().get(to));
		level.getResearch().set(to, temp);
	}

	@Override
	public String toString() {
		return "ResearchLevels [" + levels + "]";
	}

	public static ResearchLevels getAllResearchLevels() {
		ResearchLevels output = new ResearchLevels();
		Map<ResourceLocation, Research> allResearch = getResearch();
		Set<ResourceLocation> cached = new HashSet<ResourceLocation>();

		ResearchLevel currentLevel = output.new ResearchLevel();

		while (allResearch.size() > 0) {
			List<Research> satisfied = getAllResearchWithPrerequisitesInSet(allResearch.values(), cached);
			for (Research research : satisfied) {
				allResearch.remove(research.getId());
				cached.add(research.getId());
				currentLevel.research.add(research);
			}
			output.levels.add(currentLevel);
			currentLevel = output.new ResearchLevel();

			if (satisfied.size() == 0 && allResearch.size() > 0) {
				StaticPower.LOGGER.error("The following research is unreachable due to an issue with it's pre-requisite requirements: " + allResearch.keySet());
				break;
			}
		}

		output.balance();
		return output;
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
		private final List<Research> research;

		public ResearchLevel() {
			research = new ArrayList<Research>();
		}

		public List<Research> getResearch() {
			return research;
		}

		@Override
		public String toString() {
			return "ResearchLevel [" + research + "]";
		}

	}
}
