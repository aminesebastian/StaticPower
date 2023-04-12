package theking530.staticcore.productivity.product.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.cacheentry.FluidProductionEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.FluidUtilities;

public class FluidStackProductType extends ProductType<FluidStack> {

	public FluidStackProductType() {
		super(FluidStack.class);
	}

	@Override
	public String getUnlocalizedName(int amount) {
		if (amount > 1) {
			return "gui.staticcore.product.fluids";
		}
		return "gui.staticcore.product.fluid";
	}

	@Override
	public ProductionTrackingToken<FluidStack> createProductivityToken() {
		return new ProductionTrackingToken<FluidStack>(this);
	}

	@Override
	public int getProductHashCode(FluidStack product) {
		return FluidUtilities.getFluidStackHash(product);
	}

	@Override
	public FluidProductionEntry createProductionEntry(FluidStack product) {
		return new FluidProductionEntry(product);
	}

	@Override
	public String getSerializedProduct(FluidStack product) {
		CompoundTag serialized = new CompoundTag();
		product.writeToNBT(serialized);
		serialized.putInt("Amount", (byte) 1);
		return serialized.getAsString();
	}

	@Override
	public boolean isValidProduct(FluidStack product) {
		return !product.isEmpty();
	}

	@Override
	public FluidStack deserializeProduct(String serializedProduct) {
		try {
			CompoundTag tag = TagParser.parseTag(serializedProduct);
			tag.putInt("Amount", (byte) 1);
			return FluidStack.loadFluidStackFromNBT(tag);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"An error occured when attempting to deserialize the serialized string: %1$s to a FluidStack.",
					serializedProduct));
		}
	}
}
