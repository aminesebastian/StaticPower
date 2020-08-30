package theking530.staticpower.fluid;

import java.util.function.Supplier;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

public class StaticPowerFluidBlock extends FlowingFluidBlock {

	public StaticPowerFluidBlock(String name, Supplier<FlowingFluid> fluid, Properties properties) {
		super(fluid, properties.doesNotBlockMovement().noDrops());
		setRegistryName(name);
	}
}
