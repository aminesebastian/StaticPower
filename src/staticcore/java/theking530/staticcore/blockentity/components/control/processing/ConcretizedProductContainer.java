package theking530.staticcore.blockentity.components.control.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.power.PowerProducer;
import theking530.staticcore.utilities.NBTUtilities;

public class ConcretizedProductContainer implements INBTSerializable<CompoundTag>, IReadOnlyProcessingContainer {
	protected final Map<ProductType<?>, List<ProcessingProduct<?, ?>>> productMap;
	private boolean closed;

	public ConcretizedProductContainer() {
		productMap = new HashMap<>();
		closed = true;
	}

	public ConcretizedProductContainer addItem(ItemStack item) {
		return addItem(item, CaptureType.BOTH);
	}

	public ConcretizedProductContainer addItem(ItemStack item, CaptureType captureType) {
		return addItem(item, captureType, false);
	}

	public ConcretizedProductContainer addItem(ItemStack item, CaptureType captureType, boolean isTemplateProduct) {
		return add(StaticCoreProductTypes.Item.get(), item.copy(), item.getCount(), captureType, isTemplateProduct);
	}

	public ConcretizedProductContainer addFluid(FluidStack fluid) {
		return addFluid(fluid, CaptureType.BOTH);
	}

	public ConcretizedProductContainer addFluid(FluidStack fluid, CaptureType captureType) {
		return addFluid(fluid, captureType, false);
	}

	public ConcretizedProductContainer addFluid(FluidStack fluid, CaptureType captureType, boolean isTemplateProduct) {
		return add(StaticCoreProductTypes.Fluid.get(), fluid.copy(), fluid.getAmount(), captureType, isTemplateProduct);
	}

	public ConcretizedProductContainer addFluid(FluidStack fluid, int amount) {
		return add(StaticCoreProductTypes.Fluid.get(), fluid.copy(), amount, CaptureType.BOTH, false);
	}

	public ConcretizedProductContainer addFluid(FluidStack fluid, int amount, CaptureType captureType) {
		return add(StaticCoreProductTypes.Fluid.get(), fluid.copy(), amount, captureType, false);
	}

	public ConcretizedProductContainer addFluid(FluidStack fluid, int amount, CaptureType captureType,
			boolean isTemplateProduct) {
		return add(StaticCoreProductTypes.Fluid.get(), fluid.copy(), amount, captureType, isTemplateProduct);
	}

	public ConcretizedProductContainer addPower(PowerProducer producer, double power) {
		return addPower(producer, power, CaptureType.BOTH);
	}

	public ConcretizedProductContainer addPower(PowerProducer producer, double power, CaptureType captureType) {
		return add(StaticCoreProductTypes.Power.get(), producer, power, captureType, false);
	}

	@Override
	public boolean hasItems() {
		return hasProductsOfType(StaticCoreProductTypes.Item.get());
	}

	@Override
	public boolean hasFluids() {
		return hasProductsOfType(StaticCoreProductTypes.Fluid.get());
	}

	@Override
	public boolean hasPower() {
		return hasProductsOfType(StaticCoreProductTypes.Power.get());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, T extends ProductType<K>, W extends ProcessingProduct<T, K>> List<W> getProductsOfType(T type) {
		List<ProcessingProduct<?, K>> output = new ArrayList<ProcessingProduct<?, K>>();
		for (ProcessingProduct<?, ?> wrapper : productMap.get(type)) {
			output.add((ProcessingProduct<?, K>) wrapper);
		}
		return (List<W>) output;
	}

	@Override
	public ItemStack getItem(int index) {
		return getProductOfType(StaticCoreProductTypes.Item.get(), index).getProduct().copy();
	}

	public List<ItemStack> getItems() {
		return getProductsOfType(StaticCoreProductTypes.Item.get()).stream().map((prod) -> prod.getProduct()).toList();
	}

	@Override
	public FluidStack getFluid(int index) {
		return getProductOfType(StaticCoreProductTypes.Fluid.get(), index).getProduct().copy();
	}

	public List<FluidStack> getFluids() {
		return getProductsOfType(StaticCoreProductTypes.Fluid.get()).stream().map((prod) -> prod.getProduct()).toList();
	}

	@Override
	public double getPower(int index) {
		return getProductOfType(StaticCoreProductTypes.Power.get(), index).getAmount();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ProductType<K>, K> ProcessingProduct<T, K> getProductOfType(T type, int index) {
		return (ProcessingProduct<T, K>) productMap.get(type).get(index);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ConcretizedProductContainer add(ProductType<T> type, T product, double amount, CaptureType captureType,
			boolean isTemplateProduct) {

		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an input item to a closed process output container."));
		}

		if (amount == 0) {
			return this;
		}

		productMap.computeIfAbsent(type, (x) -> new ArrayList<>());
		productMap.get(type).add(new ProcessingProduct(type, product, amount, captureType, isTemplateProduct));
		return this;
	}

	public Set<ProductType<?>> getProductTypes() {
		return productMap.keySet();
	}

	@Override
	public boolean hasProductsOfType(ProductType<?> type) {
		return productMap.containsKey(type);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("product_types", serializeMap(productMap));

		return output;
	}

	protected ListTag serializeMap(Map<ProductType<?>, List<ProcessingProduct<?, ?>>> map) {
		ListTag serializedProductTypes = NBTUtilities.serialize(map.keySet(), (productType, typeTag) -> {
			ListTag serializedProducts = NBTUtilities.serialize(map.get(productType),
					(product) -> product.serializeNBT());

			typeTag.putString("type", StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
			typeTag.put("products", serializedProducts);
		});
		return serializedProductTypes;
	}

	public void mergeOther(ConcretizedProductContainer other) {
		for (Entry<ProductType<?>, List<ProcessingProduct<?, ?>>> entry : other.productMap.entrySet()) {
			productMap.computeIfAbsent(entry.getKey(), (x) -> new ArrayList<>());
			productMap.get(entry.getKey()).addAll(entry.getValue());
		}
	}

	public void open() {
		clear();
		closed = false;
	}

	public void close() {
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	public void clear() {
		productMap.clear();
		closed = true;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		clear();
		ListTag outputProductTypes = nbt.getList("product_types", ListTag.TAG_COMPOUND);
		productMap.putAll(serializeMap(outputProductTypes));
	}

	@SuppressWarnings("rawtypes")
	protected Map<ProductType<?>, List<ProcessingProduct<?, ?>>> serializeMap(ListTag list) {
		Map<ProductType<?>, List<ProcessingProduct<?, ?>>> output = new HashMap<>();
		for (Tag rawProductType : list) {
			CompoundTag productTypeTag = (CompoundTag) rawProductType;
			ProductType<?> type = StaticCoreRegistries.ProductRegistry()
					.getValue(new ResourceLocation(productTypeTag.getString("type")));

			ArrayList<ProcessingProduct<?, ?>> deserializedProducts = new ArrayList<>();
			ListTag products = productTypeTag.getList("products", ListTag.TAG_COMPOUND);
			for (Tag rawProduct : products) {
				CompoundTag productTag = (CompoundTag) rawProduct;
				deserializedProducts.add(new ProcessingProduct(productTag));
			}

			output.put(type, deserializedProducts);
		}
		return output;
	}
}
