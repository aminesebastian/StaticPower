package theking530.staticcore.productivity.product.power;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.Items;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.cacheentry.PowerProductionEntry;
import theking530.staticcore.productivity.product.ProductType;

public class PowerProductType extends ProductType<PowerProductionStack> {

	public PowerProductType() {
		super(PowerProductionStack.class, (isClientSide) -> new ProductionCache<PowerProductionStack>(StaticCoreProductTypes.Power.get(), isClientSide));
	}

	@Override
	public String getUnlocalizedName(int amount) {
		return "gui.staticpower.product.power";
	}

	@Override
	public ProductionTrackingToken<PowerProductionStack> createProductivityToken() {
		return new ProductionTrackingToken<PowerProductionStack>(this);
	}

	@Override
	public int getProductHashCode(PowerProductionStack product) {
		return product.getProductHashCode();
	}

	@Override
	public PowerProductionEntry createProductionEntry(PowerProductionStack product) {
		return new PowerProductionEntry(product);
	}

	@Override
	public String getSerializedProduct(PowerProductionStack product) {
		return product.serialize().toString();
	}

	@Override
	public boolean isValidProduct(PowerProductionStack product) {
		return product.getBlockSource().asItem() != Items.AIR;
	}

	@Override
	public PowerProductionStack deserializeProduct(String serializedProduct) {
		try {
			CompoundTag tag = TagParser.parseTag(serializedProduct);
			return PowerProductionStack.deserialize(tag);
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to deserialize the serialized string: %1$s to a PowerProductionStack.", serializedProduct));
		}
	}
}
