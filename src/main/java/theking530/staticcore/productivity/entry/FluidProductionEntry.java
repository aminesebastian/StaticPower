package theking530.staticcore.productivity.entry;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.utilities.FluidUtilities;

public class FluidProductionEntry extends ProductionEntry<FluidStack> {

	public FluidProductionEntry(FluidStack product) {
		super(product);
	}

	@Override
	public int getProductHashCode() {
		return FluidUtilities.getFluidStackHash(product);
	}

	@Override
	public String getSerializedProduct() {
		CompoundTag serialized = new CompoundTag();
		product.writeToNBT(serialized);
		serialized.remove("Amount");
		return serialized.getAsString();
	}
}
