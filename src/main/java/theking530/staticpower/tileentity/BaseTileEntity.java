package theking530.staticpower.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.utils.RedstoneModeList;
import theking530.staticpower.utils.RedstoneModeList.RedstoneMode;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.SideModeList.Mode;
import theking530.staticpower.utils.SidePicker;
import theking530.staticpower.utils.SidePicker.BlockSide;
import theking530.staticpower.utils.SidePicker.Side;
import theking530.staticpower.utils.Vector3;

public class BaseTileEntity extends TileEntity implements ISidedInventory, ITickable {

	public ItemStack[] slots;
	public ItemStackHandler  SLOTS_INPUT;
	public ItemStackHandler  SLOTS_OUTPUT;
	public ItemStackHandler  SLOTS_INTERNAL;
	
	protected static int[] slots_top = new int[] {};
	protected static int[] slots_bottom = new int[] {};
	protected static int[] slots_side = new int[] {};		
	
	public String CUSTOM_NAME;
	public int REDSTONE_MODE = 0;
	/**
	 * 0 = In/Out : 1 = In : 2 = Out : 3 = Disabled
	 */
	public SideModeList.Mode[] SIDE_MODES = {Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular};
	public int IN_TIMER = 0;
	public int IN_TIME = 2;
	public int OUT_TIMER = 0;
	public int OUT_TIME = 2;
	public int UPDATE_TIMER = 0;
	public int UPDATE_TIME = 10;
	
	public int[] OUTPUT_SLOTS;
	public int[] INPUT_SLOTS;
	
	public BaseTileEntity() {

	}
	public void initializeBasicTileEntity(int slotCount, int[] inputSlots, int[] outputSlots) {
		slots = new ItemStack[slotCount];
		SLOTS_INPUT = new ItemStackHandler(slotCount);
		OUTPUT_SLOTS = outputSlots;
		INPUT_SLOTS = inputSlots;
		
		SLOTS_INPUT = new ItemStackHandler();
		SLOTS_OUTPUT = new ItemStackHandler();
		SLOTS_INTERNAL = new ItemStackHandler();
	}
	@Override
	public void update() {
		int redstoneSignal = worldObj.getRedstonePower(pos, EnumFacing.NORTH);
		if(REDSTONE_MODE == 0) {
			if(OUTPUT_SLOTS != null) {
				outputFunction(OUTPUT_SLOTS);	
			}
			if(INPUT_SLOTS != null) {
				inputFunction(INPUT_SLOTS);				
			}
		}
		if(REDSTONE_MODE == 1) {
			if(redstoneSignal == 0) {
				if(OUTPUT_SLOTS != null) {
					outputFunction(OUTPUT_SLOTS);	
				}
				if(INPUT_SLOTS != null) {
					inputFunction(INPUT_SLOTS);				
				}
			}
		}
		if(REDSTONE_MODE == 2) {
			if(redstoneSignal > 0) {
				if(OUTPUT_SLOTS != null) {
					outputFunction(OUTPUT_SLOTS);	
				}
				if(INPUT_SLOTS != null) {
					inputFunction(INPUT_SLOTS);				
				}
			}
		}	
		if(UPDATE_TIMER < UPDATE_TIME) {
			UPDATE_TIMER++;
		}else{
			markForUpdate();
			markDirty();
			UPDATE_TIMER = 0;
		}
	}			
	
