package theking530.staticpower.utils;

public class RedstoneModeList {

	public enum RedstoneMode {
		Ignore,
		Low,
		High;
		
	public static RedstoneMode getModeFromInt(int mode) {
		switch (mode) {
		case 0:
			return Ignore;
		case 1:
			return Low;
		case 2:
			return High;
		default:
			break;
			}
		return null;
		}
	}
}
