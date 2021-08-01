package theking530.staticpower.cables.attachments.digistore.terminalbase;

public enum DigistoreSyncedSearchMode {
	DEFAULT, ONE_WAY, TWO_WAY;

	public DigistoreSyncedSearchMode getNextType() {
		int next = (ordinal() + 1) % values().length;
		return values()[next];
	}

	public DigistoreSyncedSearchMode getPrevType() {
		int next = (ordinal() - 1) % values().length;
		next = Math.abs(next);
		return values()[next];
	}
}
