package theking530.staticcore.productivity;

import theking530.staticcore.productivity.product.ProductType;

public interface IProductionCache<T> {
	public void tick(long gameTime);

	public ProductType<T> getProductType();

	public boolean haveClientValuesUpdatedSince(long lastClientFetchTime);
}
