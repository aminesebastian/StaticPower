package theking530.staticpower.tileentity.solarpanels;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.power.PowerDistributor;
import theking530.staticpower.power.StaticEnergyStorage;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityEnergizedSolarPanel extends TileEntityBasicSolarPanel implements IEnergyHandler, IEnergyProvider {
	
	public void initializeSolarPanel() {
		STORAGE = new StaticEnergyStorage(512);
		STORAGE.setMaxTransfer(80);
		STORAGE.setMaxReceive(80);
		STORAGE.setMaxExtract(80*2);
	}
}
