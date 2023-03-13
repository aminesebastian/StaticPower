package theking530.staticcore.productivity.product;

import java.util.function.Function;

import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.cacheentry.ProductionEntry;
import theking530.staticcore.utilities.SDColor;

public abstract class ProductType<T> {
	private final Class<T> productClass;
	private Function<Boolean, ProductionCache<T>> cacheType;

	public ProductType(Class<T> productClass, Function<Boolean, ProductionCache<T>> cacheType) {
		this.productClass = productClass;
		this.cacheType = cacheType;
	}

	public abstract String getUnlocalizedName(int amount);

	public final String getUnlocalizedName() {
		return getUnlocalizedName(0);
	}

	public ProductionTrackingToken<T> getProductivityToken() {
		ProductionTrackingToken<T> token = createProductivityToken();
		return token;
	}

	public ProductionCache<T> createNewCacheInstance(boolean isClientSide) {
		return cacheType.apply(isClientSide);
	}

	public SDColor getProductColor(T product) {
		int serializedHash = getProductHashCode(product);
		SDColor testColor = new SDColor(1, 1, 1, 1);
		testColor.setRed(Math.abs(serializedHash) % 100 / 100.0f);
		testColor.setGreen(Math.abs(serializedHash) % 200 / 200.0f);
		testColor.setBlue(Math.abs(serializedHash) % 300 / 300.0f);
		testColor.desaturate(-1);
		testColor.lighten(0.4f, 0.4f, 0.4f, 0.0f);
		testColor.clampToSDRRange();
		return testColor;
	}

	public SDColor getProductColor(String serializedProduct) {
		return getProductColor(deserializeProduct(serializedProduct));
	}

	public abstract boolean isValidProduct(T product);

	protected abstract ProductionTrackingToken<T> createProductivityToken();

	public abstract int getProductHashCode(T product);

	public abstract String getSerializedProduct(T product);

	public abstract T deserializeProduct(String serializedProduct);

	public abstract ProductionEntry<T> createProductionEntry(T product);

	public Class<T> getProductClass() {
		return productClass;
	}
}
