package theking530.staticpower.logicgates.adder;

import net.minecraft.nbt.NBTTagCompound;
import theking530.staticpower.logicgates.TileEntityBaseLogicGate;
import theking530.staticpower.utilities.SideModeList.Mode;

public class TileEntityAdder extends TileEntityBaseLogicGate {
	
	public boolean isOn(){
		return addAllInputSignals() > 0;
	}
	@Override
	public void gateTick() {
		if(!getWorld().isRemote) {
			int sum = addAllInputSignals();
			setAllOutputs(Math.min(sum, 15));
			if(sum > 15) {
				setAllExtraOutputs(Math.min(sum-15, 15));
			}
			updateGate();
		}
	}	
	public int maxInputs(){
		return 3;
	}
	public int maxExtra(){
		return 3;
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
		return new Mode[]{Mode.Disabled, Mode.Disabled, Mode.Input, Mode.Output, Mode.Input, Mode.Regular};
	}
}
