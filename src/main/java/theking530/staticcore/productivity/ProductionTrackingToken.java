package theking530.staticcore.productivity;

import java.util.HashSet;
import java.util.Set;

import theking530.staticcore.productivity.cacheentry.ProductionEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.teams.Team;

public class ProductionTrackingToken<T> {
	private final ProductType<T> type;
	private Set<ProductionEntry<T>> trackedProductionEntries;

	public <K extends ProductionEntry<T>> ProductionTrackingToken(ProductType<T> type) {
		this.type = type;
		this.trackedProductionEntries = new HashSet<>();
	}

	/**
	 * Gets the type of product this token tracks the production/consumption of.
	 * 
	 * @return
	 */
	public ProductType<T> getType() {
		return type;
	}

	/**
	 * Sets the production rate of the provided product by the owner of this token.
	 * This is safe to call multiple times for the same product. Subsequent calls
	 * will overwrite the production rate.
	 * 
	 * @param team
	 * @param product
	 * @param consumptionPerSection
	 */
	public void setProductionPerSecond(Team team, T product, double productionPerSecond) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addOrUpdateProductionRate(this, product, getType().getProductHashCode(product), productionPerSecond);
		trackedProductionEntries.add(entry);
	}

	/**
	 * Sets the consumption rate of the provided product by the owner of this token.
	 * This is safe to call multiple times for the same product. Subsequent calls
	 * will overwrite the consumption rate.
	 * 
	 * @param team
	 * @param product
	 * @param consumptionPerSection
	 */
	public void setConsumptionPerSection(Team team, T product, double consumptionPerSection) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addOrUpdateConsumptionRate(this, product, getType().getProductHashCode(product), consumptionPerSection);
		trackedProductionEntries.add(entry);
	}

	/**
	 * This should be called whenever a production is produced by the owner of this
	 * token.
	 * 
	 * @param team
	 * @param product
	 * @param amount
	 */
	public void produced(Team team, T product, double amount) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addProduced(product, getType().getProductHashCode(product), amount);
		trackedProductionEntries.add(entry);
	}

	/**
	 * This should be called whenever a product is consumed by the owner of this
	 * token.
	 * 
	 * @param team
	 * @param product
	 * @param amount
	 */
	public void consumed(Team team, T product, double amount) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addConsumed(product, getType().getProductHashCode(product), amount);
		trackedProductionEntries.add(entry);
	}

	/**
	 * This should be called whenever production is paused or stopped. This will
	 * clear our the production and consumption rates that were added through this
	 * token.
	 */
	public void invalidate() {
		// Make sure we're removed from all the entries this token has tracked.
		for (ProductionEntry<T> entry : trackedProductionEntries) {
			entry.invalidateToken(this);
		}
		trackedProductionEntries.clear();
	}

	private ProductionCache<T> getProductionCache(Team team) {
		ProductionManager prodManager = team.getProductionManager();
		return prodManager.getCache(getType());
	}
}
