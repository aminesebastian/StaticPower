package theking530.staticpower.machines.esotericenchanter;

import net.minecraft.item.ItemStack;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.BucketInteractionComponent;

public class TileEsotericEnchanter extends BaseMachineWithTank {

	private float initialEnergyMult;
	private int initialPowerUse;
	private int initialEnergyCapacity;
	private int initialEnergyPerTick;
	private int initialProcessingTime;
	
	private BucketInteractionComponent bucketInteractionComponent;
	
	public TileEsotericEnchanter() {
		this.initializeBasicMachine(initialEnergyMult, initialPowerUse, initialEnergyCapacity, initialEnergyPerTick, initialProcessingTime);
		this.initializeSlots(4, 3, 1);
		this.initializeTank(50000);
		
		this.registerComponent(new BatteryInteractionComponent("Battery Component", slotsInternal, 0, this, getEnergyStorage()));
		
		bucketInteractionComponent = new BucketInteractionComponent("Bucket Component", slotsInternal, 1, slotsInternal, 2, this, fluidTank, 40);
		this.registerComponent(bucketInteractionComponent);
	}
	
	@Override
	public void process() {
		
	}
	@Override
	public ItemStack getResult(ItemStack itemstack) {
		return null;	
	}	
	@Override
	public boolean canProcess(ItemStack itemstack) {
		return false;
	}
}
