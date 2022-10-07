package theking530.staticcore.productivity.entry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import theking530.staticcore.productivity.ProductionTrackingToken;

public abstract class ProductionEntry<T> {
	public record ProductionEntryState(double consumed, double produced, double consumptionRate, double productionRate) {
		public boolean isEmpty() {
			return consumed == 0 && produced == 0 && consumptionRate == 0 && productionRate == 0;
		}
	}

	private static final int MAX_STOERED_RATES = 40;

	protected T product;
	protected double currentSecondConsumed;
	protected double currentSecondProduced;
	protected Map<ProductionTrackingToken<T>, Double> productionRates;
	protected Map<ProductionTrackingToken<T>, Double> comsumptionRates;

	protected Queue<Double> historicalProductionRates;
	protected Queue<Double> historicalConsumptionRate;

	public ProductionEntry(T product) {
		this();
		this.product = product;
	}

	private ProductionEntry() {
		currentSecondConsumed = 0;
		currentSecondProduced = 0;
		productionRates = new HashMap<>();
		comsumptionRates = new HashMap<>();
		historicalProductionRates = new LinkedList<>();
		historicalConsumptionRate = new LinkedList<>();
	}

	public ProductionEntryState getValuesForDatabaseInsert() {
		return new ProductionEntryState(currentSecondConsumed, currentSecondProduced, getConsumptionRate(), getProductionRate());
	}

	public void tick(long gameTime) {
		if (historicalProductionRates.size() >= 60) {
			historicalProductionRates.poll();
		}
		if (historicalConsumptionRate.size() >= 60) {
			historicalConsumptionRate.poll();
		}

		double production = 0;
		for (double val : productionRates.values()) {
			production += val;
		}
		historicalProductionRates.add(production);

		double consumption = 0;
		for (double val : comsumptionRates.values()) {
			consumption += val;
		}
		historicalConsumptionRate.add(consumption);
	}

	public void clearCurrentSecondMetrics() {
		currentSecondConsumed = 0;
		currentSecondProduced = 0;
	}

	public void updateProductionRate(ProductionTrackingToken<T> token, double productionRate) {
		if (productionRates.containsKey(token)) {
			if (productionRate == 0) {
				productionRates.remove(token);
				return;
			}
		}
		productionRates.put(token, productionRate);
	}

	public void updateConsumptionRate(ProductionTrackingToken<T> token, double comsumptionRate) {
		if (comsumptionRates.containsKey(token)) {
			if (comsumptionRate == 0) {
				comsumptionRates.remove(token);
				return;
			}
		}
		comsumptionRates.put(token, comsumptionRate);
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

	public double getProductionRate() {
		double production = 0;
		for (double val : historicalProductionRates) {
			production += val;
		}
		return production / Math.max(historicalProductionRates.size(), 1);
	}

	public double getConsumptionRate() {
		double consumption = 0;
		for (double val : historicalConsumptionRate) {
			consumption += val;
		}
		return consumption / Math.max(historicalConsumptionRate.size(), 1);
	}

	public void produced(double amount) {
		currentSecondConsumed += Math.max(0, amount);
	}

	public void consumed(double amount) {
		currentSecondProduced += Math.max(0, amount);
	}

	public abstract int getProductHashCode();

	public abstract String getSerializedProduct();

	@Override
	public String toString() {
		return "ProductionEntry [product=" + product + ", inserted=" + currentSecondConsumed + ", extracted=" + currentSecondProduced + "]";
	}
}
