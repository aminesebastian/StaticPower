package theking530.staticcore.fluid;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SidedFluidHandlerCapabilityWrapper {
	private final Map<Direction, SidedFluidHandlerCapability> sides;
	private final IFluidHandler owner;

	public SidedFluidHandlerCapabilityWrapper(ISidedFluidHandler proxy) {
		sides = new HashMap<>();
		this.owner = proxy;
		for (Direction dir : Direction.values()) {
			sides.put(dir, new SidedFluidHandlerCapability(dir, proxy));
		}
	}

	public IFluidHandler get(@Nullable Direction side) {
		if (side == null) {
			return owner;
		}
		return sides.get(side);
	}

	public class SidedFluidHandlerCapability implements IFluidHandler {
		private final Direction representedSide;
		private final ISidedFluidHandler proxy;

		public SidedFluidHandlerCapability(Direction representedSide, ISidedFluidHandler proxy) {
			this.representedSide = representedSide;
			this.proxy = proxy;
		}

		@Override
		public int getTanks() {
			return proxy.getTanks(representedSide);
		}

		@Override
		public FluidStack getFluidInTank(int tank) {
			return proxy.getFluidInTank(representedSide, tank);
		}

		@Override
		public int getTankCapacity(int tank) {
			return proxy.getTankCapacity(representedSide, tank);
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack) {
			return proxy.isFluidValid(representedSide, tank, stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return proxy.fill(representedSide, resource, action);
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			return proxy.drain(representedSide, resource, action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			return proxy.drain(representedSide, maxDrain, action);
		}
	}
}
