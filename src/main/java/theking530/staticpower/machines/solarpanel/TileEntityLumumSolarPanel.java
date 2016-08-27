package theking530.staticpower.machines.solarpanel;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.power.PowerDistributor;
import theking530.staticpower.power.StaticEnergyStorage;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityLumumSolarPanel extends TileEntityBasicSolarPanel implements IEnergyHandler, IEnergyProvider {
	
	public void initializeSolarPanel() {
		STORAGE = new StaticEnergyStorage(1024);
		STORAGE.setMaxTransfer(160);
		STORAGE.setMaxReceive(160);
		STORAGE.setMaxExtract(160*2);
	}	
}
