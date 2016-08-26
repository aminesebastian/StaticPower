package theking530.staticpower.tileentity.gates.powercell;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.tileentity.gates.TileEntityBaseLogicGate;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityPowerCell extends TileEntityBaseLogicGate {
	
	public int POWER = 0;
	
	public boolean isOn(){
		return POWER > 0;
	}
	@Override
	public void gateTick() {
		if(!worldObj.isRemote) {
			if(addAllInputSignals() <= 0) {
				setAllOutputs(POWER);		
			}
		}
	}	
	public int maxInputs(){
		return 3;
	}
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        POWER = nbt.getInteger("POWER");
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("POWER", POWER);
		return nbt;
	}	
	public String getName() {
		return "container.PowerCell";		
	}
	public Mode[] getInitialModes(){
		return new Mode[]{Mode.Disabled, Mode.Disabled, Mode.Output, Mode.Output, Mode.Output, Mode.Output};
	}
}
