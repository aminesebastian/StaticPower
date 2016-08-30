package theking530.staticpower.machines.heatingelement;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class HeatDistributor {

	public TileEntity TE;
	public HeatStorage STORAGE;
	
	public HeatDistributor(TileEntity te, HeatStorage storage) {
		TE = te;
		STORAGE = storage;
	}
 	public int provideHeat() {
		for(int i=0; i<6; i++) {
			if(STORAGE.getHeat() > 0) {
				if(TE.getWorld().getTileEntity(TE.getPos().offset(EnumFacing.values()[i])) instanceof IHeatable) {
					IHeatable heatable = (IHeatable) TE.getWorld().getTileEntity(TE.getPos().offset(EnumFacing.values()[i]));
					if(heatable.canHeat() && heatable.getHeat() < heatable.getMaxHeat()) {
						return STORAGE.extractHeat(heatable.recieveHeat(Math.min(10, STORAGE.getHeat())));
					}
				}	
			}
		}
		return 0;	
	}
}
