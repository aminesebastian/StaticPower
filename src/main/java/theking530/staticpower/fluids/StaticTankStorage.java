package theking530.staticpower.fluids;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;

public class StaticTankStorage extends FluidTank {

	private String TANK_NAME;
	
    public StaticTankStorage(String name, int capacity) {
    	super(null, capacity);
    	TANK_NAME = name;
    }
	public StaticTankStorage(String name, Fluid fluid, int amount, int capacity) {
		super(fluid, amount, capacity);
		TANK_NAME = name;
	}

}
