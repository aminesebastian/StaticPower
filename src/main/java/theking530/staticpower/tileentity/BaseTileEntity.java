package theking530.staticpower.tileentity;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.machines.machinecomponents.IMachineComponentInterface;
import theking530.staticpower.utils.InventoryUtilities;
import theking530.staticpower.utils.RedstoneModeList.RedstoneMode;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.SideModeList.Mode;
import theking530.staticpower.utils.SideUtilities;
import theking530.staticpower.utils.SideUtilities.BlockSide;

public class BaseTileEntity extends TileEntity implements ITickable, IRedstoneConfigurable, ISideConfigurable {

	public ItemStackHandler  slotsInput;
	public ItemStackHandler  slotsOutput;
	public ItemStackHandler  slotsInternal;
	public ItemStackHandler  slotsUpgrades;
	
	public Random randomGenerator = new Random();
	
	private RedstoneMode redstoneMode = RedstoneMode.Ignore;

	private SideModeList.Mode[] sideModes = {Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular};
	public int inputTimer = 0;
	public int inputTime = 2;
	public int outputTimer = 0;
	public int outputTime = 2;
	public int updateTimer = 0;
	public int updateTime = 10;
	
	public boolean updateQueued = false;
	public ArrayList<IMachineComponentInterface> machineComponents;
	public boolean wasWrenchedDoNotBreak = false;
	
	public boolean disableFaceInteraction;
	
	public BaseTileEntity() {

	}
	public void initializeBasicTileEntity(int internalSlots, int inputSlots, int outputSlots, boolean disableFaceInteraction) {
		slotsInput = new ItemStackHandler(inputSlots);
		slotsOutput = new ItemStackHandler(outputSlots);
		slotsInternal = new ItemStackHandler(internalSlots);
		slotsUpgrades = new ItemStackHandler(3);
		machineComponents = new ArrayList<IMachineComponentInterface>();
		this.disableFaceInteraction = disableFaceInteraction;
	}
	public void initializeBasicTileEntity(int internalSlots, int inputSlots, int outputSlots) {
		initializeBasicTileEntity(internalSlots, inputSlots, outputSlots, true);
	}
	@Override
	public void update() {
		int redstoneSignal = getWorld().getRedstonePower(pos, EnumFacing.NORTH);
		if(redstoneMode == RedstoneMode.Ignore) {
			if(slotsOutput != null && slotsOutput.getSlots() > 0) {
				outputFunction();	
			}
			if(slotsInput != null && slotsInput.getSlots() > 0) {
				inputFunction();				
			}
			process();
		}
		if(redstoneMode == RedstoneMode.Low) {
			if(redstoneSignal == 0) {
				if(slotsOutput != null && slotsOutput.getSlots() > 0) {
					outputFunction();	
				}
				if(slotsInput != null && slotsInput.getSlots() > 0) {
					inputFunction();				
				}
				process();
			}
		}
		if(redstoneMode == RedstoneMode.High) {
			if(redstoneSignal > 0) {
				if(slotsOutput != null && slotsOutput.getSlots() > 0) {
					outputFunction();	
				}
				if(slotsInput != null && slotsInput != null && slotsInput.getSlots() > 0) {
					inputFunction();				
				}
				process();
			}
		}	
		if(updateTimer < updateTime) {
			updateTimer++;
		}else{
			if(updateQueued) {
				updateBlock();
			}
			markDirty();
			updateTimer = 0;
		}
	}		
	public void process() {
		
	}
	
	public ItemStack getInputStack(int slot) {
		if(slot < slotsInput.getSlots()) {
			return slotsInput.getStackInSlot(slot);	
		}else{
			return ItemStack.EMPTY;
		}
	}
	public ItemStack getOutputStack(int slot) {
		if(slot < slotsOutput.getSlots()) {
			return slotsOutput.getStackInSlot(slot);	
		}else{
			return ItemStack.EMPTY;
		}
	}
	public ItemStack getInternalStack(int slot) {
		if(slot < slotsInternal.getSlots()) {
			return slotsInternal.getStackInSlot(slot);	
		}else{
			return ItemStack.EMPTY;
		}
	}
	
	public void setInternalStack(int slot, ItemStack stack) {
		slotsInternal.setStackInSlot(slot, stack);
	}

