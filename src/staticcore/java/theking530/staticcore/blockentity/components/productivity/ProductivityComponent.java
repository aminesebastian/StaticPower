package theking530.staticcore.blockentity.components.productivity;

import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.productivity.ProductionTokenContainer;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.power.PowerProducer;

public class ProductivityComponent extends AbstractBlockEntityComponent {
	private final ProductionTokenContainer container;

	public ProductivityComponent(String name) {
		super(name);
		this.container = new ProductionTokenContainer();
	}

	@Override
	public void onRegistered(BlockEntityBase owner) {
		super.onRegistered(owner);
		container.setPowerProducerId(new PowerProducer(owner.getBlockState().getBlock()));
	}

	public PowerProducer getPowerProducerId() {
		return container.getPowerProducerId();

	}

	public <G> ProductionTrackingToken<G> getProductionToken(ProductType<G> productType) {
		return container.getProductionToken(productType);
	}

	protected void invalidateProductionTokens() {
		container.invalidateProductionTokens();
	}
}
