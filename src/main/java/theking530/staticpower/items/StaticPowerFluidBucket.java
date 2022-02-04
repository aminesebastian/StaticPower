package theking530.staticpower.items;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import theking530.staticpower.StaticPower;

public class StaticPowerFluidBucket extends BucketItem {

	public StaticPowerFluidBucket(String name, Supplier<? extends Fluid> supplier) {
		super(supplier, new Properties().stacksTo(1).tab(StaticPower.CREATIVE_TAB));
		setRegistryName(name);
	}

	@Override
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
		FluidBucketWrapper output = new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
		output.fill(new FluidStack(getFluid(), FluidAttributes.BUCKET_VOLUME), FluidAction.EXECUTE);
		return output;
	}
}
