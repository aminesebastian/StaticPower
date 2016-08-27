package theking530.staticpower.tileentity.gates.subtractor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.tileentity.gates.TileEntityBaseLogicGate;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntitySubtractorGate extends TileEntityBaseLogicGate {
	
	public boolean isOn(){
		return addAllInputSignals() > 0;
	}
	@Override
	public void gateTick() {
		if(!worldObj.isRemote) {
			if(getInputCount() == 1 && getExtraCount() == 1) {
				int diff = addAllInputSignals() - addAllExtraSignals();
				setAllOutputs(Math.max(diff, 0));
			}
			updateGate();
		}
	}	
	public int maxInputs(){
		return 1;
	}
	public int maxExtra(){
		return 1;
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
	public Mode[] getInitialModes(){
		return new Mode[]{Mode.Disabled, Mode.Disabled, Mode.Input, Mode.Output, Mode.Regular, Mode.Output};
	}
}
