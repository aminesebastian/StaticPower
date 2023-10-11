package theking530.api.heat;

public enum HeatInfoType {
	BLOCK, AIR, FLUID;

	public boolean isBlock() {
		return this == BLOCK;
	}

	public boolean isAir() {
		return this == AIR;
	}

	public boolean isFluid() {
		return this == FLUID;
	}
}