package theking530.staticpower.fluids;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;

public class StaticTankStorage extends FluidTank {

    public StaticTankStorage(String name, int capacity) {
    	super(null, capacity);
    }
	public StaticTankStorage(String name, Fluid fluid, int amount, int capacity) {
		super(fluid, amount, capacity);
	}

}
