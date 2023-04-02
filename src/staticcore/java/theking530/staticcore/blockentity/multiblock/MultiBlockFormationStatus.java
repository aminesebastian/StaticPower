package theking530.staticcore.blockentity.multiblock;

public class MultiBlockFormationStatus {
	public static final MultiBlockFormationStatus OK = new MultiBlockFormationStatus("gui.staticcore.multiblock_ok", true);
	public static final MultiBlockFormationStatus FAILED = new MultiBlockFormationStatus("gui.staticcore.multiblock_failed", false);
	public static final MultiBlockFormationStatus INVALID = new MultiBlockFormationStatus("gui.staticcore.multiblock_invalid", false);

	private final String unlocalizedStatus;
	private final boolean successful;

	private MultiBlockFormationStatus(String unlocalizedStatus, boolean successful) {
		super();
		this.unlocalizedStatus = unlocalizedStatus;
		this.successful = successful;
	}

	public static final MultiBlockFormationStatus success(String unlocalizedStatus) {
		return new MultiBlockFormationStatus(unlocalizedStatus, true);
	}

	public static final MultiBlockFormationStatus failed(String unlocalizedStatus) {
		return new MultiBlockFormationStatus(unlocalizedStatus, false);
	}

	public String getUnlocalizedStatus() {
		return unlocalizedStatus;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
