package theking530.staticpower.tileentity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.PacketMachineSync;
import theking530.staticpower.utils.OldSidePicker;
import theking530.staticpower.utils.OldSidePicker.BlockSide;
import theking530.staticpower.utils.RedstoneModeList;
import theking530.staticpower.utils.RedstoneModeList.RedstoneMode;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.SideModeList.Mode;
import theking530.staticpower.utils.SidePicker.Side;

public class BaseTileEntity extends TileEntity implements ITickable {

	public ItemStackHandler  SLOTS_INPUT;
	public ItemStackHandler  SLOTS_OUTPUT;
	public ItemStackHandler  SLOTS_INTERNAL;
	public ItemStackHandler  SLOTS_UPGRADES;
	public Random RANDOM = new Random();
	
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
	public boolean PLACED = false;
	
	public BaseTileEntity() {

	}
	public void initializeBasicTileEntity(int internalSlots, int inputSlots, int outputSlots) {
		SLOTS_INPUT = new ItemStackHandler(inputSlots);
		SLOTS_OUTPUT = new ItemStackHandler(outputSlots);
		SLOTS_INTERNAL = new ItemStackHandler(internalSlots);
		SLOTS_UPGRADES = new ItemStackHandler(3);
	}
	@Override
	public void update() {
		if(!PLACED) {
			onPlaced();
			PLACED = true;
		}
		int redstoneSignal = worldObj.getRedstonePower(pos, EnumFacing.NORTH);
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
			markForUpdate();
			markDirty();
			sync();
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
			return null;
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
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
    	NBTTagCompound tag = new NBTTagCompound();
    	writeToNBT(tag);
    	return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tag);
    }

	public boolean isUseableByPlayer(EntityPlayer player) {
		if (worldObj.getTileEntity(pos) != this) {
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
    public EnumFacing getFacingFromBlockSide(BlockSide side) {
    	EnumFacing facing = EnumFacing.getHorizontal(this.getBlockMetadata());
    	switch(facing) {
    	case NORTH:
        	switch(side) {
	    	case Front : return EnumFacing.NORTH;
	    	case Back : return EnumFacing.SOUTH;
	    	case Top : return EnumFacing.UP;
	    	case Bottom : return EnumFacing.DOWN;
	    	case Right : return EnumFacing.EAST;
	    	case Left : return EnumFacing.WEST;
        	}
    	case SOUTH:
        	switch(side) {
	    	case Front : return EnumFacing.NORTH;
	    	case Back : return EnumFacing.SOUTH;
	    	case Top : return EnumFacing.UP;
	    	case Bottom : return EnumFacing.DOWN;
	    	case Right : return EnumFacing.WEST;
	    	case Left : return EnumFacing.EAST;
        	}
    	case EAST:
        	switch(side) {
	    	case Front : return EnumFacing.EAST;
	    	case Back : return EnumFacing.WEST;
	    	case Top : return EnumFacing.UP;
	    	case Bottom : return EnumFacing.DOWN;
	    	case Right : return EnumFacing.SOUTH;
	    	case Left : return EnumFacing.NORTH;
        	}
    	case WEST:
        	switch(side) {
	    	case Front : return EnumFacing.WEST;
	    	case Back : return EnumFacing.EAST;
	    	case Top : return EnumFacing.UP;
	    	case Bottom : return EnumFacing.DOWN;
	    	case Right : return EnumFacing.NORTH;
	    	case Left : return EnumFacing.SOUTH;
        	}
    	default:
        	switch(side) {
	    	case Front : return EnumFacing.NORTH;
	    	case Back : return EnumFacing.SOUTH;
	    	case Top : return EnumFacing.UP;
	    	case Bottom : return EnumFacing.DOWN;
	    	case Right : return EnumFacing.EAST;
	    	case Left : return EnumFacing.WEST;
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
	/** From Slot to Slot */
	public void moveItem(ItemStackHandler fromInv, int fromSlot, ItemStackHandler toInv, int toSlot) {
		if(fromInv.getStackInSlot(fromSlot) != null) {
			ItemStack extractedItem = fromInv.extractItem(fromSlot, 1, false);
			if(extractedItem != null) {
				toInv.insertItem(toSlot, extractedItem, false);
			}
		}
	}

    public TileEntity getTEFromBlockSide(OldSidePicker.BlockSide blockSide, EnumFacing facing) {
    	OldSidePicker.Side side = OldSidePicker.getSideFromBlockSide(blockSide, facing);
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
    public int getBlockXCoord(OldSidePicker.BlockSide blockSide, EnumFacing facing) {
    	OldSidePicker.Side side = OldSidePicker.getSideFromBlockSide(blockSide, facing);
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
    public int getBlockZCoord(OldSidePicker.BlockSide blockSide, EnumFacing facing) {
    	OldSidePicker.Side side = OldSidePicker.getSideFromBlockSide(blockSide, facing);
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
    public int getBlockYCoord(OldSidePicker.BlockSide blockSide, EnumFacing facing) {
    	OldSidePicker.Side side = OldSidePicker.getSideFromBlockSide(blockSide, facing);
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
    public void outputItem(int fromSlot, OldSidePicker.BlockSide blockSide, int startSlot, boolean backwards) {
		if (getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Output || getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Disabled) {
			ItemStack stack = SLOTS_OUTPUT.getStackInSlot(fromSlot);
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
										if (SLOTS_OUTPUT.getStackInSlot(fromSlot).stackSize <= 0) {
											SLOTS_OUTPUT.setStackInSlot(fromSlot, null);
										}
										itemstack1.stackSize = l;
										flag1 = true;
									} else if (itemstack1.stackSize < stack.getMaxStackSize()) {
										stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
										if (SLOTS_OUTPUT.getStackInSlot(fromSlot).stackSize <= 0) {
											SLOTS_OUTPUT.setStackInSlot(fromSlot, null);
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
									SLOTS_OUTPUT.setStackInSlot(fromSlot, null);
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
	public void inputItem(int inputSlot, OldSidePicker.BlockSide blockSide, int startSlot) {
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
								if (canSlotAcceptItemstackIgnoringCount(stack, SLOTS_INPUT.getStackInSlot(inputSlot))) {				
									if(SLOTS_INPUT.getStackInSlot(inputSlot) == null && stack != null) {
										SLOTS_INPUT.setStackInSlot(inputSlot, stack.copy());
										inv.setInventorySlotContents(currentTESlot, null);
									}else{
										int i = SLOTS_INPUT.getStackInSlot(inputSlot).stackSize + stack.stackSize;
										
										if(i > SLOTS_INPUT.getStackInSlot(inputSlot).getMaxStackSize()) {
											stack.stackSize -= stack.getMaxStackSize() - SLOTS_INPUT.getStackInSlot(inputSlot).stackSize;
											SLOTS_INPUT.getStackInSlot(inputSlot).stackSize = stack.getMaxStackSize();
										}else if(i <= SLOTS_INPUT.getStackInSlot(inputSlot).getMaxStackSize()){
											SLOTS_INPUT.getStackInSlot(inputSlot).stackSize = i;
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
	public void inputFunction() {
		if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0) {
			Random randomGenerator = new Random();
			int slot = randomGenerator.nextInt(SLOTS_INPUT.getSlots());
			int rand = randomGenerator.nextInt(5);
			IN_TIMER++;
			if(IN_TIMER>0) {
				if(IN_TIMER >= IN_TIME) {
					switch(rand) {
					case 0: inputItem(slot, OldSidePicker.BlockSide.Top, 0);
							break;
					case 1: inputItem(slot, OldSidePicker.BlockSide.Bottom, 0);
							break;
					case 2: inputItem(slot, OldSidePicker.BlockSide.Right, 0);
							break;
					case 3: inputItem(slot, OldSidePicker.BlockSide.Left, 0);
							break;
					case 4: inputItem(slot, OldSidePicker.BlockSide.Back, 0);
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
			if(SLOTS_OUTPUT.getStackInSlot(slot) != null) {
				OUT_TIMER++;
				if(OUT_TIMER>0) {
					int rand = randomGenerator.nextInt(5);
				if(OUT_TIMER >= OUT_TIME) {
					switch(rand) {
					case 0: outputItem(slot, OldSidePicker.BlockSide.Top, 0, false);
							break;
					case 1: outputItem(slot, OldSidePicker.BlockSide.Bottom, 0, false);
							break;
					case 2: outputItem(slot, OldSidePicker.BlockSide.Right, 0, false);
							break;
					case 3: outputItem(slot, OldSidePicker.BlockSide.Left, 0, false);
							break;
					case 4: outputItem(slot, OldSidePicker.BlockSide.Back, 0, false);
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
	
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(SIDE_MODES[facing.ordinal()] == Mode.Disabled) {
       		System.out.println(facing);
    		return false;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
       	if(SIDE_MODES[facing.ordinal()] == Mode.Disabled) {
    		return null;
    	}
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
		if(side == 3) {
			SIDE_MODES[3] = SideModeList.Mode.Disabled;
			System.out.println(SIDE_MODES[3]);
		}
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
	public void sync() {
		IMessage msg = new PacketMachineSync( this);
		PacketHandler.net.sendToServer(msg);
	}
}
