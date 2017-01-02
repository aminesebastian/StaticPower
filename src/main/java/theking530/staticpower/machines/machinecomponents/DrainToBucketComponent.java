package theking530.staticpower.machines.machinecomponents;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBucket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class DrainToBucketComponent {

	ItemStackHandler EMPTY_BUCKET_HANDLER;
	int EMPTY_BUCKET_SLOT;
	ItemStackHandler FILLED_BUCKET_HANDLER;
	int FILLED_BUCKET_SLOT;
	FluidTank TANK;
	TileEntity TE;
	int FLUID_TO_CONTAINER_RATE;
	
	public DrainToBucketComponent(ItemStackHandler EmptyBucketHandler, int EmptyBucketSlot, ItemStackHandler FilledBucketHandler, int FilledBucketSlot,
			TileEntity tileEntity, FluidTank tank, int drainRate) {
		EMPTY_BUCKET_HANDLER = EmptyBucketHandler;
		EMPTY_BUCKET_SLOT = EmptyBucketSlot;
		FILLED_BUCKET_HANDLER = FilledBucketHandler;
		FILLED_BUCKET_SLOT = FilledBucketSlot;	 
		TANK = tank;
		TE = tileEntity;
		FLUID_TO_CONTAINER_RATE = drainRate;
	}
	public void drainToContainer() {
		ItemBucket Bucket;
	
		if(EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT) != null && EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) instanceof FluidHandlerItemStack) {
			FluidHandlerItemStack tempContainer = (FluidHandlerItemStack)EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if(TANK.getFluid() != null) {
				if(tempContainer.getFluid() == null || tempContainer.getFluid().isFluidEqual(TANK.getFluid())) {
					FluidStack drained = null;				
					if(!TE.getWorld().isRemote) {
						drained = TANK.drain(FLUID_TO_CONTAINER_RATE, false);	
						int filled = tempContainer.fill(drained, true);
						TANK.drain(filled, true);
					}
					if(drained != null && drained.amount > 0) {
						TE.getWorld().playSound(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.25f, 1, false);						
					}
				}
			}
		}	
		if(EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT) != null && EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) instanceof FluidHandlerItemStack) {
			FluidHandlerItemStack tempContainer = (FluidHandlerItemStack)EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if(tempContainer.getFluid() == null) {
				return;
			}
			if(TANK.getFluid() == null || tempContainer.getFluid().amount >= tempContainer.getTankProperties()[0].getCapacity()) {
				if(FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT) != null) {
					if(FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).isItemEqual(EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT))) {
						if(FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).stackSize < FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).getMaxStackSize()) {
							FILLED_BUCKET_HANDLER.insertItem(FILLED_BUCKET_SLOT, EMPTY_BUCKET_HANDLER.extractItem(EMPTY_BUCKET_SLOT, 1, false), false);
						}
					}
				}else{
					FILLED_BUCKET_HANDLER.insertItem(FILLED_BUCKET_SLOT, EMPTY_BUCKET_HANDLER.extractItem(EMPTY_BUCKET_SLOT, 1, false), false);
				}
			}
		}
	}
	
}
