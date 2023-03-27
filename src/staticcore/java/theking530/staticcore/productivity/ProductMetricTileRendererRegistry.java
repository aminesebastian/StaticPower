package theking530.staticcore.productivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import theking530.staticcore.productivity.product.ProductType;

public class ProductMetricTileRendererRegistry {
	protected static final Map<ProductType<?>, ProductMetricTileRenderer<?, ?>> RENDERERS = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T, K extends ProductType<T>> ProductMetricTileRenderer<T, K> getRenderer(K productType) {
		return (ProductMetricTileRenderer<T, K>) RENDERERS.get(productType);
	}

	public static class RegisterEvent extends Event implements IModBusEvent {
		private static final List<MetricRendererPair> RENDERERS_TO_REGISTER = new LinkedList<>();
		private static boolean isFinalized;

		public <T, K extends ProductType<T>> void registerProductMetricRenderer(Supplier<ProductType<T>> type, Supplier<ProductMetricTileRenderer<T, K>> renderer) {
			if (isFinalized) {
				throw new RuntimeException("Attempting to register a product metric renderer too late!");
			}
			RENDERERS_TO_REGISTER.add(new MetricRendererPair(() -> type.get(), () -> renderer.get()));
		}

		public static void finalizeRegistration() {
			isFinalized = true;
			for (MetricRendererPair pair : RENDERERS_TO_REGISTER) {
				ProductMetricTileRendererRegistry.RENDERERS.put(pair.type.get(), pair.renderer().get());
			}
		}

		private record MetricRendererPair(Supplier<ProductType<?>> type, Supplier<ProductMetricTileRenderer<?, ?>> renderer) {
		}
	}
}
