package theking530.staticpower.tileentity.gates.notgate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.tileentity.gates.TileEntityBaseLogicGate;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityNotGate extends TileEntityBaseLogicGate {
	
	public boolean isOn(){
		return getInputSignal(getInputSide()) == 0;
	}
	@Override
	public void gateTick() {
		if(!worldObj.isRemote) {
			if(getInputSignal(getInputSide()) == 0) {
				setAllOutputs(15);
			}else{
				reset();
				setAllOutputs(0);
			}
			updateGate();
		}
	}	
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		return nbt;
	}	
}
