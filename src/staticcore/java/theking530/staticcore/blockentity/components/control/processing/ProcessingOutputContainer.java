package theking530.staticcore.blockentity.components.control.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.power.PowerProducer;
import theking530.staticcore.utilities.NBTUtilities;

public class ProcessingOutputContainer implements INBTSerializable<CompoundTag> {
	protected final Map<ProductType<?>, List<ProcessingProduct<?, ?>>> outputMap;
	private boolean closed;

	public ProcessingOutputContainer() {
		outputMap = new HashMap<>();
		closed = true;
	}

	public ProcessingOutputContainer addOutputItem(ItemStack item, CaptureType captureType) {
		return addOutputItem(item, captureType, false);
	}

	public ProcessingOutputContainer addOutputItem(ItemStack item, CaptureType captureType, boolean isTemplateItem) {
		return addOutput(StaticCoreProductTypes.Item.get(), item.copy(), item.getCount(), captureType, isTemplateItem);
	}

	public ProcessingOutputContainer addOutputFluid(FluidStack fluid, CaptureType captureType) {
		return addOutputFluid(fluid, captureType, false);
	}

	public ProcessingOutputContainer addOutputFluid(FluidStack fluid, CaptureType captureType, boolean isTemplateItem) {
		return addOutput(StaticCoreProductTypes.Fluid.get(), fluid.copy(), fluid.getAmount(), captureType,
				isTemplateItem);
	}

	public ProcessingOutputContainer addOutputPower(PowerProducer producer, double power, CaptureType captureType,
			boolean isTemplateItem) {
		return addOutput(StaticCoreProductTypes.Power.get(), producer, power, captureType, isTemplateItem);
	}

	public ItemStack getOutputItem(int index) {
		return getOutput(StaticCoreProductTypes.Item.get(), index).getProduct().copy();
	}

	public FluidStack getOutputFluid(int index) {
		return getOutput(StaticCoreProductTypes.Fluid.get(), index).getProduct().copy();
	}

	public double getOutputPower(int index) {
		return getOutput(StaticCoreProductTypes.Power.get(), index).getAmount();
	}

	@SuppressWarnings("unchecked")
	public <T extends ProductType<K>, K> ProcessingProduct<T, K> getOutput(T type, int index) {
		return (ProcessingProduct<T, K>) outputMap.get(type).get(index);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ProcessingOutputContainer addOutput(ProductType<T> type, T product, double amount,
			CaptureType captureType, boolean isTemplateItem) {

		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an input item to a clowed process output container."));
		}

		if (amount == 0) {
			return this;
		}

		outputMap.computeIfAbsent(type, (x) -> new ArrayList<>());
		outputMap.get(type).add(new ProcessingProduct(type, product, amount, captureType, isTemplateItem));
		return this;
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
		outputMap.clear();
		closed = true;
	}

	public Set<ProductType<?>> getOutputProductTypes() {
		return outputMap.keySet();
	}

	public boolean hasOutputProductsOfType(ProductType<?> type) {
		return outputMap.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	public <K> List<ProcessingProduct<?, K>> getOutputProductsOfType(ProductType<K> type) {
		List<ProcessingProduct<?, K>> output = new ArrayList<ProcessingProduct<?, K>>();
		for (ProcessingProduct<?, ?> wrapper : outputMap.get(type)) {
			output.add((ProcessingProduct<?, K>) wrapper);
		}
		return output;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("output_product_types", serializeMap(outputMap));

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

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		clear();
		ListTag outputProductTypes = nbt.getList("output_product_types", ListTag.TAG_COMPOUND);
		outputMap.putAll(serializeMap(outputProductTypes));
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
