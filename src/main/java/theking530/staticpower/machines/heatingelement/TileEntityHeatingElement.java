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
			if(processingTimer >= processingTime) {
				if(HEAT_STORAGE.getHeat() < HEAT_STORAGE.getMaxHeat() && energyStorage.getEnergyStored() > getProcessingCost()) {
					HEAT_STORAGE.recieveHeat(1);
					energyStorage.extractEnergy(getProcessingCost(), false);
				}
				HEAT_DIST.provideHeat();		
				processingTimer = 0;
				
				markDirty();
			}else{
				processingTimer++;
				energyStorage.extractEnergy(getProcessingCost(), false);
			}	
		}
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
	public void onMachinePlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)  {
		super.onMachinePlaced(nbt, world, pos, state, placer, stack);
		HEAT_STORAGE.readFromNBT(nbt);
	}
}
