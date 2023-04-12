package theking530.staticcore.productivity;

import java.util.HashSet;
import java.util.Set;

import theking530.staticcore.productivity.cacheentry.ProductionEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.ServerTeam;

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
	public void setProductionPerSecond(ServerTeam team, T product, double productionPerSecond) {
		setProductionPerSecond(team, product, productionPerSecond, productionPerSecond);
	}

	public void setProductionPerSecond(ServerTeam team, T product, double productionPerSecond,
			double idealProductionPerSecond) {
		if (team == null || !getType().isValidProduct(product)) {
			return;
		}
		ServerProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addOrUpdateProductionRate(this, product, getType().getProductHashCode(product),
				productionPerSecond, idealProductionPerSecond);
		trackedProductionEntries.add(entry);
	}

	/**
	 * Sets the consumption rate of the provided product by the owner of this token.
	 * This is safe to call multiple times for the same product. Subsequent calls
	 * will overwrite the consumption rate.
	 * 
	 * @param team
	 * @param product
	 * @param consumptionPerSecond
	 */
	public void setConsumptionPerSecond(ServerTeam team, T product, double consumptionPerSecond) {
		setConsumptionPerSecond(team, product, consumptionPerSecond, consumptionPerSecond);
	}

	public void setConsumptionPerSecond(ServerTeam team, T product, double consumptionPerSecond,
			double idealConsumptionPerSecond) {
		if (team == null || !getType().isValidProduct(product)) {
			return;
		}
		ServerProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addOrUpdateConsumptionRate(this, product,
				getType().getProductHashCode(product), consumptionPerSecond, idealConsumptionPerSecond);
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
	public void produced(ServerTeam team, T product, double amount) {
		if (team == null || !getType().isValidProduct(product)) {
			return;
		}
		ServerProductionCache<T> cache = getProductionCache(team);
		cache.addProduced(product, getType().getProductHashCode(product), amount);
	}

	/**
	 * This should be called whenever a product is consumed by the owner of this
	 * token.
	 * 
	 * @param team
	 * @param product
	 * @param amount
	 */
	public void consumed(ServerTeam team, T product, double amount) {
		if (team == null || !getType().isValidProduct(product)) {
			return;
		}
		ServerProductionCache<T> cache = getProductionCache(team);
		cache.addConsumed(product, getType().getProductHashCode(product), amount);
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

	@SuppressWarnings("unchecked")
	private ServerProductionCache<T> getProductionCache(ServerTeam team) {
		IProductionManager<?> prodManager = team.getProductionManager();
		return (ServerProductionCache<T>) prodManager.getProductTypeCache(getType());
	}
}
