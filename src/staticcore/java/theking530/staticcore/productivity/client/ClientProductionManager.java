package theking530.staticcore.productivity.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.IProductionManager;
import theking530.staticcore.productivity.product.ProductType;

public class ClientProductionManager implements IProductionManager<ClientProductionCache<?>> {
	private final Map<ProductType<?>, ClientProductionCache<?>> cache;

	public ClientProductionManager() {
		cache = new HashMap<>();
		Collection<ProductType<?>> registeredProducts = StaticCoreRegistries.ProductRegistry().getValues();
		for (ProductType<?> productType : registeredProducts) {
			cache.put(productType, productType.createClientCache());
		}
	}

	@Override
	public void tick(long gameTime) {
		for (ClientProductionCache<?> prodCache : cache.values()) {
			prodCache.tick(gameTime);
		}
	}

	@Override
	public boolean isClientSide() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> ClientProductionCache<T> getProductTypeCache(ProductType<T> productType) {
		if (cache.containsKey(productType)) {
			return (ClientProductionCache<T>) cache.get(productType);
		}
		return null;
	}
}
