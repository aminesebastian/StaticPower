package theking530.staticcore.blockentity.components.control.processing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.productivity.product.ProductType;

public class ProcessingProduct<T extends ProductType<K>, K> {
	private final T productType;
	private final K product;
	private final CaptureType captureType;
	private final boolean isTemplateProduct;
	private double amount;

	public ProcessingProduct(T productType, K product, double amount, CaptureType captureType, boolean isTemplateProduct) {
		this.productType = productType;
		this.product = product;
		this.amount = amount;
		this.captureType = captureType;
		this.isTemplateProduct = isTemplateProduct;
	}

	@SuppressWarnings("unchecked")
	public ProcessingProduct(CompoundTag tag) {
		productType = (T) StaticCoreRegistries.ProductRegistry().getValue(new ResourceLocation(tag.getString("type")));
		product = productType.deserializeProduct(tag.getString("product"));
		amount = tag.getDouble("amount");
		captureType = CaptureType.values()[tag.getByte("capture_type")];
		isTemplateProduct = tag.getBoolean("template");
	}

	public boolean isTemplateProduct() {
		return isTemplateProduct;
	}

	public K getProduct() {
		return product;
	}

	public CaptureType getCaptureType() {
		return captureType;
	}

	public int getAmountInt() {
		return (int) amount;
	}

	public double getAmount() {
		return amount;
	}

	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();

		String serializedProduct = productType.getSerializedProduct(getProduct());
		output.putString("product", serializedProduct);
		output.putDouble("amount", amount);
		output.putByte("capture_type", (byte) captureType.ordinal());
		output.putBoolean("template", isTemplateProduct);

		return output;
	}
}
