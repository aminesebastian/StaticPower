package theking530.staticcore.productivity.product.fluid;

import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.cacheentry.FluidProductionEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.utilities.FluidUtilities;

public class FluidStackProductType extends ProductType<FluidStack> {

	public FluidStackProductType() {
		super(FluidStack.class, (isClientSide) -> new ProductionCache<FluidStack>(ModProducts.Fluid.get(), isClientSide));
	}

	@Override
	public String getUnlocalizedName() {
		return "gui.staticpower.item";
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
}
