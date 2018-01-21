package theking530.staticpower.tileentity.solarpanels;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import theking530.staticpower.energy.StaticEnergyStorage;

public class TileEntityStaticSolarPanel extends TileEntityBasicSolarPanel implements IEnergyHandler, IEnergyProvider {
	
	public void initializeSolarPanel() {
		STORAGE = new StaticEnergyStorage(128);
		STORAGE.setMaxTransfer(20);
		STORAGE.setMaxReceive(20);
		STORAGE.setMaxExtract(20*2);
	}	
}
