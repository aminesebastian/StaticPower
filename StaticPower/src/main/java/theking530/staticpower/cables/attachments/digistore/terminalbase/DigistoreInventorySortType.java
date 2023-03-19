package theking530.staticpower.cables.attachments.digistore.terminalbase;

public enum DigistoreInventorySortType {
	NAME, COUNT;

	public DigistoreInventorySortType getNextType() {
		int next = (ordinal() + 1) % values().length;
		return values()[next];
	}

	public DigistoreInventorySortType getPrevType() {
		int next = (ordinal() - 1) % values().length;
		next = Math.abs(next);
		return values()[next];
	}
}
