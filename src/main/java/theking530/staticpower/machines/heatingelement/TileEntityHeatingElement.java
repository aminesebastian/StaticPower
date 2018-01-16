package theking530.staticpower.machines.heatingelement;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityHeatingElement extends BaseMachine implements ITickable, IHeatProvider {

	public HeatStorage HEAT_STORAGE;
	public HeatDistributor HEAT_DIST;
	
	public TileEntityHeatingElement() {
		initializeBasicMachine(1, 5, 100, 100, 20, 0, 0, 0);
		HEAT_STORAGE = new HeatStorage(200);
		HEAT_DIST = new HeatDistributor(this, HEAT_STORAGE);
	}
	@Override
	public void process(){
		if(!getWorld().isRemote) {
			if(PROCESSING_TIMER >= PROCESSING_TIME) {
				if(HEAT_STORAGE.getHeat() < HEAT_STORAGE.getMaxHeat() && STORAGE.getEnergyStored() > getProcessingCost()) {
					HEAT_STORAGE.recieveHeat(1);
					STORAGE.extractEnergy(getProcessingCost(), false);
				}
				HEAT_DIST.provideHeat();		
				PROCESSING_TIMER = 0;
				
				markDirty();
			}else{
				PROCESSING_TIMER++;
				STORAGE.extractEnergy(getProcessingCost(), false);
			}	
		}
	}
	@Override
	public void readFromSyncNBT(NBTTagCompound nbt) {
		super.readFromSyncNBT(nbt);
        HEAT_STORAGE.readFromNBT(nbt);
	}
	@Override
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
        HEAT_STORAGE.writeToNBT(nbt);
		return nbt;
	}
	
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        HEAT_STORAGE.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        HEAT_STORAGE.writeToNBT(nbt);
        return nbt;
	}	
	public void onMachinePlaced(NBTTagCompound nbt) {
		super.onMachinePlaced(nbt);
		HEAT_STORAGE.readFromNBT(nbt);
	}
}
