package theking530.staticpower.teams.production;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import theking530.staticpower.StaticPower;
import theking530.staticpower.teams.production.metrics.Metric;
import theking530.staticpower.teams.production.metrics.MetricPeriod;
import theking530.staticpower.teams.production.metrics.SerializedMetricPeriod;

public abstract class AbstractProductivityCache<T> {
	private final List<Map<Integer, AbstractProductionEntry<T>>> productivityBuckets;
	private final Map<Integer, Integer> productivityBucketMap;
	private final Connection database;
	private int bucketRoundRobinIndex;

	public AbstractProductivityCache(Connection database) {
		this.database = database;

		bucketRoundRobinIndex = 0;
		productivityBucketMap = new HashMap<>();
		productivityBuckets = new LinkedList<Map<Integer, AbstractProductionEntry<T>>>();
		for (int i = 0; i < 20; i++) {
			productivityBuckets.add(new HashMap<Integer, AbstractProductionEntry<T>>());
		}
	}

	public void initializeDatabase() {
		createProductLookupTable();
	}

	public void capturePerSecondMetrics() {
		for (Map<Integer, AbstractProductionEntry<T>> bucket : productivityBuckets) {
			for (AbstractProductionEntry<T> entry : bucket.values()) {
				entry.captureCurrentSecondMetric();
			}
		}
	}

	public void insertProductivityPerSecond(Connection database, int bucketIndex, long gameTime) {
		List<AbstractProductionEntry<T>> toBeInserted = new LinkedList<>();

		try {
			Collection<AbstractProductionEntry<T>> bucket = productivityBuckets.get(bucketIndex).values();
			Statement stmt = database.createStatement();

			for (AbstractProductionEntry<T> entry : bucket) {
				Metric metric = entry.captureCurrentSecondMetric();
				if (metric.getInput() == 0 && metric.getOutput() == 0) {
					continue;
				}

				//@formatter:off
				String upsert = String.format("REPLACE INTO %1$s_productivity_%2$s(product_hash, input, output, game_tick) \n"
						+ "  VALUES('%3$d', '%4$d', '%5$d', '%6$d');",
						getProductType(), MetricPeriod.SECOND.getTableKey(), entry.getProductHashCode(), (int)metric.getInput(), (int)metric.getOutput(), gameTime);
				//@formatter:on
				stmt.addBatch(upsert);
				toBeInserted.add(entry);
			}

			if (!toBeInserted.isEmpty()) {
				StaticPower.LOGGER.trace(String.format("Inserting %1$d entries for bucket: %2$d for product: %3$s.", toBeInserted.size(), bucketIndex, getProductType()));
				stmt.executeBatch();

				// Only if we made it this far should we clear the entires.
				for (AbstractProductionEntry<T> entry : toBeInserted) {
					entry.clearCurrentSecondMetrics();
				}
			}

		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when inserting the per second productivity for product: %1$s.", getProductType()), e);
		}
	}

