package theking530.staticpower.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.machines.machinecomponents.IMachineComponentInterface;
import theking530.staticpower.utils.RedstoneModeList;
import theking530.staticpower.utils.RedstoneModeList.RedstoneMode;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.SideModeList.Mode;
import theking530.staticpower.utils.SidePicker.Side;
import theking530.staticpower.utils.SideUtils;
import theking530.staticpower.utils.SideUtils.BlockSide;

public class BaseTileEntity extends TileEntity implements ITickable {

	public ItemStackHandler  SLOTS_INPUT;
	public ItemStackHandler  SLOTS_OUTPUT;
	public ItemStackHandler  SLOTS_INTERNAL;
	public ItemStackHandler  SLOTS_UPGRADES;
	public Random RANDOM = new Random();
	
	public String CUSTOM_NAME;
	public int REDSTONE_MODE = 0;

	/** EnumFacing */
	public SideModeList.Mode[] SIDE_MODES = {Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular};
	public int IN_TIMER = 0;
	public int IN_TIME = 2;
	public int OUT_TIMER = 0;
	public int OUT_TIME = 2;
	public int UPDATE_TIMER = 0;
	public int UPDATE_TIME = 10;
	public boolean PLACED = false;
	
	public boolean REQUIRES_UPDATE = false;
	public ArrayList<IMachineComponentInterface> COMPONENTS;
	public boolean WRENCHED = false;
	
	public boolean DISABLE_FACE_INTERACTION;
	
