package theking530.staticpower.cables.attachments.digistore.terminal;

public enum DigistoreSearchMode {
	DEFAULT, ONE_WAY, TWO_WAY;

	public DigistoreSearchMode getNextType() {
		int next = (ordinal() + 1) % values().length;
		return values()[next];
	}

	public DigistoreSearchMode getPrevType() {
		int next = (ordinal() - 1) % values().length;
		next = Math.abs(next);
		return values()[next];
	}
}
