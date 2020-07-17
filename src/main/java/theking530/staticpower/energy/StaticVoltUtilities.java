package theking530.staticpower.energy;

public class StaticVoltUtilities {
	public static int getMilliWatts(int milliVolts, int milliAmps) {
		return (int) ((milliVolts * milliAmps) / 1e3);
	}
}
