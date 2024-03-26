package theking530.staticcore.blockentity.components.multiblock;

import net.minecraft.nbt.CompoundTag;

public class MultiBlockFormationStatus {
	public static final MultiBlockFormationStatus OK = new MultiBlockFormationStatus("gui.staticcore.multiblock_ok",
			true);
	public static final MultiBlockFormationStatus FAILED = new MultiBlockFormationStatus(
			"gui.staticcore.multiblock_failed", false);
	public static final MultiBlockFormationStatus INVALID = new MultiBlockFormationStatus(
			"gui.staticcore.multiblock_invalid", false);

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

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putBoolean("successful", successful);
		output.putString("status", unlocalizedStatus);
		return output;
	}

	public static MultiBlockFormationStatus deserialize(CompoundTag nbt) {
		boolean successful = nbt.getBoolean("successful");
		String status = nbt.getString("status");

		if (status.equals(OK.unlocalizedStatus)) {
			return OK;
		}
		if (status.equals(FAILED.unlocalizedStatus)) {
			return FAILED;
		}
		if (status.equals(INVALID.unlocalizedStatus)) {
			return INVALID;
		}
		return new MultiBlockFormationStatus(status, successful);
	}
}
