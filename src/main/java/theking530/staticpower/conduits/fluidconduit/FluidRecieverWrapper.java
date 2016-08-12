package theking530.staticpower.conduits.fluidconduit;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidRecieverWrapper {
	
	private TileEntity TE;
	private IFluidHandler HANDLER;
	
	public FluidRecieverWrapper(TileEntity te, IFluidHandler handler) {
		TE = te;
		HANDLER = handler;
	}
	
	public void update() {

	}
	public TileEntity getTileEntity() {
		return TE;
	}
}
