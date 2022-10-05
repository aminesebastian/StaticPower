package theking530.staticpower.teams.production;

import java.util.HashSet;
import java.util.Set;

import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.production.product.ProductType;

public class ProductionTrackingToken<T> {
	private static long NEXT_ID = 0;

	private final long id;
	private final ProductType<T, ?> type;
	private Set<ProductionEntry<T>> trackedProductionEntries;

	public <K extends ProductionEntry<T>> ProductionTrackingToken(ProductType<T, K> type) {
		this.id = NEXT_ID;
		this.type = type;
		this.trackedProductionEntries = new HashSet<>();
		NEXT_ID++;
	}

	public long getId() {
		return id;
	}

	public ProductType<T, ?> getType() {
		return type;
	}

	public void setProductionPerSecond(Team team, T product, double productionPerSecond) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addOrUpdateProductionRate(this, product, getType().getProductHashCode(product), productionPerSecond);
		trackedProductionEntries.add(entry);
	}

	public void setConsumptionPerSection(Team team, T product, double consumptionPerSection) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addOrUpdateConsumptionRate(this, product, getType().getProductHashCode(product), consumptionPerSection);
		trackedProductionEntries.add(entry);
	}

	public void produced(Team team, T product, double amount) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addProduced(product, getType().getProductHashCode(product), amount);
		trackedProductionEntries.add(entry);
	}

	public void consumed(Team team, T product, double amount) {
		if (team == null) {
			return;
		}
		ProductionCache<T> cache = getProductionCache(team);
		ProductionEntry<T> entry = cache.addConsumed(product, getType().getProductHashCode(product), amount);
		trackedProductionEntries.add(entry);
	}

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
