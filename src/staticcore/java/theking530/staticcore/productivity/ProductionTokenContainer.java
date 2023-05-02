package theking530.staticcore.productivity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.power.PowerProducer;

public class ProductionTokenContainer {
	/**
	 * A container for all of our productivity tokens.
	 */
	private final Map<ProductType<?>, ProductionTrackingToken<?>> productionTokens;
	/**
	 * The power product id for the owning block. This is used to group similar
	 * machines in the production UI.
	 */
	private PowerProducer powerProducerId;

	public ProductionTokenContainer() {
		this.productionTokens = new HashMap<ProductType<?>, ProductionTrackingToken<?>>();
	}

	public ProductionTokenContainer(Block block) {
		this();
		powerProducerId = new PowerProducer(block);
	}

	public void setPowerProducerId(PowerProducer powerProducerId) {
		this.powerProducerId = powerProducerId;
	}

	public PowerProducer getPowerProducerId() {
		return this.powerProducerId;
	}

	@SuppressWarnings("unchecked")
	public <G> ProductionTrackingToken<G> getProductionToken(ProductType<G> productType) {
		if (!productionTokens.containsKey(productType)) {
			productionTokens.put(productType, productType.getProductivityToken());
		}
		return (ProductionTrackingToken<G>) productionTokens.get(productType);
	}

	public void invalidateProductionTokens() {
		for (ProductionTrackingToken<?> token : productionTokens.values()) {
			token.invalidate();
		}
		productionTokens.clear();
	}
}
