package theking530.staticpower.machines.machinecomponents;

import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import theking530.staticpower.tileentity.BaseTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class DrainToBucketComponent extends BaseComponent{

	private ItemStackHandler INITIAL_EMPTY_BUCKET_HANDLER;
	private int INITIAL_EMPTY_BUCKET_SLOT;
	private ItemStackHandler INITIAL_FILLED_BUCKET_HANDLER;
	private int INITIAL_FILLED_BUCKET_SLOT;
	
	private ItemStackHandler EMPTY_BUCKET_HANDLER;
	private int EMPTY_BUCKET_SLOT;
	private ItemStackHandler FILLED_BUCKET_HANDLER;
	private int FILLED_BUCKET_SLOT;
	
	private IFluidHandler FLUID_HANDLER;
	private BaseTileEntity TE;
	private int FLUID_TO_CONTAINER_RATE;
	private FluidContainerInteractionMode MODE = FluidContainerInteractionMode.DrainToContainer;

	private int MOVE_TIME = 0;
	private int MOVE_TIMER = 0;
	
	public enum FluidContainerInteractionMode {
		FillFromContainer, DrainToContainer;
	}

	public DrainToBucketComponent(String componentName, ItemStackHandler EmptyBucketHandler, int EmptyBucketSlot, ItemStackHandler FilledBucketHandler, int FilledBucketSlot,
			BaseTileEntity tileEntity, IFluidHandler fluidHandler, int drainRate) {
		super(componentName);
		
		INITIAL_EMPTY_BUCKET_HANDLER = EmptyBucketHandler;
		INITIAL_EMPTY_BUCKET_SLOT = EmptyBucketSlot;
		INITIAL_FILLED_BUCKET_HANDLER = FilledBucketHandler;
		INITIAL_FILLED_BUCKET_SLOT = FilledBucketSlot;	 
		
		EMPTY_BUCKET_HANDLER = EmptyBucketHandler;
		EMPTY_BUCKET_SLOT = EmptyBucketSlot;
		FILLED_BUCKET_HANDLER = FilledBucketHandler;
		FILLED_BUCKET_SLOT = FilledBucketSlot;	
		
		FLUID_HANDLER = fluidHandler;
		TE = tileEntity;
		FLUID_TO_CONTAINER_RATE = drainRate;
	}
	public void update() {
		if(MOVE_TIMER >= MOVE_TIME) {
			if(MODE == FluidContainerInteractionMode.FillFromContainer) {
				fillFromContainer();
			}else{
				drainToContainer();
			}
			MOVE_TIMER = 0;
		}else{
			MOVE_TIMER++;
		}
	}
	public void setMode(FluidContainerInteractionMode NewMode) {
		MODE = NewMode;
		if(MODE == FluidContainerInteractionMode.FillFromContainer) {
			FILLED_BUCKET_HANDLER = INITIAL_EMPTY_BUCKET_HANDLER;
			FILLED_BUCKET_SLOT = INITIAL_EMPTY_BUCKET_SLOT;
			EMPTY_BUCKET_HANDLER = INITIAL_FILLED_BUCKET_HANDLER;
			EMPTY_BUCKET_SLOT = INITIAL_FILLED_BUCKET_SLOT;
		}else{
			FILLED_BUCKET_HANDLER = INITIAL_FILLED_BUCKET_HANDLER;
			FILLED_BUCKET_SLOT = INITIAL_FILLED_BUCKET_SLOT;
			EMPTY_BUCKET_HANDLER = INITIAL_EMPTY_BUCKET_HANDLER;
			EMPTY_BUCKET_SLOT = INITIAL_EMPTY_BUCKET_SLOT;
		}
	}
	private void drainToContainer() {
		if(EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT) != null && EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) instanceof FluidHandlerItemStack) {
			FluidHandlerItemStack tempContainer = (FluidHandlerItemStack)EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			FluidStack testStack = FLUID_HANDLER.getTankProperties()[0].getContents();
			if(testStack != null) {
				if(tempContainer.getFluid() == null || tempContainer.getFluid().isFluidEqual(testStack)) {
					FluidStack drained = null;				
					if(!TE.getWorld().isRemote) {
						drained = FLUID_HANDLER.drain(FLUID_TO_CONTAINER_RATE, false);	
						int filled = tempContainer.fill(drained, true);
						FLUID_HANDLER.drain(filled, true);
						TE.updateBlock();
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
			FluidStack testStack = FLUID_HANDLER.drain(1, false);
			if(testStack == null || tempContainer.getFluid().amount >= tempContainer.getTankProperties()[0].getCapacity()) {
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
	private void fillFromContainer() {
		if(FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT) != null && FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) instanceof FluidHandlerItemStack) {
			FluidHandlerItemStack tempContainer = (FluidHandlerItemStack)FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if(tempContainer.getFluid() != null) {
				FluidStack testStack = FLUID_HANDLER.getTankProperties()[0].getContents();
				if(testStack == null || (testStack.amount < FLUID_HANDLER.getTankProperties()[0].getCapacity() && testStack.isFluidEqual(tempContainer.getFluid()))) {
					FluidStack drained = null;				
					if(!TE.getWorld().isRemote) {
						drained = tempContainer.drain(Math.min(FLUID_TO_CONTAINER_RATE, FLUID_HANDLER.getTankProperties()[0].getCapacity()), false);
						int filled = FLUID_HANDLER.fill(drained, true);
						tempContainer.drain(filled, true);		
						TE.updateBlock();
					}
					if(drained != null && drained.amount > 0) {
						TE.getWorld().playSound(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.25f, 1, false);						
					}
				}
			}	
		}
		if(FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT) != null && FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) instanceof FluidHandlerItemStack) {
			FluidHandlerItemStack tempContainer = (FluidHandlerItemStack)FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if(tempContainer.getFluid() == null) {
				if(EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT) != null) {
					if(EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).isItemEqual(FILLED_BUCKET_HANDLER.getStackInSlot(FILLED_BUCKET_SLOT))) {
						if(EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).stackSize < EMPTY_BUCKET_HANDLER.getStackInSlot(EMPTY_BUCKET_SLOT).getMaxStackSize()) {
							EMPTY_BUCKET_HANDLER.insertItem(EMPTY_BUCKET_SLOT, FILLED_BUCKET_HANDLER.extractItem(FILLED_BUCKET_SLOT, 1, false), false);
						}
					}
				}else{
					EMPTY_BUCKET_HANDLER.insertItem(EMPTY_BUCKET_SLOT, FILLED_BUCKET_HANDLER.extractItem(FILLED_BUCKET_SLOT, 1, false), false);
				}
			}
		}
	}
	public FluidContainerInteractionMode getMode() {
		return MODE;
	}
	public FluidContainerInteractionMode getInverseMode() {
		if(MODE == FluidContainerInteractionMode.DrainToContainer) {
			return FluidContainerInteractionMode.FillFromContainer;
		}else{
			return FluidContainerInteractionMode.DrainToContainer;
		}
	}
}