	public void setCustomName(String name) {
	}
	public int getSizeInventory() {
		if(slots != null) {
			return slots.length;	
		}
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int i) {
		if(i < slots.length) {
			return slots[i];
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (slots[i] !=null) {
			if (slots[i].stackSize <= j) {
				ItemStack itemstack = slots[i];
				slots[i] = null;
				return itemstack;
			}
			
			ItemStack itemstack1 = slots[i].splitStack(j);
			
			if(slots[i].stackSize == 0) {
				slots[i] = null;
			}
			
			return itemstack1;
			
		}else{
			
			return null;
		}
	}	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		slots[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (worldObj.getTileEntity(pos) != this) {
			return false;
		}else{
			return player.getDistanceSq((double)pos.getX() + 0.25D, (double)pos.getY() + 0.25D, (double)pos.getZ() + 0.25D) <= 22;
		}

	}	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(itemstack != null) {
			if(OUTPUT_SLOTS != null) {
				for(int j=0; j<OUTPUT_SLOTS.length; j++) {
					if(OUTPUT_SLOTS[j] == i){
						return false;
					}
				}	
			}
	    	if(slots[i] == null) {
	    		return true;
	    	}else{
	    		if(slots[i].isItemEqual(itemstack) && slots[i].stackSize + itemstack.stackSize < itemstack.getMaxStackSize() && slots[i].areItemStackTagsEqual(slots[i], itemstack)){
	    			return true;
	    		}
	    	}
		}
		return false;			
	}
   
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        REDSTONE_MODE = nbt.getShort("REDSTONE_MODE");
        for(int i=0; i<6; i++) {
        	SIDE_MODES[i] = SideModeList.Mode.values()[nbt.getInteger("SLOSIDEMODETMODE" + i)];
        }      
        if(slots != null) {
            NBTTagList list = nbt.getTagList("Items", 10);
    		slots = new ItemStack[getSizeInventory()];
            for (int i =0; i < list.tagCount(); i++) {
    			NBTTagCompound nbt1 = (NBTTagCompound)list.getCompoundTagAt(i);
    			byte b0 = nbt1.getByte("Slot");
    			
    			if (b0 >= 0 && b0 < slots.length) {
    				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
    			}
    		}	
        }
        SLOTS_INPUT.deserializeNBT((NBTTagCompound) nbt.getTag("INPUTS"));
        SLOTS_OUTPUT.deserializeNBT((NBTTagCompound) nbt.getTag("OUTPUTS"));
        SLOTS_INTERNAL.deserializeNBT((NBTTagCompound) nbt.getTag("INTERNAL"));
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		nbt.setShort("REDSTONE_MODE", (short)REDSTONE_MODE);
        for(int i=0; i<6; i++) {
        	nbt.setInteger("SIDEMODE" + i, SIDE_MODES[i].ordinal());
        }
    	if(slots != null) {
        	NBTTagList list = new NBTTagList();
    		for (int i = 0; i < slots.length; i++) {
    			if (slots[i] != null) {
    				NBTTagCompound nbt1 = new NBTTagCompound();
    				nbt1.setByte("Slot", (byte)i);
    				slots[i].writeToNBT(nbt1);
    				list.appendTag(nbt1);
    			}
    			
    		}
    		nbt.setTag("Items", list);
    	}
    	nbt.setTag("INPUTS", SLOTS_INPUT.serializeNBT());
    	nbt.setTag("OUTPUTS", SLOTS_OUTPUT.serializeNBT());
    	nbt.setTag("INTERNAL", SLOTS_INTERNAL.serializeNBT());
		return nbt;	
	}
	
	public NBTTagCompound onMachineBroken() {
		NBTTagCompound nbt = new NBTTagCompound();	
    	return writeToNBT(nbt);
	}
	public void onMachinePlaced(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}	
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
    	NBTTagCompound tag = new NBTTagCompound();
    	writeToNBT(tag);
    	return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tag);
    }

    public Mode getModeFromInt(int side) {
    	return SIDE_MODES[side];
    }   
    public Mode getSideModeFromBlockSide(BlockSide side) {
    	EnumFacing facing = EnumFacing.getHorizontal(this.getBlockMetadata());
    	switch(facing) {
    	case NORTH:
        	switch(side) {
	    	case Front : return SIDE_MODES[2];
	    	case Back : return SIDE_MODES[3];
	    	case Top : return SIDE_MODES[1];
	    	case Bottom : return SIDE_MODES[0];
	    	case Right : return SIDE_MODES[5];
	    	case Left : return SIDE_MODES[4];
        	}
    	case SOUTH:
        	switch(side) {
	    	case Front : return SIDE_MODES[2];
	    	case Back : return SIDE_MODES[3];
	    	case Top : return SIDE_MODES[1];
	    	case Bottom : return SIDE_MODES[0];
	    	case Right : return SIDE_MODES[4];
	    	case Left : return SIDE_MODES[5];
        	}
    	case EAST:
        	switch(side) {
	    	case Front : return SIDE_MODES[5];
	    	case Back : return SIDE_MODES[4];
	    	case Top : return SIDE_MODES[1];
	    	case Bottom : return SIDE_MODES[0];
	    	case Right : return SIDE_MODES[3];
	    	case Left : return SIDE_MODES[2];
        	}
    	case WEST:
        	switch(side) {
	    	case Front : return SIDE_MODES[4];
	    	case Back : return SIDE_MODES[5];
	    	case Top : return SIDE_MODES[1];
	    	case Bottom : return SIDE_MODES[0];
	    	case Right : return SIDE_MODES[2];
	    	case Left : return SIDE_MODES[3];
        	}
    	default:
        	switch(side) {
	    	case Front : return SIDE_MODES[2];
	    	case Back : return SIDE_MODES[3];
	    	case Top : return SIDE_MODES[1];
	    	case Bottom : return SIDE_MODES[0];
	    	case Right : return SIDE_MODES[5];
	    	case Left : return SIDE_MODES[4];
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
		if(slot == null) {
			return true;
		}
		if(item != null) {
			if(slot.getItem() == item.getItem() && (!item.getHasSubtypes() || item.getItemDamage() == slot.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, slot)) {
				int i = slot.stackSize + item.stackSize;			
				if(i <= item.getMaxStackSize() && i <= this.getInventoryStackLimit()) {
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	public boolean canSlotAcceptItemstackIgnoringCount(ItemStack item, ItemStack slot) {
		if(slot == null) {
			return true;
		}
		if(item != null) {
			if(slot.getItem() == item.getItem() && (!item.getHasSubtypes() || item.getItemDamage() == slot.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, slot)) {
					return true;
			}
		}
		return false;
	}
	public boolean placeStackInSlot(ItemStack stack, int slot) {
		if(stack != null) {
			if(canSlotAcceptItemstack(stack, slots[slot])) {
				if(slots[slot] == null) {
					slots[slot] = stack.copy();
					return true;
				}else{
					if(slots[slot].isItemEqual(stack)) {
						int i = slots[slot].stackSize + stack.stackSize;
						if(i > stack.getMaxStackSize() || i > this.getInventoryStackLimit()) {
							return false;
						}else if(i <= stack.getMaxStackSize()){
							slots[slot].stackSize += stack.stackSize;	
							return true;
						}
					}
				}
			}
		}	
		return false;
	}
	/** From Slot to Slot */
	public void moveItem(int fromSlot, int toSlot) {
		if(slots[fromSlot] != null) {
			ItemStack inputStack = slots[fromSlot];
			slots[toSlot] = inputStack.copy();
			slots[toSlot].stackSize = 1;
			slots[fromSlot].stackSize -= 1;
			if (slots[fromSlot].stackSize <= 0) {
				slots[fromSlot] = null;
			}		
		}
	}
	public boolean isSlotFree(ItemStack itemStack, ItemStack slot) {
		if(slot == null) {
			return true;
		}
		if(itemStack != null) {
			if(slot.getItem() == itemStack.getItem() && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == slot.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, slot)) {
				int i = slot.stackSize + itemStack.stackSize;
				if(i <= itemStack.getMaxStackSize() && i <= this.getInventoryStackLimit()) {
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	public void placeItemStackInSlot(ItemStack stack, int slot) {
		 if(stack != null) {
			if(isSlotFree(stack, slots[slot])) {
				if(slots[slot] == null) {
					slots[slot] = stack.copy();
				}else{
					if(slots[slot].isItemEqual(stack)) {
						int i = slots[slot].stackSize + stack.stackSize;
						if(i > stack.getMaxStackSize() || i > this.getInventoryStackLimit()) {
						}else if(i <= stack.getMaxStackSize()){
							slots[slot].stackSize += stack.stackSize;			
						}
					}
				}
			}
		}		
	}
    public TileEntity getTEFromBlockSide(SidePicker.BlockSide blockSide, EnumFacing facing) {
    	SidePicker.Side side = SidePicker.Side.getSideFromBlockSide(blockSide, facing);
    	switch(side) {
    	case YNeg : 
    		return worldObj.getTileEntity(pos.offset(EnumFacing.DOWN));
    	case YPos : 
    		return worldObj.getTileEntity(pos.offset(EnumFacing.UP));
    	case ZNeg : 
    		return worldObj.getTileEntity(pos.offset(EnumFacing.NORTH));
    	case ZPos : 
    		return worldObj.getTileEntity(pos.offset(EnumFacing.SOUTH));
    	case XNeg : 
    		return worldObj.getTileEntity(pos.offset(EnumFacing.WEST));
    	case XPos : 
    		return worldObj.getTileEntity(pos.offset(EnumFacing.EAST));
    		default :
    			break;
    	}
		return null;
    }
    public int getBlockXCoord(SidePicker.BlockSide blockSide, EnumFacing facing) {
    	SidePicker.Side side = SidePicker.Side.getSideFromBlockSide(blockSide, facing);
    	switch(side) {
    	case YNeg : 
    		return 0;
    	case YPos : 
    		return 0;
    	case ZNeg : 
    		return 0;
    	case ZPos : 
    		return 0;
    	case XNeg : 
    		return -1;
    	case XPos : 
    		return +1;
    		default :
    			break;
    	}
		return 0;
    } 
    public int getBlockZCoord(SidePicker.BlockSide blockSide, EnumFacing facing) {
    	SidePicker.Side side = SidePicker.Side.getSideFromBlockSide(blockSide, facing);
    	switch(side) {
    	case YNeg : 
    		return 0;
    	case YPos : 
    		return 0;
    	case ZNeg : 
    		return -1;
    	case ZPos : 
    		return +1;
    	case XNeg : 
    		return 0;
    	case XPos : 
    		return 0;
    		default :
    			break;
    	}
		return 0;
    } 
    public int getBlockYCoord(SidePicker.BlockSide blockSide, EnumFacing facing) {
    	SidePicker.Side side = SidePicker.Side.getSideFromBlockSide(blockSide, facing);
    	switch(side) {
    	case YNeg : 
    		return -1;
    	case YPos : 
    		return +1;
    	case ZNeg : 
    		return 0;
    	case ZPos : 
    		return 0;
    	case XNeg : 
    		return 0;
    	case XPos : 
    		return 0;
    		default :
    			break;
    	}
		return 0;
    } 
    public RedstoneMode getRedstoneMode() {
    	return RedstoneModeList.RedstoneMode.getModeFromInt(REDSTONE_MODE);
    } 
    public Item getItem(ItemStack itemStack) {
    	if(itemStack != null) {
    		return itemStack.getItem();
    	}
		return null;
    }
    public boolean hasResult(ItemStack stack) {
    	return true;
    }

    public Mode getModeFromFacing(EnumFacing facing) {
    	return SIDE_MODES[facing.ordinal()];
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
		if(inv.getStackInSlot(slot) == null) {
			inv.setInventorySlotContents(slot, stack);
			return true;
		}else{
			if(inv.getStackInSlot(slot).isItemEqual(stack) && inv.getStackInSlot(slot).areItemStackTagsEqual(inv.getStackInSlot(slot), stack)) {
				int stackSize = inv.getStackInSlot(slot).stackSize + stack.stackSize;
				if(stackSize <= 64) {
					inv.getStackInSlot(slot).stackSize = stackSize;
					return true;
				}		
			}
		}
		return false;
    }
    public void outputItem(int fromSlot, SidePicker.BlockSide blockSide, int startSlot, boolean backwards) {
		if (getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Output || getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Disabled) {
			ItemStack stack = slots[fromSlot];
			if (stack == null) {
				return;
			} else {
				EnumFacing facing = worldObj.getBlockState(pos).getValue(BlockHorizontal.FACING);
				TileEntity te = getTEFromBlockSide(blockSide, facing);
				if (te == null) {
					return;
				} else if (te instanceof IInventory) {
					IInventory inv = (IInventory) te;
					int numSlots = inv.getSizeInventory();
					if (!worldObj.isRemote) {
						boolean flag1 = false;
						int k = startSlot;
						if (backwards) {
							k = numSlots - 1;
						}

						ItemStack itemstack1;

						if (stack.isStackable() && inv.isItemValidForSlot(k, stack)) {
							while (stack.stackSize > 0 && (!backwards && k < numSlots || backwards && k >= startSlot)) {
								itemstack1 = inv.getStackInSlot(k);

								if (itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
									int l = itemstack1.stackSize + stack.stackSize;

									if (l <= stack.getMaxStackSize()) {
										stack.stackSize = 0;
										if (slots[fromSlot].stackSize <= 0) {
											slots[fromSlot] = null;
										}
										itemstack1.stackSize = l;
										flag1 = true;
									} else if (itemstack1.stackSize < stack.getMaxStackSize()) {
										stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
										if (slots[fromSlot].stackSize <= 0) {
											slots[fromSlot] = null;
										}
										itemstack1.stackSize = stack.getMaxStackSize();
										flag1 = true;
									}
								}

								if (backwards) {
									--k;
								} else {
									++k;
								}
							}
						}

						if (stack.stackSize > 0) {
							if (backwards) {
								k = numSlots - 1;
							} else {
								k = startSlot;
							}

							while (!backwards && k < numSlots || backwards && k >= startSlot) {
								itemstack1 = inv.getStackInSlot(k);

								if (itemstack1 == null && inv.isItemValidForSlot(k, stack)) {
									inv.setInventorySlotContents(k, stack.copy());
									slots[fromSlot] = null;
									flag1 = true;
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
	public void inputItem(int inputSlot, SidePicker.BlockSide blockSide, int startSlot) {
		if (getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Input || getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Disabled) {
			EnumFacing facing = worldObj.getBlockState(pos).getValue(BlockHorizontal.FACING);
			TileEntity te = getTEFromBlockSide(blockSide, facing);
			if (te == null) {
				return;
			} else if (te instanceof IInventory) {
				IInventory inv = (IInventory) te;
				if (!worldObj.isRemote) {
					for(int currentTESlot = startSlot; currentTESlot < inv.getSizeInventory(); currentTESlot++) {
						ItemStack stack = inv.getStackInSlot(currentTESlot);
						if(stack != null) {
							if(hasResult(stack)) {
								if (canSlotAcceptItemstackIgnoringCount(stack, slots[inputSlot])) {				
									if(slots[inputSlot] == null && stack != null) {
										slots[inputSlot] = stack.copy();
										inv.setInventorySlotContents(currentTESlot, null);
									}else{
										int i = slots[inputSlot].stackSize + stack.stackSize;
										
										if(i > slots[inputSlot].getMaxStackSize()) {
											stack.stackSize -= stack.getMaxStackSize() - slots[inputSlot].stackSize;
											slots[inputSlot].stackSize = stack.getMaxStackSize();
										}else if(i <= slots[inputSlot].getMaxStackSize()){
											slots[inputSlot].stackSize = i;
											inv.setInventorySlotContents(currentTESlot, null);				
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
	public void inputFunction(int...inputSlots) {
		if(slots != null && INPUT_SLOTS != null && inputSlots.length > 0) {
			Random randomGenerator = new Random();
			int slot = randomGenerator.nextInt(inputSlots.length);
			int rand = randomGenerator.nextInt(5);
			IN_TIMER++;
			if(IN_TIMER>0) {
				if(IN_TIMER >= IN_TIME) {
					switch(rand) {
					case 0: inputItem(slot, SidePicker.BlockSide.Top, 0);
							break;
					case 1: inputItem(slot, SidePicker.BlockSide.Bottom, 0);
							break;
					case 2: inputItem(slot, SidePicker.BlockSide.Right, 0);
							break;
					case 3: inputItem(slot, SidePicker.BlockSide.Left, 0);
							break;
					case 4: inputItem(slot, SidePicker.BlockSide.Back, 0);
							break;
					default:
						break;
					}
				IN_TIMER = 0;
				}
			}	
		}
	}
	public void outputFunction(int... outputSlots) {
		if(outputSlots.length > 0 && slots != null) {
			Random randomGenerator = new Random();
			int slot = randomGenerator.nextInt(outputSlots.length);
			if(getStackInSlot(outputSlots[slot]) != null) {
				OUT_TIMER++;
				if(OUT_TIMER>0) {
					int rand = randomGenerator.nextInt(5);
				if(OUT_TIMER >= OUT_TIME) {
					switch(rand) {
					case 0: outputItem(outputSlots[slot], SidePicker.BlockSide.Top, 0, false);
							break;
					case 1: outputItem(outputSlots[slot], SidePicker.BlockSide.Bottom, 0, false);
							break;
					case 2: outputItem(outputSlots[slot], SidePicker.BlockSide.Right, 0, false);
							break;
					case 3: outputItem(outputSlots[slot], SidePicker.BlockSide.Left, 0, false);
							break;
					case 4: outputItem(outputSlots[slot], SidePicker.BlockSide.Back, 0, false);
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
		if (stack == null) {
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
	
	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack temp = slots[index];
		slots[index] = null;
		return temp;
	}
	@Override
	public void openInventory(EntityPlayer player) {	
	}
	@Override
	public void closeInventory(EntityPlayer player) {	
	}
	@Override
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {
	}
	@Override
	public int getFieldCount() {
		return 0;
	}
	@Override
	public void clear() {
	}
	@Override
	public String getName() {
		return CUSTOM_NAME;
	}
	@Override
	public boolean hasCustomName() {
		return true;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side == EnumFacing.UP) {
			return INPUT_SLOTS;
		}else if(side == EnumFacing.DOWN){
			return OUTPUT_SLOTS;
		}else{
			return OUTPUT_SLOTS;
		}
	}
	@Override
	public boolean canInsertItem(int index, ItemStack itemstack, EnumFacing direction) {
		if(slots[index] == null) {
			return true;
		}else{
			if(slots[index].isItemEqual(itemstack) && slots[index].areItemStackTagsEqual(itemstack, slots[index])) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}
	
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }

    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if(facing == EnumFacing.UP) {
    			return (T) SLOTS_INPUT;
    		}else{
    			return (T) SLOTS_OUTPUT;
    		}
    	}
    	return super.getCapability(capability, facing);
    }
    
	public void markForUpdate(){ 
		worldObj.markAndNotifyBlock(pos, worldObj.getChunkFromBlockCoords(pos), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);

	}
	
	public void incrementSide(int side){
		if(SIDE_MODES[side].ordinal() >= 3) {
			SIDE_MODES[side] = SideModeList.Mode.values()[0];
		}else{
			SIDE_MODES[side] = SideModeList.Mode.values()[SIDE_MODES[side].ordinal()+1];
		}
	}
	public void resetSides(){
		for(int i=0; i<6; i++) {
			SIDE_MODES[i] = SideModeList.Mode.values()[0];
		}
	}
}
