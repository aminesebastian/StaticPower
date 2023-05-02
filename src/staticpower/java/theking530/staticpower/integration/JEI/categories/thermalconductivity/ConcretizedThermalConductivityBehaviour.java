package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours;

public record ConcretizedThermalConductivityBehaviour(ItemStack block, FluidStack fluid, StaticPowerOutputItem item) {
	public static ConcretizedThermalConductivityBehaviour from(ThermalConductivityBehaviours behaviour) {
		ItemStack block = ItemStack.EMPTY;
		FluidStack fluid = FluidStack.EMPTY;
		StaticPowerOutputItem item = StaticPowerOutputItem.EMPTY;

		if (behaviour.hasBlock()) {
			if (behaviour.getBlockState().getFluidState().getType() != Fluids.EMPTY) {
				fluid = new FluidStack(behaviour.getBlockState().getFluidState().getType(), 1000);
			} else {
				block = new ItemStack(behaviour.getBlockState().getBlock());
			}
		}

		if (behaviour.hasItem()) {
			item = behaviour.getItem().copy();
		}

		return new ConcretizedThermalConductivityBehaviour(block, fluid, item);
	}
}