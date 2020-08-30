package theking530.staticcore.utilities;

public class SDTime {

	public static final int TICKS_PER_SECOND = 20;
	public static final int TICKS_PER_MINUTE = 60 * TICKS_PER_SECOND;
	public static final int TICKS_PER_HOUR = 60 * TICKS_PER_MINUTE;
	public static final int TICKS_PER_DAY = 24 * TICKS_PER_HOUR;
	public static final int TICKS_PER_WEEK = 7 * TICKS_PER_DAY;

	/**
	 * Converts the provided ticks into a human readable time string. Format: D
	 * HH:MM:SS
	 * 
	 * Starts with the first relevant field. For example, when 1220 ticks are
	 * provided, the result will be: 01:01 for 1 minute and one second.
	 * 
	 * @param ticks
	 * @return
	 */
	public static String ticksToTimeString(long ticks) {
		long days = ticks / TICKS_PER_DAY;
		long hours = (ticks - (days * TICKS_PER_DAY)) / TICKS_PER_HOUR;
		long minutes = (ticks - ((days * TICKS_PER_DAY) + (hours * TICKS_PER_HOUR))) / TICKS_PER_MINUTE;
		long seconds = (ticks - ((days * TICKS_PER_DAY) + (hours * TICKS_PER_HOUR) + (minutes * TICKS_PER_MINUTE))) / TICKS_PER_SECOND;

		// Keep track of where we start so we don't skip a slot.
		boolean prevPresent = false;

		StringBuilder output = new StringBuilder();
		if (days > 0) {
			output.append(days + " ");
			prevPresent = true;
		}
		if (hours > 0 || prevPresent) {
			output.append(String.format("%02d:", hours));
			prevPresent = true;
		}
		if (minutes > 0 || prevPresent) {
			output.append(String.format("%02d:", minutes));
			prevPresent = true;
		}
		if (seconds > 0 || prevPresent) {
			output.append(String.format("%02d", seconds));
		}

		return output.toString();
	}
}
