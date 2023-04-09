package theking530.staticcore.productivity.product.power;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.Items;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.cacheentry.PowerProductionEntry;
import theking530.staticcore.productivity.product.ProductType;

public class PowerProductType extends ProductType<PowerProductInterfaceId> {

	public PowerProductType() {
		super(PowerProductInterfaceId.class, (isClientSide) -> new ProductionCache<PowerProductInterfaceId>(StaticCoreProductTypes.Power.get(), isClientSide));
	}

	@Override
	public String getUnlocalizedName(int amount) {
		return "gui.staticcore.product.power";
	}

	@Override
	public ProductionTrackingToken<PowerProductInterfaceId> createProductivityToken() {
		return new ProductionTrackingToken<PowerProductInterfaceId>(this);
	}

	@Override
	public int getProductHashCode(PowerProductInterfaceId product) {
		return product.getProductHashCode();
	}

	@Override
	public PowerProductionEntry createProductionEntry(PowerProductInterfaceId product) {
		return new PowerProductionEntry(product);
	}

	@Override
	public String getSerializedProduct(PowerProductInterfaceId product) {
		return product.serialize().toString();
	}

	@Override
	public boolean isValidProduct(PowerProductInterfaceId product) {
		return product.getBlockSource().asItem() != Items.AIR;
	}

	@Override
	public PowerProductInterfaceId deserializeProduct(String serializedProduct) {
		try {
			CompoundTag tag = TagParser.parseTag(serializedProduct);
			return PowerProductInterfaceId.deserialize(tag);
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to deserialize the serialized string: %1$s to a PowerProductionStack.", serializedProduct));
		}
	}
}
