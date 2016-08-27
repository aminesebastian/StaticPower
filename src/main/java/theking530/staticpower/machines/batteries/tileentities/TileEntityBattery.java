package theking530.staticpower.machines.batteries.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.power.PowerDistributor;
import theking530.staticpower.utils.RedstoneModeList.RedstoneMode;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityBattery extends BaseMachine{
	
	public int MAX_INPUT;
	public int MAX_OUTPUT;
	protected PowerDistributor POWER_DIS;
	
	public TileEntityBattery() {

	}
	
	public void process() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		int redstoneSignal = worldObj.getStrongPower(pos);
		powerTab();
		if(!worldObj.isRemote) {
			if(getRedstoneMode() == RedstoneMode.Ignore) {
				POWER_DIS.distributePower();
			}
			if(getRedstoneMode() == RedstoneMode.Low) {
				if(redstoneSignal == 0) {
					POWER_DIS.distributePower();
				}
			}
			if(getRedstoneMode() == RedstoneMode.High) {
				if(redstoneSignal > 0) {
					POWER_DIS.distributePower();
				}
			}
			sync();
		}
	}
	@Override
	public void upgradeHandler() {
		
	}
	  
	//Tab Integration
	public void powerTab() {	
		if(getEnergyLevelScaled(1)*100 > this.MIN_POWER_THRESHOLD && getEnergyLevelScaled(1)*100 < this.MAX_POWER_THRESHOLD) {
			//worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 15, 3);
		}else{
			//worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
		}
	}
	@Override
	public String getName() {
		return "Battery";	
	}
	
	//Energy
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if(getModeFromFacing(from) == Mode.Disabled) {
			return false;
		}else{
			return true;
		}
	}
	public float getEnergyLevelScaled(int i) {
		float amount = STORAGE.getEnergyStored();
		float percentFilled = (amount/STORAGE.getMaxEnergyStored());	
		return percentFilled * (float)i;
	}	
}


	
