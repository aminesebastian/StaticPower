	package theking530.staticpower.tileentity.solarpanels;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import theking530.staticpower.energy.StaticEnergyStorage;

public class TileEntityLumumSolarPanel extends TileEntityBasicSolarPanel implements IEnergyHandler, IEnergyProvider {
	
	public void initializeSolarPanel() {
		STORAGE = new StaticEnergyStorage(1024);
		STORAGE.setMaxTransfer(160);
		STORAGE.setMaxReceive(160);
		STORAGE.setMaxExtract(160*2);
	}	
}