	public void updateAggregateData(Connection database, MetricPeriod fromPeriod, MetricPeriod toPeriod, long gameTime) {
		StaticPower.LOGGER.trace(String.format("Updating aggregate data for product type: %1$s from period: %2$s to period: %3$s.", getProductType(), fromPeriod.getTableKey(),
				toPeriod.getTableKey()));
		try {
			//@formatter:off
			String upsert = String.format("REPLACE INTO %1$s_productivity_%3$s \n"
					+ " SELECT product_hash, SUM(input) as input, SUM(output) as output, %4$d \n"
					+ " FROM item_productivity_%2$s \n"
					+ " WHERE game_tick > %5$d \n"
					+ " GROUP BY product_hash;",
					getProductType(), fromPeriod.getTableKey(), toPeriod.getTableKey(), gameTime, gameTime - toPeriod.getMetricPeriodInTicks());
			//@formatter:on

			Statement stmt = database.createStatement();
			stmt.execute(upsert);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when aggregating the productivity for product: %1$s from period: %2$s to period: %3$s.", getProductType(),
					fromPeriod.getTableKey(), toPeriod.getTableKey()), e);
		}
	}

	public List<SerializedMetricPeriod> getAverageProductionRate(MetricPeriod period) {
		List<SerializedMetricPeriod> metrics = new LinkedList<SerializedMetricPeriod>();

		try {
		//@formatter:off
		String query = "SELECT \r\n"
				+ "	product.serialized_product, \r\n"
				+ "	SUM(input) as input_sum,\r\n"
				+ "	SUM(output) as output_sum,\r\n"
				+ "	(MAX(game_tick) - MIN(game_tick)) as ticks\r\n"
				+ "FROM \r\n"
				+ "	item_product as product\r\n"
				+ "		LEFT JOIN\r\n"
				+ "	item_productivity_second as metric\r\n"
				+ "		ON\r\n"
				+ "	product.product_hash = metric.product_hash\r\n"
				+ " GROUP BY\r\n"
				+ "	product.product_hash"
				+ " ORDER BY\r\n"
				+ " CASE WHEN output > input THEN output ELSE input END DESC";
		//@formatter:on
			Statement stmt = database.createStatement();
			ResultSet result = stmt.executeQuery(query);
			while (result.next()) {
				float actualRatio = (float) MetricPeriod.SECOND.getMaxRecordsAgeTicks() / result.getInt("ticks");
				if (Float.isInfinite(actualRatio)) {
					actualRatio = 1;
				}
				String product = result.getString("serialized_product");
				float seconds = Math.max(period.getPeriodLengthInSeconds(), (result.getInt("ticks") * actualRatio) / 20);
				float input = (result.getInt("input_sum") / seconds) * period.getPeriodLengthInSeconds();
				float output = (result.getInt("output_sum") / seconds) * period.getPeriodLengthInSeconds();
				metrics.add(new SerializedMetricPeriod(product, input, output, period));
			}
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when pulling the average production rage for product type: %1$s.", getProductType()), e);
		}

		return metrics;
	}

	public void clearOldEntries(Connection database, MetricPeriod period, long gameTime) {
		if (period.getMaxRecordsAgeTicks() == -1) {
			return;
		}

		StaticPower.LOGGER.trace(String.format("Deleting old data for product: %1$s over period: %2$s.", getProductType(), period.getTableKey()));
		try {
			//@formatter:off
			String upsert = String.format("DELETE FROM %1$s_productivity_%2$s \n"
					+ " WHERE game_tick < %3$d;",
					getProductType(), period.getTableKey(),  gameTime - period.getMaxRecordsAgeTicks());
			//@formatter:on

			Statement stmt = database.createStatement();
			stmt.execute(upsert);
		} catch (SQLException e) {
			StaticPower.LOGGER
					.error(String.format("An error occured when clearing old data for product: %1$s over period: %2$s.", getProductType(), period.getMaxRecordsAgeTicks()), e);
		}
	}

	public Map<Integer, AbstractProductionEntry<T>> getBucket(int bucketIndex) {
		return productivityBuckets.get(bucketIndex);
	}

	public boolean containsProduct(int productHash) {
		return productivityBucketMap.containsKey(productHash);
	}

	public void increment(T product, int productHash, int input, int output) {
		if (containsProduct(productHash)) {
			Map<Integer, AbstractProductionEntry<T>> bucket = productivityBuckets.get(productivityBucketMap.get(productHash));
			bucket.get(productHash).inserted(input);
			bucket.get(productHash).extracted(output);
		} else {
			AbstractProductionEntry<T> entry = createNewEntry(product);
			entry.inserted(input);
			entry.extracted(output);
			insertNew(entry);
		}
	}

	public void insertNew(AbstractProductionEntry<T> entry) {
		if (containsProduct(entry.getProductHashCode())) {
			throw new RuntimeException("Attempted to insert a product entry for a product we're already tracking!");
		}
		productivityBucketMap.put(entry.getProductHashCode(), bucketRoundRobinIndex);
		Map<Integer, AbstractProductionEntry<T>> bucket = productivityBuckets.get(bucketRoundRobinIndex);
		bucket.put(entry.getProductHashCode(), entry);
		bucketRoundRobinIndex = (bucketRoundRobinIndex + 1) % productivityBuckets.size();
		insertNewProduct(entry);
	}

	protected void createProductLookupTable() {
		//@formatter:off
		String tableQuery =  String.format("CREATE TABLE IF NOT EXISTS %1$s_product (\n" 
				+ "	product_hash int NOT NULL,\n" 
				+ "	serialized_product text NOT NULL,\n"
				+ " UNIQUE(product_hash,serialized_product)\n"
				+ ")", getProductType());
		//@formatter:on

		try {
			Statement stmt = getDatabase().createStatement();
			stmt.execute(tableQuery);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when creating the lookup table for product type: $1$s!", getProductType()), e);
		}
	}

	protected void insertNewProduct(AbstractProductionEntry<T> entry) {
		//@formatter:off
			String insert = String.format("REPLACE INTO %1$s_product(product_hash, serialized_product) \n"
					+ "  VALUES('%2$d', '%3$s');",
					getProductType(), entry.getProductHashCode(), entry.getSerializedProduct());
			//@formatter:on
		try {
			Statement stmt = getDatabase().createStatement();
			stmt.execute(insert);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when inserting a new product entry for product: $1$s!", entry.getSerializedProduct()), e);
		}
	}

	protected abstract AbstractProductionEntry<T> createNewEntry(T product);

	protected abstract String getProductType();

	protected Connection getDatabase() {
		return database;
	}
}
