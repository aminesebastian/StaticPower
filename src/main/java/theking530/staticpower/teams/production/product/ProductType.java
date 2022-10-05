package theking530.staticpower.teams.production.product;

import theking530.staticpower.teams.production.ProductionEntry;
import theking530.staticpower.teams.production.ProductionTrackingToken;

public abstract class ProductType<T, K extends ProductionEntry<T>> {
	private final Class<T> productClass;

	public ProductType(Class<T> productClass) {
		this.productClass = productClass;
	}

	public abstract String getUnlocalizedName();

	public ProductionTrackingToken<T> getProductivityToken() {
		ProductionTrackingToken<T> token = createProductivityToken();
		return token;
	}

	protected abstract ProductionTrackingToken<T> createProductivityToken();

	public abstract int getProductHashCode(T product);

	public abstract K createProductionEntry(T product);

	public Class<T> getProductClass() {
		return productClass;
	}
}
