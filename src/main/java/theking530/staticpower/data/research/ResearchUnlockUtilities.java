package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResearchUnlockUtilities {
	public static List<ResearchUnlock> getCollapsedUnlocks(Research research) {
		List<ResearchUnlock> output = new ArrayList<ResearchUnlock>();
		Set<String> usedKeys = new HashSet<String>();
		for (ResearchUnlock unlock : research.getUnlocks()) {
			if (!unlock.isHidden() && !usedKeys.contains(unlock.getDisplayKey())) {
				usedKeys.add(unlock.getDisplayKey());
				output.add(unlock);
			}
		}
		return output;
	}
}
