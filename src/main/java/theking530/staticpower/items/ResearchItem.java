package theking530.staticpower.items;

import theking530.staticcore.utilities.Color;

public class ResearchItem extends StaticPowerItem {
	private final Color color;
	private final int researchTier;

	public ResearchItem(String name, Color color, int tier) {
		super(name);
		this.color = color;
		this.researchTier = tier;
	}

	public Color getColor() {
		return color;
	}

	public int getResearchTier() {
		return researchTier;
	}
}