	public BaseTileEntity() {

	}
	public void initializeBasicTileEntity(int internalSlots, int inputSlots, int outputSlots, boolean disableFaceInteraction) {
		SLOTS_INPUT = new ItemStackHandler(inputSlots);
		SLOTS_OUTPUT = new ItemStackHandler(outputSlots);
		SLOTS_INTERNAL = new ItemStackHandler(internalSlots);
		SLOTS_UPGRADES = new ItemStackHandler(3);
		COMPONENTS = new ArrayList<IMachineComponentInterface>();
		DISABLE_FACE_INTERACTION = disableFaceInteraction;
	}
	public void initializeBasicTileEntity(int internalSlots, int inputSlots, int outputSlots) {
		initializeBasicTileEntity(internalSlots, inputSlots, outputSlots, true);
	}
	@Override
	public void update() {
		if(!PLACED) {
			onPlaced();
			PLACED = true;
		}
		int redstoneSignal = getWorld().getRedstonePower(pos, EnumFacing.NORTH);
		if(REDSTONE_MODE == 0) {
			if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0) {
				outputFunction();	
			}
			if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0) {
				inputFunction();				
			}
			process();
		}
		if(REDSTONE_MODE == 1) {
			if(redstoneSignal == 0) {
				if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0) {
					outputFunction();	
				}
				if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0) {
					inputFunction();				
				}
				process();
			}
		}
		if(REDSTONE_MODE == 2) {
			if(redstoneSignal > 0) {
				if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0) {
					outputFunction();	
				}
				if(SLOTS_INPUT != null && SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0) {
					inputFunction();				
				}
				process();
			}
		}	
		if(UPDATE_TIMER < UPDATE_TIME) {
			UPDATE_TIMER++;
		}else{
			if(REQUIRES_UPDATE) {
				updateBlock();
			}
			markDirty();
			UPDATE_TIMER = 0;
		}
	}		
	public void onPlaced(){
		
	}
	public void process() {
		
	}
	public ItemStack getInputStack(int slot) {
		if(slot < SLOTS_INPUT.getSlots()) {
			return SLOTS_INPUT.getStackInSlot(slot);	
		}else{
			return ItemStack.EMPTY;
		}
	}
	public ItemStack getOutputStack(int slot) {
		return SLOTS_OUTPUT.getStackInSlot(slot);
	}
	public ItemStack getInternalStack(int slot) {
		return SLOTS_INTERNAL.getStackInSlot(slot);
	}
	public void setInternalStack(int slot, ItemStack stack) {
		SLOTS_INTERNAL.setStackInSlot(slot, stack);
	}
    
	public void readFromSyncNBT(NBTTagCompound nbt) {
	}
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		return nbt;
	}
	
	@Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        REDSTONE_MODE = nbt.getShort("REDSTONE_MODE");
        for(int i=0; i<6; i++) {
        	SIDE_MODES[i] = SideModeList.Mode.values()[nbt.getInteger("SIDEMODE" + i)];
        }      
        if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0 && nbt.hasKey("INPUTS")) {
        	SLOTS_INPUT.deserializeNBT((NBTTagCompound) nbt.getTag("INPUTS"));	
        }
        if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0 && nbt.hasKey("OUTPUTS")) {
            SLOTS_OUTPUT.deserializeNBT((NBTTagCompound) nbt.getTag("OUTPUTS"));
        }
        if(SLOTS_INTERNAL != null && SLOTS_INTERNAL.getSlots() > 0 && nbt.hasKey("INTERNAL")) {
            SLOTS_INTERNAL.deserializeNBT((NBTTagCompound) nbt.getTag("INTERNAL"));
        }
        if(SLOTS_UPGRADES != null && SLOTS_UPGRADES.getSlots() > 0 && nbt.hasKey("UPGRADES")) {
            SLOTS_UPGRADES.deserializeNBT((NBTTagCompound) nbt.getTag("UPGRADES"));
        }
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		nbt.setShort("REDSTONE_MODE", (short)REDSTONE_MODE);
        for(int i=0; i<6; i++) {
        	nbt.setInteger("SIDEMODE" + i, SIDE_MODES[i].ordinal());
        }
        if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0) {
        	nbt.setTag("INPUTS", SLOTS_INPUT.serializeNBT());	
        }
        if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0) {
        	nbt.setTag("OUTPUTS", SLOTS_OUTPUT.serializeNBT());
        }
        if(SLOTS_INTERNAL != null && SLOTS_INTERNAL.getSlots() > 0) {
        	nbt.setTag("INTERNAL", SLOTS_INTERNAL.serializeNBT());
        }
        if(SLOTS_UPGRADES != null && SLOTS_UPGRADES.getSlots() > 0) {
        	nbt.setTag("UPGRADES", SLOTS_UPGRADES.serializeNBT());
        }
        nbt.setBoolean("PLACED", true);
		return nbt;	
	}
	
	public NBTTagCompound onMachineBroken(NBTTagCompound nbt) {
		writeToNBT(nbt);
    	return nbt;
	}
	public void onMachinePlaced(NBTTagCompound nbt) {
        REDSTONE_MODE = nbt.getShort("REDSTONE_MODE");
        for(int i=0; i<6; i++) {
        	SIDE_MODES[i] = SideModeList.Mode.values()[nbt.getInteger("SIDEMODE" + i)];
        }      
        if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0) {
        	SLOTS_INPUT.deserializeNBT((NBTTagCompound) nbt.getTag("INPUTS"));	
        }
        if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0) {
            SLOTS_OUTPUT.deserializeNBT((NBTTagCompound) nbt.getTag("OUTPUTS"));
        }
        if(SLOTS_INTERNAL != null && SLOTS_INTERNAL.getSlots() > 0) {
            SLOTS_INTERNAL.deserializeNBT((NBTTagCompound) nbt.getTag("INTERNAL"));
        }
        if(SLOTS_UPGRADES != null && SLOTS_UPGRADES.getSlots() > 0) {
            SLOTS_UPGRADES.deserializeNBT((NBTTagCompound) nbt.getTag("UPGRADES"));
        }
	}	
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 1, this.getUpdateTag());
	}
    
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (getWorld().getTileEntity(pos) != this) {
			return false;
		}else{
			return player.getDistanceSq((double)pos.getX() + 0.25D, (double)pos.getY() + 0.25D, (double)pos.getZ() + 0.25D) <= 22;
		}

	}
    public String getName(){
    	return null;
    }
    public Mode getModeFromInt(int side) {
    	return SIDE_MODES[side];
    }   
    public Mode getSideModeFromBlockSide(BlockSide side) {
    	EnumFacing facing = EnumFacing.getHorizontal(this.getBlockMetadata());
    	switch(facing) {
    	case NORTH:
        	switch(side) {
	    	case FRONT : return SIDE_MODES[2];
	    	case BACK : return SIDE_MODES[3];
	    	case TOP : return SIDE_MODES[1];
	    	case BOTTOM : return SIDE_MODES[0];
	    	case RIGHT : return SIDE_MODES[5];
	    	case LEFT : return SIDE_MODES[4];
        	}
    	case SOUTH:
        	switch(side) {
	    	case FRONT : return SIDE_MODES[2];
	    	case BACK : return SIDE_MODES[3];
	    	case TOP : return SIDE_MODES[1];
	    	case BOTTOM : return SIDE_MODES[0];
	    	case RIGHT : return SIDE_MODES[4];
	    	case LEFT : return SIDE_MODES[5];
        	}
    	case EAST:
        	switch(side) {
	    	case FRONT : return SIDE_MODES[5];
	    	case BACK : return SIDE_MODES[4];
	    	case TOP : return SIDE_MODES[1];
	    	case BOTTOM : return SIDE_MODES[0];
	    	case RIGHT : return SIDE_MODES[3];
	    	case LEFT : return SIDE_MODES[2];
        	}
    	case WEST:
        	switch(side) {
	    	case FRONT : return SIDE_MODES[4];
	    	case BACK : return SIDE_MODES[5];
	    	case TOP : return SIDE_MODES[1];
	    	case BOTTOM : return SIDE_MODES[0];
	    	case RIGHT : return SIDE_MODES[2];
	    	case LEFT : return SIDE_MODES[3];
        	}
    	default:
        	switch(side) {
	    	case FRONT : return SIDE_MODES[2];
	    	case BACK : return SIDE_MODES[3];
	    	case TOP : return SIDE_MODES[1];
	    	case BOTTOM : return SIDE_MODES[0];
	    	case RIGHT : return SIDE_MODES[5];
	    	case LEFT : return SIDE_MODES[4];
        	}
    	}
		return null;
    }
    public Mode getSideModeFromSide(Side side) {
    	switch(side) {
	    	case XPos : return SIDE_MODES[5];
	    	case XNeg : return SIDE_MODES[4];
	    	case YPos : return SIDE_MODES[1];
	    	case YNeg : return SIDE_MODES[0];
	    	case ZPos : return SIDE_MODES[3];
	    	case ZNeg : return SIDE_MODES[2];
    	}
		return null;	
    }
	public boolean canSlotAcceptItemstack(ItemStack item, ItemStack slot) {
		if(slot == ItemStack.EMPTY) {
			return true;
		}

		if(item != ItemStack.EMPTY) {
			if(slot.getItem() == item.getItem() && (!item.getHasSubtypes() || item.getItemDamage() == slot.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, slot)) {
				int i = slot.getCount() + item.getCount();			
				if(i <= item.getMaxStackSize()) {
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	public boolean canSlotAcceptItemstackIgnoringCount(ItemStack item, ItemStack slot) {
		if(slot == ItemStack.EMPTY) {
			return true;
		}
		if(item != null) {
			if(slot.getItem() == item.getItem() && (!item.getHasSubtypes() || item.getItemDamage() == slot.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, slot)) {
					return true;
			}
		}
		return false;
	}
	/** From Slot to Slot */
	public void moveItem(ItemStackHandler fromInv, int fromSlot, ItemStackHandler toInv, int toSlot) {
		if(fromInv.getStackInSlot(fromSlot) != ItemStack.EMPTY) {
			ItemStack extractedItem = fromInv.extractItem(fromSlot, 1, false);
			if(extractedItem != ItemStack.EMPTY) {
				toInv.insertItem(toSlot, extractedItem, false);
			}
		}
	}

    public RedstoneMode getRedstoneMode() {
    	return RedstoneModeList.RedstoneMode.getModeFromInt(REDSTONE_MODE);
    } 
    public Item getItem(ItemStack itemStack) {
    	if(itemStack != ItemStack.EMPTY) {
    		return itemStack.getItem();
    	}
		return null;
    }
    public boolean hasResult(ItemStack stack) {
    	return true;
    }

    public Mode getModeFromFacing(EnumFacing facing) {
    	if(SideUtils.getBlockHorizontal(getWorld().getBlockState(pos)) != null) {
    		return SIDE_MODES[SideUtils.getBlockSide(facing.getOpposite(), SideUtils.getBlockHorizontal(getWorld().getBlockState(pos))).ordinal()];	
    	}
    	return Mode.Disabled;
    }
    
    public EnumFacing getFacingDirection() {
    	if(getWorld().getBlockState(pos).getProperties().containsKey(BlockHorizontal.FACING)) {
        	return getWorld().getBlockState(getPos()).getValue(BlockHorizontal.FACING);
    	}else{
        	return EnumFacing.UP;
    	}
    }
    
    public boolean insertItem(ItemStack stack, ISidedInventory inv, EnumFacing side) {
		int[] tempSlots = inv.getSlotsForFace(side);
		for(int i=0; i<tempSlots.length; i++) {
			if(inv.canInsertItem(tempSlots[i], stack, side)) {
				return insertItem(stack, inv, tempSlots[i]);
			}
		}
		return false;
    }
    public boolean insertItem(ItemStack stack, IInventory inv, int slot) {
		if(inv.getStackInSlot(slot) == ItemStack.EMPTY) {
			inv.setInventorySlotContents(slot, stack);
			return true;
		}else{
			if(inv.getStackInSlot(slot).isItemEqual(stack) && ItemStack.areItemStacksEqual(inv.getStackInSlot(slot), stack)) {
				int stackSize = inv.getStackInSlot(slot).getCount() + stack.getCount();
				if(stackSize <= 64) {
					inv.getStackInSlot(slot).setCount(stackSize);
					return true;
				}		
			}
		}
		return false;
    }
    public void outputItem(int fromSlot, theking530.staticpower.utils.SideUtils.BlockSide blockSide, int startSlot, boolean backwards) {
		if (getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Output || getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Disabled) {
			ItemStack stack = SLOTS_OUTPUT.getStackInSlot(fromSlot);
			if (stack == ItemStack.EMPTY) {
				return;
			} else {
				EnumFacing facing = getWorld().getBlockState(pos).getValue(BlockHorizontal.FACING);
				TileEntity te = getWorld().getTileEntity(pos.offset(SideUtils.getEnumFacingFromSide(blockSide, facing)));
				if (te == null) {
					return;
				} else if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing))) {
					IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing));
					int numSlots = inv.getSlots();
					if (!getWorld().isRemote) {
						int k = startSlot;
						if (backwards) {
							k = numSlots - 1;
						}

						ItemStack itemstack1;

						if (stack.isStackable() && inv.insertItem(k, stack, false) == ItemStack.EMPTY) {
							while (stack.getCount() > 0 && (!backwards && k < numSlots || backwards && k >= startSlot)) {
								itemstack1 = inv.getStackInSlot(k);

								if (itemstack1 != ItemStack.EMPTY && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
									int l = itemstack1.getCount() + stack.getCount();

									if (l <= stack.getMaxStackSize()) {
										stack.setCount(0);
										if (SLOTS_OUTPUT.getStackInSlot(fromSlot).getCount() <= 0) {
											SLOTS_OUTPUT.setStackInSlot(fromSlot, ItemStack.EMPTY);
										}
										itemstack1.setCount(l);
									} else if (itemstack1.getCount() < stack.getMaxStackSize()) {
										stack.setCount(stack.getCount() - stack.getMaxStackSize() - itemstack1.getCount());
										if (SLOTS_OUTPUT.getStackInSlot(fromSlot).getCount() <= 0) {
											SLOTS_OUTPUT.setStackInSlot(fromSlot, ItemStack.EMPTY);
										}
										itemstack1.setCount(stack.getMaxStackSize());
									}
								}

								if (backwards) {
									--k;
								} else {
									++k;
								}
							}
						}

						if (stack.getCount() > 0) {
							if (backwards) {
								k = numSlots - 1;
							} else {
								k = startSlot;
							}

							while (!backwards && k < numSlots || backwards && k >= startSlot) {
								itemstack1 = inv.getStackInSlot(k);

								if (itemstack1 == ItemStack.EMPTY && inv.insertItem(k, stack, false) == ItemStack.EMPTY) {
									inv.insertItem(k, stack.copy(), true);
									SLOTS_OUTPUT.setStackInSlot(fromSlot, ItemStack.EMPTY);
									break;
								}

								if (backwards) {
									--k;
								} else {
									++k;
								}
							}
						}
					}
				}
			}
		}else{
			return;
		}
    }
	public void inputItem(int inputSlot, BlockSide blockSide, int startSlot) {
		if (getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Input || getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Disabled) {
			EnumFacing facing = getWorld().getBlockState(pos).getValue(BlockHorizontal.FACING);
			TileEntity te = getWorld().getTileEntity(pos.offset(SideUtils.getEnumFacingFromSide(blockSide, facing)));
			if (te == null) {
				return;
			} else if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing))) {
				IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing));
				if (!getWorld().isRemote) {
					for(int currentTESlot = startSlot; currentTESlot < inv.getSlots(); currentTESlot++) {
						ItemStack stack = inv.getStackInSlot(currentTESlot);
						if(stack != null) {
							if(hasResult(stack)) {
								if (canSlotAcceptItemstackIgnoringCount(stack, SLOTS_INPUT.getStackInSlot(inputSlot))) {				
									if(SLOTS_INPUT.getStackInSlot(inputSlot) == null && stack != null) {
										SLOTS_INPUT.setStackInSlot(inputSlot, stack.copy());
										inv.extractItem(currentTESlot, inv.getStackInSlot(currentTESlot).getCount(), false);
									}else{
										int i = SLOTS_INPUT.getStackInSlot(inputSlot).getCount() + stack.getCount();
										
										if(i > SLOTS_INPUT.getStackInSlot(inputSlot).getMaxStackSize()) {
											stack.setCount(stack.getCount() - stack.getMaxStackSize() - SLOTS_INPUT.getStackInSlot(inputSlot).getCount());
											SLOTS_INPUT.getStackInSlot(inputSlot).setCount(stack.getMaxStackSize());
										}else if(i <= SLOTS_INPUT.getStackInSlot(inputSlot).getMaxStackSize()){
											SLOTS_INPUT.getStackInSlot(inputSlot).setCount(i);
											inv.extractItem(currentTESlot, inv.getStackInSlot(currentTESlot).getCount(), false);			
										}	
									}									
								}
							}
						}
					}
				}
			}
		}					
	}	
	public void inputFunction() {
		if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0 && getWorld().getBlockState(pos) != null && getWorld().getBlockState(pos) != Blocks.AIR) {
			Random randomGenerator = new Random();
			int slot = randomGenerator.nextInt(SLOTS_INPUT.getSlots());
			int rand = randomGenerator.nextInt(5);
			IN_TIMER++;
			if(IN_TIMER>0) {
				if(IN_TIMER >= IN_TIME) {
					switch(rand) {
					case 0: inputItem(slot, BlockSide.TOP, 0);
							break;
					case 1: inputItem(slot, BlockSide.BOTTOM, 0);
							break;
					case 2: inputItem(slot, BlockSide.RIGHT, 0);
							break;
					case 3: inputItem(slot, BlockSide.LEFT, 0);
							break;
					case 4: inputItem(slot, BlockSide.BACK, 0);
							break;
					default:
						break;
					}
				IN_TIMER = 0;
				}
			}	
		}
	}
	public void outputFunction() {
		if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0) {
			Random randomGenerator = new Random();
			int slot = randomGenerator.nextInt(SLOTS_OUTPUT.getSlots());
			if(SLOTS_OUTPUT.getStackInSlot(slot) != ItemStack.EMPTY) {
				OUT_TIMER++;
				if(OUT_TIMER>0) {
					int rand = randomGenerator.nextInt(5);
				if(OUT_TIMER >= OUT_TIME) {
					switch(rand) {
					case 0: outputItem(slot, BlockSide.TOP, 0, false);
							break;
					case 1: outputItem(slot, BlockSide.BOTTOM, 0, false);
							break;
					case 2: outputItem(slot, BlockSide.RIGHT, 0, false);
							break;
					case 3: outputItem(slot, BlockSide.LEFT, 0, false);
							break;
					case 4: outputItem(slot, BlockSide.BACK, 0, false);
							break;
					default:
						break;
					}
					OUT_TIMER = 0;
					}
				}
			}
		}
	}
	public boolean canInventoryAcceptStacks(IInventory inv, List<ItemStack> stacks) {
		boolean flag = true;
		if(stacks == null) {
			return true;
		}else{
			for(int i=0; i<stacks.size(); i++) {
				if(!canInventoryAcceptStack(stacks.get(i), inv)) {
					flag = false;
				}
			}
			return flag;
		}	
	}
	public boolean canInventoryAcceptStack(ItemStack stack, IInventory inv) {
		if (stack == ItemStack.EMPTY) {
			return false;
		}else{
			for(int i=0; i<inv.getSizeInventory(); i++) {
				if(canSlotAcceptItemstack(stack, inv.getStackInSlot(i))) {
					return true;
				}
			}
		}
		return false;		
	}
	
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(SIDE_MODES[facing.ordinal()] == Mode.Disabled) {
    		return false;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && SideUtils.getBlockSide(facing, getFacingDirection()) != BlockSide.FRONT) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
    @SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
       	if(SIDE_MODES[facing.ordinal()] == Mode.Disabled || SideUtils.getBlockSide(facing, getFacingDirection()) == BlockSide.FRONT) {
    		return null;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if(SIDE_MODES[facing.ordinal()] == Mode.Output) {
    			return (T) SLOTS_OUTPUT;
    		}else{
    			return (T) SLOTS_INPUT;
    		}
    	}
    	return super.getCapability(capability, facing);
    }
    
	public void incrementSide(int side){
		if(side == 3 && DISABLE_FACE_INTERACTION) {
			SIDE_MODES[3] = SideModeList.Mode.Disabled;
		}
		if(SIDE_MODES[side].ordinal() >= 3) {
			SIDE_MODES[side] = SideModeList.Mode.values()[0];
		}else{
			SIDE_MODES[side] = SideModeList.Mode.values()[SIDE_MODES[side].ordinal()+1];
		}
		onSidesConfigUpdate();
	}
	public void resetSides(){
		for(int i=0; i<6; i++) {
			SIDE_MODES[i] = SideModeList.Mode.values()[0];
		}
		onSidesConfigUpdate();
	}
	public void updateBlock() {
		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 2);
	}
	public void onSidesConfigUpdate() {
		
	}
}