	@Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        redstoneMode = RedstoneMode.getModeFromInt(nbt.getShort("REDSTONE_MODE"));
        for(int i=0; i<6; i++) {
        	sideModes[i] = SideModeList.Mode.values()[nbt.getInteger("SIDEMODE" + i)];
        }      
        if(slotsInput != null && slotsInput.getSlots() > 0 && nbt.hasKey("INPUTS")) {
        	slotsInput.deserializeNBT((NBTTagCompound) nbt.getTag("INPUTS"));	
        }
        if(slotsOutput != null && slotsOutput.getSlots() > 0 && nbt.hasKey("OUTPUTS")) {
            slotsOutput.deserializeNBT((NBTTagCompound) nbt.getTag("OUTPUTS"));
        }
        if(slotsInternal != null && slotsInternal.getSlots() > 0 && nbt.hasKey("INTERNAL")) {
            slotsInternal.deserializeNBT((NBTTagCompound) nbt.getTag("INTERNAL"));
        }
        if(slotsUpgrades != null && slotsUpgrades.getSlots() > 0 && nbt.hasKey("UPGRADES")) {
            slotsUpgrades.deserializeNBT((NBTTagCompound) nbt.getTag("UPGRADES"));
        }
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		nbt.setShort("REDSTONE_MODE", (short)redstoneMode.ordinal());
        for(int i=0; i<6; i++) {
        	nbt.setInteger("SIDEMODE" + i, sideModes[i].ordinal());
        }
        if(slotsInput != null && slotsInput.getSlots() > 0) {
        	nbt.setTag("INPUTS", slotsInput.serializeNBT());	
        }
        if(slotsOutput != null && slotsOutput.getSlots() > 0) {
        	nbt.setTag("OUTPUTS", slotsOutput.serializeNBT());
        }
        if(slotsInternal != null && slotsInternal.getSlots() > 0) {
        	nbt.setTag("INTERNAL", slotsInternal.serializeNBT());
        }
        if(slotsUpgrades != null && slotsUpgrades.getSlots() > 0) {
        	nbt.setTag("UPGRADES", slotsUpgrades.serializeNBT());
        }
        nbt.setBoolean("PLACED", true);
		return nbt;	
	}
	public NBTTagCompound onMachineBroken(NBTTagCompound nbt) {
		writeToNBT(nbt);
    	return nbt;
	}
	public void onMachinePlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        redstoneMode = RedstoneMode.getModeFromInt(nbt.getShort("REDSTONE_MODE"));
        for(int i=0; i<6; i++) {
        	sideModes[i] = SideModeList.Mode.values()[nbt.getInteger("SIDEMODE" + i)];
        }      
        if(slotsInput != null && slotsInput.getSlots() > 0) {
        	slotsInput.deserializeNBT((NBTTagCompound) nbt.getTag("INPUTS"));	
        }
        if(slotsOutput != null && slotsOutput.getSlots() > 0) {
            slotsOutput.deserializeNBT((NBTTagCompound) nbt.getTag("OUTPUTS"));
        }
        if(slotsInternal != null && slotsInternal.getSlots() > 0) {
            slotsInternal.deserializeNBT((NBTTagCompound) nbt.getTag("INTERNAL"));
        }
        if(slotsUpgrades != null && slotsUpgrades.getSlots() > 0) {
            slotsUpgrades.deserializeNBT((NBTTagCompound) nbt.getTag("UPGRADES"));
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
	
    /** From Slot to Slot */
	public void moveItem(ItemStackHandler fromInv, int fromSlot, ItemStackHandler toInv, int toSlot) {
		toInv.insertItem(toSlot, fromInv.extractItem(fromSlot, 1, false), false);
	}
    public boolean hasResult(ItemStack stack) {
    	return true;
    }


    public EnumFacing getFacingDirection() {
    	if(getWorld().getBlockState(pos).getProperties().containsKey(BlockHorizontal.FACING)) {
        	return getWorld().getBlockState(getPos()).getValue(BlockHorizontal.FACING);
    	}else{
        	return EnumFacing.UP;
    	}
    }
    public void outputItem(int fromSlot, theking530.staticpower.utils.SideUtilities.BlockSide blockSide, int startSlot, boolean backwards) {
		if (getSideConfiguration(blockSide) == SideModeList.Mode.Output) {
			ItemStack stack = slotsOutput.getStackInSlot(fromSlot);
			if (!stack.isEmpty()) {
				EnumFacing facing = getWorld().getBlockState(pos).getValue(BlockHorizontal.FACING);
				TileEntity te = getWorld().getTileEntity(pos.offset(SideUtilities.getEnumFacingFromSide(blockSide, facing)));
				if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtilities.getEnumFacingFromSide(blockSide, facing))) {
					IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtilities.getEnumFacingFromSide(blockSide, facing));
					slotsOutput.setStackInSlot(fromSlot, InventoryUtilities.insertItemIntoInventory(inv, stack));
				}
			}
		}
    }
	public void inputItem(int inputSlot, BlockSide blockSide, int startSlot) {
		if (getSideConfiguration(blockSide) == SideModeList.Mode.Input) {
			EnumFacing facing = getWorld().getBlockState(pos).getValue(BlockHorizontal.FACING);
			TileEntity te = getWorld().getTileEntity(pos.offset(SideUtilities.getEnumFacingFromSide(blockSide, facing)));
			if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtilities.getEnumFacingFromSide(blockSide, facing))) {
				IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtilities.getEnumFacingFromSide(blockSide, facing));
				for(int currentTESlot = startSlot; currentTESlot < inv.getSlots(); currentTESlot++) {
					ItemStack stack = inv.getStackInSlot(currentTESlot);
					if(stack != null && hasResult(stack)) {
						if (ItemStack.areItemsEqual(stack, slotsInput.getStackInSlot(inputSlot))) {				
							ItemStack returnedStack = slotsInput.insertItem(inputSlot, stack, false);
							inv.extractItem(currentTESlot, stack.getCount() - returnedStack.getCount(), false);																	
						}
					}
				}		
			}
		}					
	}	
	public void inputFunction() {
		if(slotsInput != null && slotsInput.getSlots() > 0 && getWorld().getBlockState(pos) != null && getWorld().getBlockState(pos) != Blocks.AIR) {
			Random randomGenerator = new Random();
			int slot = randomGenerator.nextInt(slotsInput.getSlots());
			int rand = randomGenerator.nextInt(5);
			inputTimer++;
			if(inputTimer>0) {
				if(inputTimer >= inputTime) {
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
				inputTimer = 0;
				}
			}	
		}
	}
	public void outputFunction() {
		if(slotsOutput != null && slotsOutput.getSlots() > 0) {
			Random randomGenerator = new Random();
			int slot = randomGenerator.nextInt(slotsOutput.getSlots());
			if(slotsOutput.getStackInSlot(slot) != ItemStack.EMPTY) {
				outputTimer++;
				if(outputTimer>0) {
					int rand = randomGenerator.nextInt(5);
				if(outputTimer >= outputTime) {
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
					outputTimer = 0;
					}
				}
			}
		}
	}

    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(getSideConfiguration(facing) == Mode.Disabled) {
    		return false;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && SideUtilities.getBlockSide(facing, getFacingDirection()) != BlockSide.FRONT) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
    @SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
       	if(getSideConfiguration(facing) == Mode.Disabled || SideUtilities.getBlockSide(facing, getFacingDirection()) == BlockSide.FRONT) {
    		return null;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if(sideModes[facing.ordinal()] == Mode.Output) {
    			return (T) slotsOutput;
    		}else{
    			return (T) slotsInput;
    		}
    	}
    	return super.getCapability(capability, facing);
    }
    
	public void incrementSide(int side){
		if(side == 3 && disableFaceInteraction) {
			sideModes[3] = SideModeList.Mode.Disabled;
		}
		if(sideModes[side].ordinal() >= 3) {
			sideModes[side] = SideModeList.Mode.values()[0];
		}else{
			sideModes[side] = SideModeList.Mode.values()[sideModes[side].ordinal()+1];
		}
		onSidesConfigUpdate();
	}
	public void resetSides(){
		for(int i=0; i<6; i++) {
			sideModes[i] = SideModeList.Mode.values()[0];
		}
		onSidesConfigUpdate();
	}
	public void updateBlock() {
		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 2);
	}
	public void onSidesConfigUpdate() {
		
	}
	
	/*Redstone Control*/
	@Override
    public RedstoneMode getRedstoneMode() {
    	return redstoneMode;
    } 
	@Override
	public void setRedstoneMode(RedstoneMode newMode) {
		redstoneMode = newMode;
	}
	@Override
	public boolean isRedstoneControllable() {
		return true;
	}

	/*Side Control*/
	@Override
	public boolean isSideConfigurable() {
		return true;
	}
	@Override
	public Mode getSideConfiguration(BlockSide side) {
		EnumFacing facing = SideUtilities.getEnumFacingFromSide(side, getFacingDirection());
		return getSideConfiguration(facing);
	}
	@Override
	public Mode getSideConfiguration(EnumFacing facing) {
		return sideModes[facing.ordinal()];
	}
	@Override
	public Mode[] getSideConfigurations() {
		return sideModes;
	}
	@Override
	public void setSideConfiguration(Mode newMode, BlockSide side) {
		EnumFacing facing = SideUtilities.getEnumFacingFromSide(side, getFacingDirection());
		setSideConfiguration(newMode, facing);
	}
	@Override
	public void setSideConfiguration(Mode newMode, EnumFacing facing) {
		sideModes[facing.ordinal()] = newMode;
	}
}
