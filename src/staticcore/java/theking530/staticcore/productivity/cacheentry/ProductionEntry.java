package theking530.staticcore.productivity.cacheentry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import theking530.staticcore.productivity.ProductionTrackingToken;

public abstract class ProductionEntry<T> {
	public record ProductionEntryState(double consumed, double produced, ProductivityRate consumptionRate,
			ProductivityRate productionRate) {
		public boolean isEmpty() {
			return consumed == 0 && produced == 0 && consumptionRate.isZero() && productionRate.isZero();
		}
	}

	private static final int TIME_UNTIL_REMOVAL = 100;

	protected T product;
	protected double currentSecondConsumed;
	protected double currentSecondProduced;

	protected Map<ProductionTrackingToken<T>, ProductivityRate> productionRates;
	protected Map<ProductionTrackingToken<T>, ProductivityRate> consumptionRates;
	protected ProductivityRate currentTickProduction;
	protected ProductivityRate currentTickConsumption;

	protected Map<ProductionTrackingToken<T>, Long> invalidatedTokens;
	protected Set<ProductionTrackingToken<T>> readyToRemoveTokens;

	protected long currentGameTime;

	public ProductionEntry(T product) {
		this();
		this.product = product;
	}

	private ProductionEntry() {
		currentSecondConsumed = 0;
		currentSecondProduced = 0;
		productionRates = new HashMap<>();
		consumptionRates = new HashMap<>();
		invalidatedTokens = new HashMap<>();
		readyToRemoveTokens = new HashSet<>();
		currentTickProduction = new ProductivityRate(0, 0);
		currentTickConsumption = new ProductivityRate(0, 0);
		currentGameTime = 0;
	}

	public ProductionEntryState getValuesForDatabaseInsert() {
		return new ProductionEntryState(currentSecondConsumed, currentSecondProduced, getConsumptionRate(),
				getProductionRate());
	}

	public void tick(long gameTime) {
		double currentProduction = 0, idealProduction = 0;
		for (ProductivityRate val : productionRates.values()) {
			currentProduction += val.getCurrentValue();
			idealProduction += val.getIdealValue();
		}
		currentTickProduction.setValues(currentProduction, idealProduction);

		double currentConsumption = 0, idealConsumption = 0;
		for (ProductivityRate val : consumptionRates.values()) {
			currentConsumption += val.getCurrentValue();
			idealConsumption += val.getIdealValue();
		}
		currentTickConsumption.setValues(currentConsumption, idealConsumption);

		// Remove any tokens that have not had any activity in TIME_UNTIL_REMOVAL time.
		for (ProductionTrackingToken<T> removedToken : invalidatedTokens.keySet()) {
			long timeSinceRemoved = gameTime - invalidatedTokens.get(removedToken);
			if (timeSinceRemoved >= TIME_UNTIL_REMOVAL) {
				productionRates.remove(removedToken);
				consumptionRates.remove(removedToken);
				readyToRemoveTokens.add(removedToken);
			}
		}

		for (ProductionTrackingToken<T> fullyRemoved : readyToRemoveTokens) {
			invalidatedTokens.remove(fullyRemoved);
		}
		readyToRemoveTokens.clear();
	}

	public void onProductivityCapturedIntoDatabase() {
		currentSecondConsumed = 0;
		currentSecondProduced = 0;
	}

	public void updateProductionRate(ProductionTrackingToken<T> token, double currentProductionRate,
			double idealProductionRate) {
		if (productionRates.containsKey(token)) {
			if (currentProductionRate <= 0 && idealProductionRate <= 0) {
				productionRates.remove(token);
			} else {
				ProductivityRate rate = productionRates.get(token);
				rate.setCurrentValue(currentProductionRate);
				rate.setIdealValue(idealProductionRate);
			}
		} else {
			productionRates.put(token, new ProductivityRate(currentProductionRate, idealProductionRate));
		}
	}

	public void updateConsumptionRate(ProductionTrackingToken<T> token, double currentComsumptionRate,
			double idealConsumptionRate) {
		if (consumptionRates.containsKey(token)) {
			if (currentComsumptionRate <= 0 && idealConsumptionRate <= 0) {
				consumptionRates.remove(token);
				return;
			} else {
				ProductivityRate rate = consumptionRates.get(token);
				rate.setCurrentValue(currentComsumptionRate);
				rate.setIdealValue(idealConsumptionRate);
			}
		} else {
			consumptionRates.put(token, new ProductivityRate(currentComsumptionRate, idealConsumptionRate));
		}
	}

	public void invalidateToken(ProductionTrackingToken<T> token) {
		invalidatedTokens.put(token, currentGameTime);
	}

	public T getProduct() {
		return product;
	}

	public ProductivityRate getProductionRate() {
		return currentTickProduction;
	}

	public ProductivityRate getConsumptionRate() {
		return currentTickConsumption;
	}

	public void produced(double amount) {
		currentSecondConsumed += Math.max(0, amount);
	}

	public void consumed(double amount) {
		currentSecondProduced += Math.max(0, amount);
	}

	@Override
	public String toString() {
		return "ProductionEntry [product=" + product + ", inserted=" + currentSecondConsumed + ", extracted="
				+ currentSecondProduced + "]";
	}
}
