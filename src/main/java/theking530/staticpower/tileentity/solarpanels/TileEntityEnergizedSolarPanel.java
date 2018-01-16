package theking530.staticpower.tileentity.solarpanels;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import theking530.staticpower.power.StaticEnergyStorage;

public class TileEntityEnergizedSolarPanel extends TileEntityBasicSolarPanel implements IEnergyHandler, IEnergyProvider {
	
	public void initializeSolarPanel() {
		STORAGE = new StaticEnergyStorage(512);
		STORAGE.setMaxTransfer(80);
		STORAGE.setMaxReceive(80);
		STORAGE.setMaxExtract(80*2);
	}
}
