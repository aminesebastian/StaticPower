package theking530.staticpower.conduits.staticconduit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.conduits.TileEntityBaseConduit;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityStaticConduit extends TileEntityBaseConduit implements IEnergyHandler, IEnergyProvider, IEnergyReceiver {
	
	public int ENERGY_CAPACTIY = 6000;
	public EnergyStorage STORAGE = new EnergyStorage(ENERGY_CAPACTIY);
	public int RF_PER_TICK = 1000;
	public PowerDistributor POWER_DIST;
	public EnumFacing LAST_RECIEVED;

	public int TICK_FOR_DEBUG;
	
	public TileEntityStaticConduit() {
		STORAGE.setMaxExtract(RF_PER_TICK);
		STORAGE.setMaxReceive(RF_PER_TICK);
		STORAGE.setMaxTransfer(RF_PER_TICK);
		POWER_DIST = new PowerDistributor(this, STORAGE);
	}

	@Override
	public void update() {
		super.update();
		if(!getWorld().isRemote) {
			testDebugPower();
			//System.out.println(GRID.GatherPath(new BlockPos(-750,  237, 703), new BlockPos(-756,  237, 708)));
		}
	}	
	public void testDebugPower() {
		int recieverCount = 0;
		List<BlockPos> recievers = new ArrayList<BlockPos>();
		
	    Iterator<Entry<BlockPos, TileEntity>> it = GRID.ENERGY_STORAGE_MAP.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<BlockPos, TileEntity> pair = (Map.Entry<BlockPos, TileEntity>)it.next();
	        if(getWorld().getTileEntity(pair.getKey()) != null) {
		        IEnergyStorage storage = getWorld().getTileEntity(pair.getKey()).getCapability(CapabilityEnergy.ENERGY, null);
		        if(storage.canReceive() && storage.getEnergyStored() < storage.getMaxEnergyStored()) {
		        	recieverCount++;
		        	recievers.add(pair.getKey());
		        }   	
	        }   
	    }   

	    if(recieverCount > 0) {
	    	for(int i=0; i<recievers.size(); i++) {
		        List<BlockPos> path = GRID.GatherPath(getPos(), recievers.get(i));	
		        if(path != null) {
		        	if(path.size() > 1) { //GET PROPER SIDE
		        		//System.out.println(path);
		        		int powerMult = 1;
		        		if(getWorld().getTileEntity(path.get(1)) instanceof TileEntityStaticConduit) {
		        			powerMult = 6;
		        		}
				        int provided = POWER_DIST.provideRF(path.get(1), EnumFacing.UP, Math.min(STORAGE.getEnergyStored(), powerMult * RF_PER_TICK/recieverCount));	
				        //System.out.println("Sent " + provided + " Power to: " + path.get(1).toString() + " which is a: " + world.getTileEntity(path.get(1)));
		        	}
		        }
	    	}
	    }
	}
	public void distributePower() {
		int provided = 0;
		int count = 0;
		
		for(int i=0; i<6; i++) {
			if(LAST_RECIEVED != null && LAST_RECIEVED.ordinal() == i) {
				continue;
			}
			if(getWorld().getTileEntity(getPos().offset(EnumFacing.values()[i])) != null) {
				net.minecraftforge.energy.IEnergyStorage adjacentStorage = POWER_DIST.getEnergyHandlerPosition(EnumFacing.values()[i]);
				if(adjacentStorage != null) {
					if(adjacentStorage.getEnergyStored() < adjacentStorage.getMaxEnergyStored()) {
						count++;
					}
				}
			}
		}

		if(count <= 0) {
			return;
		}
		
		for(int i=0; i<6; i++) {
			if(LAST_RECIEVED != null && LAST_RECIEVED.ordinal() == i) {
				continue;
			}
			if(STORAGE.getEnergyStored() > 0) {
				TileEntity adjacentTileEntity = getWorld().getTileEntity(pos.offset(EnumFacing.values()[i]));
				if(adjacentTileEntity instanceof TileEntityStaticConduit) {
					net.minecraftforge.energy.IEnergyStorage adjacentStorage = POWER_DIST.getEnergyHandlerPosition(EnumFacing.values()[i]);
					if(adjacentStorage != null) {
						provided = POWER_DIST.provideRF(EnumFacing.values()[i], Math.min(STORAGE.getEnergyStored(), RF_PER_TICK));				
					}	
				}else{
					provided = POWER_DIST.provideRF(EnumFacing.values()[i], Math.min(STORAGE.getEnergyStored(), RF_PER_TICK));					
				}
			}
		}	
		if(LAST_RECIEVED != null) {
			if(provided < RF_PER_TICK) {
				POWER_DIST.provideRF(LAST_RECIEVED, Math.min(STORAGE.getEnergyStored(), RF_PER_TICK-provided));	
			}
		}
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
	public boolean isConduit(EnumFacing side) {
		if(SIDE_MODES[side.ordinal()] == 1) {
			return false;
		}
		if(getWorld().getTileEntity(pos.offset(side)) instanceof TileEntityStaticConduit) {
			return ((TileEntityStaticConduit)getWorld().getTileEntity(pos.offset(side))).SIDE_MODES[side.getOpposite().ordinal()] == 0;
		}
		return false;
	}		
	@Override
	public boolean isReciever(EnumFacing side) {
		if(SIDE_MODES[side.ordinal()] == 1) {
			return false;
		}
		if(getWorld().getTileEntity(pos.offset(side)) != null && !(getWorld().getTileEntity(pos.offset(side)) instanceof TileEntityStaticConduit) && getWorld().getTileEntity(pos.offset(side)).hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
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
			LAST_RECIEVED = from;
			return STORAGE.receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}
	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {
		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, from);
	}
	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {
		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {
					LAST_RECIEVED = from;
					return TileEntityStaticConduit.this.receiveEnergy(from, maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return TileEntityStaticConduit.this.extractEnergy(from, maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {

					return TileEntityStaticConduit.this.getEnergyStored(from);
				}

				@Override
				public int getMaxEnergyStored() {

					return TileEntityStaticConduit.this.getMaxEnergyStored(from);
				}

				@Override
				public boolean canExtract() {
					return SIDE_MODES[from.ordinal()] == 0;
				}

				@Override
				public boolean canReceive() {
					return SIDE_MODES[from.ordinal()] == 0;
				}
			});
		}
		return super.getCapability(capability, from);
	}
}
	

					
				
	


