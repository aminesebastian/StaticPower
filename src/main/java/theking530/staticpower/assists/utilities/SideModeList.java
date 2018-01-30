package theking530.staticpower.assists.utilities;

public class SideModeList {
	
	public enum Mode {
		Regular,
		Input,
		Output,
		Disabled;

	public static Mode getModeFromInt(int mode) {
		switch (mode) {
		case 0:
			return Regular;
		case 1:
			return Input;
		case 2:
			return Output;
		case 3:
			return Disabled;
		default:
			break;
			}
		return null;
		}
	}
}
