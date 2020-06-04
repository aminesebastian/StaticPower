package theking530.staticpower.items;

import java.util.function.Supplier;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import theking530.staticpower.StaticPower;

public class StaticPowerFluidBucket extends BucketItem {

	public StaticPowerFluidBucket(String name, Supplier<? extends Fluid> supplier) {
		super(supplier, new Properties().maxStackSize(1).group(StaticPower.CREATIVE_TAB));
		setRegistryName(name);
	}
}
