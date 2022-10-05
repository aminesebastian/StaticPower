package theking530.staticpower.teams.production.metrics;

public enum MetricType {
	CONSUMPTION("consumption"), PRODUCTION("production");

	private String queryField;

	MetricType(String queryField) {
		this.queryField = queryField;
	}

	public String getQueryField() {
		return queryField;
	}
}
