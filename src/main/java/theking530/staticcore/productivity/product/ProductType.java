package theking530.staticcore.productivity.product;

import java.util.function.Supplier;

import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.entry.ProductionEntry;

public abstract class ProductType<T> {
	private final Class<T> productClass;
	private Supplier<ProductionCache<T>> cacheType;

	public ProductType(Class<T> productClass, Supplier<ProductionCache<T>> cacheType) {
		this.productClass = productClass;
		this.cacheType = cacheType;
	}

	public abstract String getUnlocalizedName();

	public ProductionTrackingToken<T> getProductivityToken() {
		ProductionTrackingToken<T> token = createProductivityToken();
		return token;
	}

	public ProductionCache<T> createNewCacheInstance() {
		return cacheType.get();
	}

	protected abstract ProductionTrackingToken<T> createProductivityToken();

	public abstract int getProductHashCode(T product);

	public abstract ProductionEntry<T> createProductionEntry(T product);

	public Class<T> getProductClass() {
		return productClass;
	}
}
