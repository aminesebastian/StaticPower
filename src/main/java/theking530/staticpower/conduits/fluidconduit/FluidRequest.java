package theking530.staticpower.conduits.fluidconduit;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

public class FluidRequest {

	FluidStack FLUID;
	TileEntity TILE_ENTITY;
	
	public FluidRequest(TileEntity tileentity, FluidStack fluid) {
		FLUID = fluid;
		TILE_ENTITY = tileentity;
	}
}
