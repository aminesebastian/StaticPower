package theking530.staticpower.conduits.staticconduit;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.conduits.TileEntityBaseConduit;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityStaticConduit extends TileEntityBaseConduit implements IEnergyHandler, IEnergyProvider, IEnergyReceiver{
	
	public int ENERGY_CAPACTIY = 1280;
	public EnergyStorage STORAGE = new EnergyStorage(ENERGY_CAPACTIY);
	public int RF_PER_TICK = 100;
	public PowerDistributor POWER_DIST;
	public EnumFacing LAST_RECIEVED;
	private boolean needsUpdate = false;
	
	public TileEntityStaticConduit() {
		STORAGE.setMaxExtract(RF_PER_TICK);
		STORAGE.setMaxReceive(RF_PER_TICK);
		STORAGE.setMaxTransfer(RF_PER_TICK);
		POWER_DIST = new PowerDistributor(this, STORAGE);
	}
	
	public void updateEntity() {
		worldObj.markAndNotifyBlock(pos, worldObj.getChunkFromBlockCoords(pos), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
		updateConduitRenderConnections();
		updateRecieverRenderConnections();
		for(int i=0; i<6; i++) {
			if(STORAGE.getEnergyStored() >= STORAGE.getMaxExtract()) {
				if(LAST_RECIEVED == null || LAST_RECIEVED.ordinal() != i) {
					POWER_DIST.provideRF(EnumFacing.values()[i], STORAGE.getMaxExtract());
				}			
			}
		}
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}	
		
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        STORAGE.readFromNBT(nbt);
		for(int i=0; i<6; i++) {
			SIDE_MODES[i] = nbt.getInteger("SIDE"+i);
		}
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        STORAGE.writeToNBT(nbt);
		for(int i=0; i<6; i++) {
			nbt.setInteger("SIDE"+i, SIDE_MODES[i]);
		}
		return nbt;
	}

	@Override
	public boolean isReciever(BlockPos pos) {
		TileEntity te = worldObj.getTileEntity(pos);
		if(te instanceof IEnergyReceiver) {
			if(te instanceof TileEntityStaticConduit) {
				return false;
			}
			return true;
		}
		return false;
	}	
	public float getEnergyAdjusted() {
		int amount = STORAGE.getEnergyStored();
		int max = ENERGY_CAPACTIY;
	
		return (float)amount/(float)max;
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if(disconected(from) || canPull(from)) {
			return false;
		}
		return true;
	}
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return STORAGE.extractEnergy(maxExtract, simulate);
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
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if(!disconected(from) && !canPull(from)) {
			return STORAGE.receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}
}
	

					
				
	


