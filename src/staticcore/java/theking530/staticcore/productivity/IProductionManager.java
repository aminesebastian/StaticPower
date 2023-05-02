package theking530.staticcore.productivity;

import theking530.staticcore.productivity.product.ProductType;

public interface IProductionManager<K extends IProductionCache<?>> {
	public <T> K getProductCache(ProductType<T> productType);

	public void tick(long gameTime);

	public boolean isClientSide();
}
