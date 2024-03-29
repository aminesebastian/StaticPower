package theking530.staticcore.productivity.metrics;

import theking530.staticcore.utilities.SDTime;

public enum MetricPeriod {
	//@formatter:off
		SECOND("second", "gui.staticcore.seconds", "gui.staticcore.seconds.short", SDTime.TICKS_PER_SECOND * 60,  1), 
		MINUTE("minute", "gui.staticcore.minutes", "gui.staticcore.minutes.short", SDTime.TICKS_PER_MINUTE * 60, 60),
		HOUR("hour", "gui.staticcore.hours", "gui.staticcore.hours.short", SDTime.TICKS_PER_HOUR * 24,  3600), 
		DAY("day", "gui.staticcore.days", "gui.staticcore.days.short",  -1, 86400);
		//@formatter:on

	private final String tableKey;
	private final String unlocalizedName;
	private final String unlocalizedShortName;
	private final int periodLengthInSeconds;
	private final int metricPeriodInTicks;
	private final int maxMetricAgeTicks;

	MetricPeriod(String tableKey, String longName, String shortName, int maxRecordAgeTicks, int periodLengthInSeconds) {
		this.tableKey = tableKey;
		this.unlocalizedName = longName;
		this.unlocalizedShortName = shortName;
		this.maxMetricAgeTicks = maxRecordAgeTicks;
		this.periodLengthInSeconds = periodLengthInSeconds;
		this.metricPeriodInTicks = periodLengthInSeconds * SDTime.TICKS_PER_SECOND;
	}

	public String getTableKey() {
		return tableKey;
	}

	public String getUnlocalizedShortName() {
		return unlocalizedShortName;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public int getMetricPeriodInTicks() {
		return metricPeriodInTicks;
	}

	public int getMaxRecordsAgeTicks() {
		return maxMetricAgeTicks;
	}

	public int getPeriodLengthInSeconds() {
		return periodLengthInSeconds;
	}
}