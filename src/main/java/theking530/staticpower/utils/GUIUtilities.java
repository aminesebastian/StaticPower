package theking530.staticpower.utils;

public class GUIUtilities {

	public static int getColor(int R, int G, int B, int A) {
		return A << 24 | R << 16 | G << 8 | B;
	}
	public static int getColor(int R, int G, int B) {
		return 255 << 24 | R << 16 | G << 8 | B;
	}
}
