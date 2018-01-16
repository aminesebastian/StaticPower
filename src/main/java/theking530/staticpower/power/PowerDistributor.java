package theking530.staticpower.power;

import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.utils.SideModeList.Mode;

public class PowerDistributor {

	private TileEntity TE;
	private EnergyStorage E_STORAGE;
	private Mode[] SIDE_SETTINGS;
	
	public PowerDistributor(TileEntity tileEntity, EnergyStorage energyStorage, Mode... modes) {
		TE = tileEntity;
		E_STORAGE = energyStorage;
		SIDE_SETTINGS = modes;
	}
	public void updateSideSettings(Mode... modes) {
		SIDE_SETTINGS = modes;
	}
	public void distributePower() {
		if(E_STORAGE != null && TE != null) { 
			if(!TE.getWorld().isRemote) {
				if(E_STORAGE.getEnergyStored() > 0) {
					for(int i=0; i<6; i++) {
						EnumFacing facing = EnumFacing.values()[i];
						if(SIDE_SETTINGS != null && SIDE_SETTINGS.length > i) {
							if(SIDE_SETTINGS[i] == Mode.Output || SIDE_SETTINGS[i] == Mode.Regular) {
								provideRF(facing, Math.min(E_STORAGE.getMaxExtract(), E_STORAGE.getEnergyStored()));
							}
						}
					}	
				}	
			}
		}
	}
	public void provideRF(EnumFacing facing, int amount) {
		if(getEnergyHandlerPosition(facing) != null) {
			IEnergyStorage handler = getEnergyHandlerPosition(facing);
			if(handler.getEnergyStored() < handler.getMaxEnergyStored() && handler.canReceive()) {
				int provided = handler.receiveEnergy(amount, false);
				E_STORAGE.extractEnergy(provided, false);
			}
		}
	}	
	public IEnergyStorage getEnergyHandlerPosition(EnumFacing facing) {
		if (TE.getWorld().getTileEntity(TE.getPos().offset(facing)) != null && TE.getWorld().getTileEntity(TE.getPos().offset(facing)).hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {					
			TileEntity te = TE.getWorld().getTileEntity(TE.getPos().offset(facing));
			IEnergyStorage  handler = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
			return handler;
		}
		return null;
	}
}
