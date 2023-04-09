package theking530.staticcore.blockentity.components.control.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.product.ProductType;

public class ProcessingContainer extends ProcessingOutputContainer {
	private final Map<ProductType<?>, List<ProcessingProductWrapper<?, ?>>> inputMap;
	private CompoundTag customParameters;

	public ProcessingContainer() {
		inputMap = new HashMap<>();
		customParameters = null;
	}

	public ProcessingContainer addInputItem(ItemStack item, CaptureType captureType) {
		return addInputItem(item, captureType, false);
	}

	public ProcessingContainer addInputItem(ItemStack item, CaptureType captureType, boolean isTemplateItem) {
		return addInput(StaticCoreProductTypes.Item.get(), item, item.getCount(), captureType, isTemplateItem);
	}

	public ProcessingContainer addInputFluid(FluidStack fluid, CaptureType captureType) {
		return addInputFluid(fluid, captureType, false);
	}

	public ProcessingContainer addInputFluid(FluidStack fluid, CaptureType captureType, boolean isTemplateItem) {
		return addInput(StaticCoreProductTypes.Fluid.get(), fluid, fluid.getAmount(), captureType, isTemplateItem);
	}

	public ItemStack getInputItem(int index) {
		return getInput(StaticCoreProductTypes.Item.get(), index).copy();
	}

	public FluidStack getInputFluid(int index) {
		return getInput(StaticCoreProductTypes.Fluid.get(), index).copy();
	}

	@SuppressWarnings("unchecked")
	public <T> T getInput(ProductType<T> type, int index) {
		List<ProcessingProductWrapper<?, ?>> list = inputMap.get(type);
		return (T) list.get(index).getProduct();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ProcessingContainer addInput(ProductType<T> type, T product, int amount, CaptureType captureType,
			boolean isTemplateItem) {

		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an input item to a clowed process output container."));
		}

		if (amount == 0) {
			return this;
		}

		inputMap.computeIfAbsent(type, (x) -> new ArrayList<>());
		inputMap.get(type).add(new ProcessingProductWrapper(type, product, amount, captureType, isTemplateItem));
		return this;
	}

	public void mergeOutputContainer(ProcessingOutputContainer other) {
		for (Entry<ProductType<?>, List<ProcessingProductWrapper<?, ?>>> entry : other.outputMap.entrySet()) {
			outputMap.computeIfAbsent(entry.getKey(), (x) -> new ArrayList<>());
			outputMap.get(entry.getKey()).addAll(entry.getValue());
		}
	}

	@Override
	public void clear() {
		super.clear();
		inputMap.clear();
		customParameters = new CompoundTag();
	}

	public Set<ProductType<?>> geInputProductTypes() {
		return inputMap.keySet();
	}

	public boolean hasInputProductsOfType(ProductType<?> type) {
		return inputMap.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	public <K, T extends ProductType<K>, W extends ProcessingProductWrapper<T, K>> List<W> getInputProductsOfType(
			T type) {
		return (List<W>) inputMap.get(type);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = super.serializeNBT();
		;
		output.put("custom_data", customParameters);
		output.put("input_product_types", serializeMap(inputMap));

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		super.deserializeNBT(nbt);

		customParameters = nbt.getCompound("custom_data");

		ListTag inputProductTypes = nbt.getList("input_product_types", ListTag.TAG_COMPOUND);
		inputMap.putAll(serializeMap(inputProductTypes));
	}
}
