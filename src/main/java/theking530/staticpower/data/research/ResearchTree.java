package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;

public class ResearchTree {
	private Research curent;
	private List<ResearchTree> subTrees;

	public ResearchTree(Research curent) {
		this.curent = curent;
		subTrees = new ArrayList<>();
	}

	@Override
	public String toString() {
		if (subTrees.size() == 0) {
			return curent.toString();
		} else {
			return curent + " => [" + subTrees + "]";
		}
	}

	public static List<ResearchTree> getResearchTrees() {
		List<ResearchTree> outputs = new ArrayList<ResearchTree>();
		List<Research> rootResearch = getResearchWithPrerequisite(null);
		Set<Research> visited = new HashSet<Research>();

		for (Research root : rootResearch) {
			ResearchTree tree = new ResearchTree(root);
			buildTreeForPrerequisite(tree, visited);
			outputs.add(tree);
		}

		return outputs;
	}

	private static void buildTreeForPrerequisite(ResearchTree parent, Set<Research> visited) {
		List<Research> researchList = getResearchWithPrerequisite(parent.curent.getId());
		if (researchList.size() == 0) {
			return;
		}

		for (Research re : researchList) {
			if (visited.contains(re)) {
				StaticPower.LOGGER.error(String.format("Cycle detected in research tree between nodes: %1$s and prerequisite: %2$s. Skipping child node.", re.getId(), parent.curent.getId()));
				continue;
			}
			ResearchTree output = new ResearchTree(re);
			parent.subTrees.add(output);
			buildTreeForPrerequisite(output, visited);
		}
		visited.add(parent.curent);
	}

	private static List<Research> getResearchWithPrerequisite(@Nullable ResourceLocation prerequisite) {
		List<Research> researchList = StaticPowerRecipeRegistry.getRecipesOfType(Research.RECIPE_TYPE);
		List<Research> output = new ArrayList<Research>();

		for (Research re : researchList) {
			if (re.getPrerequisites().contains(prerequisite) || (prerequisite == null && re.getPrerequisites().size() == 0)) {
				output.add(re);
			}
		}

		return output;
	}
}
