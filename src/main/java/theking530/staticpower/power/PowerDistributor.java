package theking530.staticpower.power;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.machines.BaseTileEntity;
import theking530.staticpower.utils.SideModeList.Mode;

public class PowerDistributor {

	private TileEntity TE;
	private EnergyStorage E_STORAGE;
	
	public PowerDistributor(TileEntity tileEntity, EnergyStorage energyStorage) {
		TE = tileEntity;
		E_STORAGE = energyStorage;
	}
	
	public void distributePower() {
		if(E_STORAGE != null && TE != null) { 
			if(E_STORAGE.getEnergyStored() > 0) {
				for(int i=0; i<6; i++) {
					EnumFacing facing = EnumFacing.values()[i];
					if(TE instanceof BaseTileEntity) {
						BaseTileEntity tempTe = (BaseTileEntity)TE;
						if(tempTe.getModeFromInt(i) == Mode.Output || tempTe.getModeFromInt(i) == Mode.Regular) {
							if(E_STORAGE.getEnergyStored() > E_STORAGE.getMaxExtract()) {
								provideRF(facing, E_STORAGE.getMaxExtract());
							}else{
								provideRF(facing, E_STORAGE.getEnergyStored());
							}
						}
					}else{
						if(E_STORAGE.getEnergyStored() > E_STORAGE.getMaxExtract()) {
							provideRF(facing, E_STORAGE.getMaxExtract());
						}else{
							provideRF(facing, E_STORAGE.getEnergyStored());
						}
					}
				}	
			}
		}
	}
	public void provideRF(EnumFacing facing, int amount) {
		if(getEnergyHandlerPosition(TE.getPos().offset(facing)) != null) {
			IEnergyReceiver handler = getEnergyHandlerPosition(TE.getPos().offset(facing));
			EnumFacing opposite = facing.getOpposite();
			if(handler.getEnergyStored(opposite) < handler.getMaxEnergyStored(opposite) && handler.canConnectEnergy(opposite)) {
				int provided = handler.receiveEnergy(opposite, amount, false);		
				E_STORAGE.extractEnergy(provided, false);
			}
		}
	}	
    public EnumFacing getOpp(int x, int y, int z) {
		if(x == 0 && y == -1 && z == 0) {
			return EnumFacing.UP;
		}
		if(x == 0 && y == 1 && z == 0) {
			return EnumFacing.DOWN;
		}
		if(x == -1 && y == 0 && z == 0) {
			return EnumFacing.EAST;
		}
		if(x == 1 && y == 0 && z == 0) {
			return EnumFacing.WEST;
		}
		if(x == 0 && y == 0 && z == 1) {
			return EnumFacing.NORTH;
		}
		if(x == 0 && y == 0 && z == -1) {
			return EnumFacing.SOUTH;
		}
		return null;
	}
	public IEnergyReceiver getEnergyHandlerPosition(BlockPos pos) {
		if (TE.getWorld().getTileEntity(pos) instanceof IEnergyReceiver) {					
			TileEntity te = TE.getWorld().getTileEntity(pos);
			if(te instanceof IEnergyReceiver) {
				IEnergyReceiver handler = (IEnergyReceiver)te;
				return handler;
			}
			return null;
		}
		return null;
	}
}
