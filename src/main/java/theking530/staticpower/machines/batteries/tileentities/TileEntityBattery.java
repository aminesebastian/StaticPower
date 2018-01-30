package theking530.staticpower.machines.batteries.tileentities;

import net.minecraft.util.EnumFacing;
import theking530.staticpower.assists.utilities.RedstoneModeList.RedstoneMode;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.energy.PowerDistributor;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityBattery extends BaseMachine{
	
	public int MAX_INPUT;
	public int MAX_OUTPUT;
	protected PowerDistributor POWER_DIS;
	
	public TileEntityBattery() {
		POWER_DIS = new PowerDistributor(this, energyStorage);		
	}
	
	public void process() {
		//getWorld().markBlockForUpdate(xCoord, yCoord, zCoord);
		int redstoneSignal = getWorld().getStrongPower(pos);
		powerTab();
		if(!getWorld().isRemote) {
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
			updateBlock();
		}
	}
	@Override
	public void upgradeHandler() {
		
	}
	  
	//Tab Integration
	public void powerTab() {	
		if(getEnergyLevelScaled(1)*100 > this.minPowerThreshold && getEnergyLevelScaled(1)*100 < this.maxPowerThreshold) {
			//getWorld().setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 15, 3);
		}else{
			//getWorld().setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
		}
	}
	@Override
	public String getName() {
		return "Battery";	
	}
	@Override
	public void onSidesConfigUpdate() {

	}
	
	//Energy
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if(getSideConfiguration(from) == Mode.Disabled) {
			return false;
		}else{
			return true;
		}
	}
	public float getEnergyLevelScaled(int i) {
		float amount = energyStorage.getEnergyStored();
		float percentFilled = (amount/energyStorage.getMaxEnergyStored());	
		return percentFilled * (float)i;
	}	
}


	
