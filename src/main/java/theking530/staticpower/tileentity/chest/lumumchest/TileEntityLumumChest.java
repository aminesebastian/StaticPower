package theking530.staticpower.tileentity.chest.lumumchest;

import net.minecraft.nbt.NBTTagCompound;
import theking530.staticpower.tileentity.chest.TileEntityBaseChest;

public class TileEntityLumumChest extends TileEntityBaseChest{

	public TileEntityLumumChest() {
		initializeSlots(0, 0, 96);
	}
	//NBT
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return nbt;
	}	  	    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            this.numPlayersUsing = j;
            return true;
        }else{
            return super.receiveClientEvent(i, j);
        }
    }

    //IInventory
	@Override
	public String getName() {
		return "container.LumumChest";
		
	}					
}
