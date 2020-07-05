package theking530.staticpower.cables.digistore;

public enum DigistoreInventorySortType {
	NAME, COUNT;

	public DigistoreInventorySortType getNextType() {
		int next = (ordinal() + 1) % values().length;
		return values()[next];
	}
}
