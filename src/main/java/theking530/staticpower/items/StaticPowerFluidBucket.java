package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.init.ModGroups;

public class StaticPowerFluidBucket extends BucketItem {

	public StaticPowerFluidBucket(RegistryObject<? extends Fluid> supplier) {
		super(supplier, new Properties().stacksTo(1).tab(ModGroups.CREATIVE_TAB));
	}

	@Override
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
		FluidBucketWrapper output = new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
		output.fill(new FluidStack(getFluid(), 1000), FluidAction.EXECUTE);
		return output;
	}
}
