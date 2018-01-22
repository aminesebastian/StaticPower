package theking530.staticpower.tileentity;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import theking530.staticpower.utils.InventoryUtilities;
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
	public RedstoneMode REDSTONE_MODE = RedstoneMode.Ignore;

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
		if(REDSTONE_MODE == RedstoneMode.Ignore) {
			if(SLOTS_OUTPUT != null && SLOTS_OUTPUT.getSlots() > 0) {
				outputFunction();	
			}
			if(SLOTS_INPUT != null && SLOTS_INPUT.getSlots() > 0) {
				inputFunction();				
			}
			process();
		}
		if(REDSTONE_MODE == RedstoneMode.Low) {
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
		if(REDSTONE_MODE == RedstoneMode.High) {
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
        REDSTONE_MODE = RedstoneMode.getModeFromInt(nbt.getShort("REDSTONE_MODE"));
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
		nbt.setShort("REDSTONE_MODE", (short)REDSTONE_MODE.ordinal());
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
        REDSTONE_MODE = RedstoneMode.getModeFromInt(nbt.getShort("REDSTONE_MODE"));
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
		toInv.insertItem(toSlot, fromInv.extractItem(fromSlot, 1, false), false);
	}

    public RedstoneMode getRedstoneMode() {
    	return REDSTONE_MODE;
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
    public void outputItem(int fromSlot, theking530.staticpower.utils.SideUtils.BlockSide blockSide, int startSlot, boolean backwards) {
		if (getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Output) {
			ItemStack stack = SLOTS_OUTPUT.getStackInSlot(fromSlot);
			if (!stack.isEmpty()) {
				EnumFacing facing = getWorld().getBlockState(pos).getValue(BlockHorizontal.FACING);
				TileEntity te = getWorld().getTileEntity(pos.offset(SideUtils.getEnumFacingFromSide(blockSide, facing)));
				if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing))) {
					IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing));
					SLOTS_OUTPUT.setStackInSlot(fromSlot, InventoryUtilities.insertItemIntoInventory(inv, stack));
				}
			}
		}
    }
	public void inputItem(int inputSlot, BlockSide blockSide, int startSlot) {
		if (getSideModeFromBlockSide(blockSide) == SideModeList.Mode.Input) {
			EnumFacing facing = getWorld().getBlockState(pos).getValue(BlockHorizontal.FACING);
			TileEntity te = getWorld().getTileEntity(pos.offset(SideUtils.getEnumFacingFromSide(blockSide, facing)));
			if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing))) {
				IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtils.getEnumFacingFromSide(blockSide, facing));
				for(int currentTESlot = startSlot; currentTESlot < inv.getSlots(); currentTESlot++) {
					ItemStack stack = inv.getStackInSlot(currentTESlot);
					if(stack != null && hasResult(stack)) {
						if (canSlotAcceptItemstackIgnoringCount(stack, SLOTS_INPUT.getStackInSlot(inputSlot))) {				
							ItemStack returnedStack = SLOTS_INPUT.insertItem(inputSlot, stack, false);
							inv.extractItem(currentTESlot, stack.getCount() - returnedStack.getCount(), false);																	
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
