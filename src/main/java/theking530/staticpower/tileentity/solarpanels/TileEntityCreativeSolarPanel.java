package theking530.staticpower.tileentity.solarpanels;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.power.StaticEnergyStorage;

public class TileEntityCreativeSolarPanel extends TileEntityBasicSolarPanel implements IEnergyHandler, IEnergyProvider {
	
	public void initializeSolarPanel() {
		STORAGE = new StaticEnergyStorage(2048);
		STORAGE.setMaxTransfer(2000);
		STORAGE.setMaxReceive(2000);
		STORAGE.setMaxExtract(2000*2);
	}	
	public void updateEntity() {
		generateRF();
		POWER_DIST.distributePower();
	}	
	//Functionality
	public void generateRF() {
		STORAGE.receiveEnergy(STORAGE.getMaxReceive(), false);
	}
	public boolean isGenerating() {
		return true;
	}
	@Override
	public int getEnergyStored(EnumFacing from) {
		return STORAGE.getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return STORAGE.getMaxEnergyStored();
	}
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(from == EnumFacing.DOWN) {
			return STORAGE.extractEnergy(maxExtract, simulate);
		}
		return 0;
	}
}
