package theking530.staticpower.machines.heatingelement;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityHeatingElement extends BaseMachine implements ITickable, IHeatProvider {

	public HeatStorage heatStorage;
	public HeatDistributor heatDistributor;
	
	public TileEntityHeatingElement() {
		initializeSlots(0, 0, 0);
		initializeBasicMachine(1, 5, 100, 100, 20);
		heatStorage = new HeatStorage(1);
		heatDistributor = new HeatDistributor(this, heatStorage);
	}
	@Override
	public void process(){
		if(!getWorld().isRemote && energyStorage.getEnergyStored() > getProcessingCost()) {
			if(processingTimer >= processingTime) {
				if(heatStorage.getHeat() < heatStorage.getMaxHeat() && energyStorage.getEnergyStored() > getProcessingCost()) {
					heatStorage.recieveHeat(1);
					energyStorage.extractEnergy(getProcessingCost(), false);
				}	
				processingTimer = 0;
				updateBlock();
			}else{
				processingTimer++;
				energyStorage.extractEnergy(getProcessingCost(), false);
			}
			heatDistributor.provideHeat();	
		}
	}
    @Override  
	public void deserializeData(NBTTagCompound nbt) {
        super.deserializeData(nbt);
        heatStorage.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound serializeData(NBTTagCompound nbt) {
        super.serializeData(nbt);
        heatStorage.writeToNBT(nbt);
        return nbt;
	}	
	public void deserializeOnPlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)  {
		super.deserializeOnPlaced(nbt, world, pos, state, placer, stack);
		heatStorage.readFromNBT(nbt);
	}
	@Override
	public boolean isSideConfigurable() {
		return false;
	}
}
