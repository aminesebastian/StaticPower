package theking530.staticpower.blockentities.components;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.product.ProductType;

public class ProductionTrackingComponent extends AbstractBlockEntityComponent {
	private final Map<ProductType<?>, ProductionTrackingToken<?>> tokens;

	public ProductionTrackingComponent(String name) {
		super(name);
		tokens = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <T> ProductionTrackingToken<T> getToken(ProductType<T> productType) {
		if (!tokens.containsKey(productType)) {
			tokens.put(productType, productType.getProductivityToken());
		}
		return (ProductionTrackingToken<T>) tokens.get(productType);
	}

	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		invalidateAll();
	}

	public void invalidateAll() {
		for (ProductionTrackingToken<?> token : tokens.values()) {
			token.invalidate();
		}
	}
}
