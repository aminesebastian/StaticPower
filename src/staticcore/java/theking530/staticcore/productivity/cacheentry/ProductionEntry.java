package theking530.staticcore.productivity.cacheentry;

import java.util.HashMap;
import java.util.Map;

import theking530.staticcore.productivity.ProductionTrackingToken;

public abstract class ProductionEntry<T> {
	public record ProductionEntryState(double consumed, double produced, ProductivityRate consumptionRate, ProductivityRate productionRate) {
		public boolean isEmpty() {
			return consumed == 0 && produced == 0 && consumptionRate.isZero() && productionRate.isZero();
		}
	}

	private static final int SMOOTHING_FACTOR = 10;

	protected T product;
	protected double currentSecondConsumed;
	protected double currentSecondProduced;
	protected Map<ProductionTrackingToken<T>, ProductivityRate> productionRates;
	protected Map<ProductionTrackingToken<T>, ProductivityRate> comsumptionRates;

	protected ProductivityRate smoothedProduction;
	protected ProductivityRate smoothedConsumption;

	public ProductionEntry(T product) {
		this();
		this.product = product;
	}

	private ProductionEntry() {
		currentSecondConsumed = 0;
		currentSecondProduced = 0;
		productionRates = new HashMap<>();
		comsumptionRates = new HashMap<>();
		smoothedProduction = new ProductivityRate(0, 0);
		smoothedConsumption = new ProductivityRate(0, 0);
	}

	public ProductionEntryState getValuesForDatabaseInsert() {
		return new ProductionEntryState(currentSecondConsumed, currentSecondProduced, getConsumptionRate(), getProductionRate());
	}

	public void tick(long gameTime) {
		double currentProduction = 0;
		double idealProduction = 0;
		for (ProductivityRate val : productionRates.values()) {
			currentProduction += val.getCurrentValue();
			idealProduction += val.getIdealValue();
		}
		smoothedProduction.interpolateTowards(currentProduction, idealProduction, SMOOTHING_FACTOR);

		double currentConsumption = 0;
		double idealConsumption = 0;
		for (ProductivityRate val : comsumptionRates.values()) {
			currentConsumption += val.getCurrentValue();
			idealConsumption += val.getIdealValue();
		}
		smoothedConsumption.interpolateTowards(currentConsumption, idealConsumption, SMOOTHING_FACTOR);
	}

	public void clearCurrentSecondMetrics() {
		currentSecondConsumed = 0;
		currentSecondProduced = 0;
	}

	public void updateProductionRate(ProductionTrackingToken<T> token, double currentProductionRate, double idealProductionRate) {
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

	public void updateConsumptionRate(ProductionTrackingToken<T> token, double currentComsumptionRate, double idealConsumptionRate) {
		if (comsumptionRates.containsKey(token)) {
			if (currentComsumptionRate <= 0 && idealConsumptionRate <= 0) {
				comsumptionRates.remove(token);
				return;
			} else {
				ProductivityRate rate = comsumptionRates.get(token);
				rate.setCurrentValue(currentComsumptionRate);
				rate.setIdealValue(idealConsumptionRate);
			}
		} else {
			comsumptionRates.put(token, new ProductivityRate(currentComsumptionRate, idealConsumptionRate));
		}
	}

	public void invalidateToken(ProductionTrackingToken<T> token) {
		if (comsumptionRates.containsKey(token)) {
			comsumptionRates.remove(token);
		}
		if (productionRates.containsKey(token)) {
			productionRates.remove(token);
		}
	}

	public T getProduct() {
		return product;
	}

	public ProductivityRate getProductionRate() {
		return smoothedProduction;
	}

	public ProductivityRate getConsumptionRate() {
		return smoothedConsumption;
	}

	public void produced(double amount) {
		currentSecondConsumed += Math.max(0, amount);
	}

	public void consumed(double amount) {
		currentSecondProduced += Math.max(0, amount);
	}

	@Override
	public String toString() {
		return "ProductionEntry [product=" + product + ", inserted=" + currentSecondConsumed + ", extracted=" + currentSecondProduced + "]";
	}
}
