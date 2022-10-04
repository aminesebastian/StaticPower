package theking530.staticpower.teams.production.metrics;

import theking530.staticcore.utilities.SDTime;

public enum MetricPeriod {
	//@formatter:off
		SECOND("second", SDTime.TICKS_PER_SECOND * 240,  1), 
		MINUTE("minute", SDTime.TICKS_PER_MINUTE * 240, 60),
		HOUR("hour", SDTime.TICKS_PER_HOUR * 48,  3600), 
		DAY("day", -1, 86400);
		//@formatter:on

	private final String tableKey;
	private final int periodLengthInSeconds;
	private final int metricPeriodInTicks;
	private final int maxMetricAgeTicks;

	MetricPeriod(String tableKey, int maxRecordAgeTicks, int periodLengthInSeconds) {
		this.tableKey = tableKey;
		this.maxMetricAgeTicks = maxRecordAgeTicks;
		this.periodLengthInSeconds = periodLengthInSeconds;
		this.metricPeriodInTicks = periodLengthInSeconds * SDTime.TICKS_PER_SECOND;
	}

	public String getTableKey() {
		return tableKey;
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