package theking530.staticpower.tileentities.components.power;

public interface IPowerMetricsSyncConsumer {
	public void recieveMetrics(TransferMetrics secondsMetrics, TransferMetrics minuteMetrics, TransferMetrics hourlyMetrics);
}
