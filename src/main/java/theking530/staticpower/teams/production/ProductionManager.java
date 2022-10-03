package theking530.staticpower.teams.production;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.StaticPower;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.production.ProductionEntry.MetricPeriod;
import theking530.staticpower.utilities.ItemUtilities;

public class ProductionManager {
	private final Team team;
	private final Map<Integer, ProductionEntry<ItemStack>> itemProductivity;

	public ProductionManager(Team team) {
		this.team = team;
		itemProductivity = new HashMap<>();
		initializeDatabase();
	}

	public void tick(long gameTime) {
		if (gameTime % SDTime.TICKS_PER_SECOND == 0) {
			int gameSecond = (int) (gameTime / SDTime.TICKS_PER_SECOND);
			int secondPeriodValue = gameSecond % 60;
			insertProductivityPerSecond("item", secondPeriodValue);
		}

		if (gameTime % SDTime.TICKS_PER_MINUTE == 0) {
			int gameMinute = (int) (gameTime / SDTime.TICKS_PER_MINUTE);
			int minutePeriodValue = gameMinute % 60;
			StaticPower.LOGGER.debug(String.format("Inserting data for minute: %1$d.", minutePeriodValue));
			updateAggregateData("item", MetricPeriod.SECOND, MetricPeriod.MINUTE, minutePeriodValue);
		}

		if (gameTime % SDTime.TICKS_PER_HOUR == 0) {
			int gameHour = (int) (gameTime / SDTime.TICKS_PER_HOUR);
			int hourPeriodValue = gameHour % 24;
			StaticPower.LOGGER.debug(String.format("Inserting data for hour: %1$d.", hourPeriodValue));
			updateAggregateData("item", MetricPeriod.MINUTE, MetricPeriod.HOUR, hourPeriodValue);
		}

		if (gameTime % SDTime.TICKS_PER_DAY == 0) {
			int gameDay = (int) (gameTime / SDTime.TICKS_PER_DAY);
			int dayPeriodValue = gameDay % 30;
			StaticPower.LOGGER.debug(String.format("Inserting data for day: %1$d.", dayPeriodValue));
			updateAggregateData("item", MetricPeriod.HOUR, MetricPeriod.DAY, dayPeriodValue);
		}
	}

	public void itemInserted(ItemStack stack, int count) {
		if (!stack.isEmpty()) {
			int hash = ItemUtilities.getItemStackHash(stack);
			if (!itemProductivity.containsKey(hash)) {
				ItemStack singleItemStack = stack.copy();
				singleItemStack.setCount(1);
				itemProductivity.put(hash, new ItemProductionEntry(singleItemStack));
			}
			itemProductivity.get(hash).inserted(count);
		}
	}

	public void itemExtracted(ItemStack stack, int count) {
		if (!stack.isEmpty() && count > 0) {
			int hash = ItemUtilities.getItemStackHash(stack);
			if (!itemProductivity.containsKey(hash)) {
				ItemStack singleItemStack = stack.copy();
				singleItemStack.setCount(1);
				itemProductivity.put(hash, new ItemProductionEntry(singleItemStack));
			}
			itemProductivity.get(hash).extracted(count);
		}
	}

	protected void insertProductivityPerSecond(String productType, int periodValue) {
		try {
			Connection db = team.getDatabaseConnection();
			Statement stmt = db.createStatement();

			int count = 0;
			for (ProductionEntry<ItemStack> entry : itemProductivity.values()) {
				if (entry.currentSecondInput == 0 && entry.currentSecondOutput == 0) {
					continue;
				}
				count++;
				//@formatter:off
				String upsert = String.format("REPLACE INTO %1$s_productivity_%2$s(product_hash, serialized_product, input, output, period_value, timestamp) \n"
						+ "  VALUES('%3$d', '%4$s', '%5$d', '%6$d', '%7$d', '%8$d');",
						productType, MetricPeriod.SECOND.getTableKey(), entry.getProductHashCode(), entry.getSerializedProduct(), 
						entry.getCurrentSecondInput(), entry.getCurrentSecondOutput(), periodValue, System.currentTimeMillis() / 1000L);
				//@formatter:on
				entry.reset();
				stmt.addBatch(upsert);
			}
			StaticPower.LOGGER.debug(String.format("Inserting data for %1$d items for second: %2$d.", count, periodValue));
			stmt.executeBatch();
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when updating the productivity for product: %1$s over period: %2$s.", productType, MetricPeriod.SECOND), e);
		}
	}

	protected void updateAggregateData(String productType, MetricPeriod fromPeriod, MetricPeriod toPeriod, int periodValue) {
		try {
			//@formatter:off
			String upsert = String.format("REPLACE INTO %1$s_productivity_%3$s \n"
					+ " SELECT product_hash, serialized_product, SUM(input) as input, SUM(output) as output, %4$d, %5$d \n"
					+ " FROM item_productivity_%2$s GROUP BY product_hash"
					+ " WHERE input > 0 AND output > 0;",
					productType, fromPeriod.getTableKey(), toPeriod.getTableKey(), periodValue, System.currentTimeMillis() / 1000L);
			//@formatter:on

			Connection db = team.getDatabaseConnection();
			Statement stmt = db.createStatement();
			stmt.execute(upsert);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when updating the productivity for product: %1$s over period: %2$s.", productType, MetricPeriod.SECOND), e);
		}
	}

	protected void initializeDatabase() {
		Connection db = team.getDatabaseConnection();

		try {
			Statement stmt = db.createStatement();
			for (MetricPeriod period : MetricPeriod.values()) {
				stmt.execute(createProductivityTable("item", period));
			}
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when creating the productivity tracking tables!"), e);
		}
	}

	private String createProductivityTable(String productType, MetricPeriod period) {
		//@formatter:off
		return String.format("CREATE TABLE IF NOT EXISTS %1$s_productivity_%2$s (\n" 
				+ "	product_hash int NOT NULL,\n" 
				+ "	serialized_product text NOT NULL,\n" 
				+ "	input integer NOT NULL,\n"
				+ "	output integer NOT NULL,\n" 
				+ "	period_value tinyint NOT NULL,\n"
				+ "	timestamp integer NOT NULL,\n"
				+ " UNIQUE(product_hash,period_value)\n"
				+ ");", productType, period.getTableKey());
		//@formatter:on
	}
}
