package theking530.staticpower.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.assists.INameable;
import theking530.staticpower.assists.utilities.RedstoneModeList.RedstoneMode;
import theking530.staticpower.assists.utilities.SideModeList;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.assists.utilities.SideUtilities;
import theking530.staticpower.assists.utilities.SideUtilities.BlockSide;
import theking530.staticpower.machines.tileentitycomponents.ITileEntityComponent;

public class BaseTileEntity extends TileEntity implements ITickable, IRedstoneConfigurable, ISideConfigurable, IUpgradeable, INameable, IBreakSerializeable {

	public ItemStackHandler  slotsInput;
	public ItemStackHandler  slotsOutput;
	public ItemStackHandler  slotsInternal;
	public ItemStackHandler  slotsUpgrades;
	
	private List<ITileEntityComponent> components;	
	private RedstoneMode redstoneMode = RedstoneMode.Ignore;
	private SideConfiguration ioSideConfiguration;

	private int updateTimer = 0;
	private int updateTime = 10;
	
	private boolean updateQueued = false;
	private boolean disableFaceInteraction;
	
	public boolean wasWrenchedDoNotBreak = false;
	
	public BaseTileEntity() {
		ioSideConfiguration = new SideConfiguration();
		components = new ArrayList<ITileEntityComponent>();
	}
	public void initializeSlots(int internalSlots, int inputSlots, int outputSlots, boolean disableFaceInteraction) {
		slotsInput = new ItemStackHandler(inputSlots);
		slotsOutput = new ItemStackHandler(outputSlots);
		slotsInternal = new ItemStackHandler(internalSlots);
		slotsUpgrades = new ItemStackHandler(3);
		this.disableFaceInteraction = disableFaceInteraction;
	}
	public void initializeSlots(int internalSlots, int inputSlots, int outputSlots) {
		initializeSlots(internalSlots, inputSlots, outputSlots, true);
	}
	
	@Override
	public void update() {
		if(evauluateRedstoneSettings()) {
			preProcessUpdateComponents();
			process();
			postProcessUpdateComponents();
		}
		if(updateTimer < updateTime) {
			updateTimer++;
		}else{
			if(updateQueued) {
				//updateBlock();
			}
			markDirty();
			updateTimer = 0;
		}
	}		
	public void process() {}
	public void updateBlock() {
		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 2);
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
        deserializeData(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return serializeData(nbt);
	}
	
    public void deserializeData(NBTTagCompound nbt) {
        if(this.isRedstoneControllable()) {
        	redstoneMode = RedstoneMode.getModeFromInt(nbt.getShort("REDSTONE_MODE"));
        }
		if(isSideConfigurable()) {
	        for(int i=0; i<6; i++) {
	        	setSideConfiguration(SideModeList.Mode.values()[nbt.getInteger("SIDEMODE" + i)], EnumFacing.values()[i]);
	        }    
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
    public NBTTagCompound serializeData(NBTTagCompound nbt) {
        if(this.isRedstoneControllable()) {
    		nbt.setShort("REDSTONE_MODE", (short)redstoneMode.ordinal());	
        }
		if(isSideConfigurable()) {
	        for(int i=0; i<6; i++) {
	        	nbt.setInteger("SIDEMODE" + i, getSideConfiguration(EnumFacing.values()[i]).ordinal());
	        }
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
    
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), getBlockMetadata(), this.getUpdateTag());
	}
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (getWorld().getTileEntity(pos) != this) {
			return false;
		}else{
			return player.getDistanceSq((double)pos.getX() + 0.25D, (double)pos.getY() + 0.25D, (double)pos.getZ() + 0.25D) <= 22;
		}
	}
	public void transferItemInternally(ItemStackHandler fromInv, int fromSlot, ItemStackHandler toInv, int toSlot) {
		toInv.insertItem(toSlot, fromInv.extractItem(fromSlot, 1, false), false);
	}
    public EnumFacing getFacingDirection() {
    	if(getWorld().getBlockState(pos).getProperties().containsKey(BlockHorizontal.FACING)) {
        	return getWorld().getBlockState(getPos()).getValue(BlockHorizontal.FACING);
    	}else{
        	return EnumFacing.UP;
    	}
    }

    /*Nameable*/
	@Override
    public String getName(){
    	return "*ERROR*";
    }
    
    /*Serializeable*/
    public NBTTagCompound serializeOnBroken(NBTTagCompound nbt) {
    	serializeData(nbt);
    	return nbt;
	}
	public void deserializeOnPlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		deserializeData(nbt);
	}	
	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}
	@Override
	public boolean shouldDeserializeWhenPlace(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		return true;
	}
    
	/*Capability Handling*/
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
    	if(facing == null) {
    		return false;
    	}
    	if(getSideConfiguration(facing) == Mode.Disabled) {
    		return false;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && SideUtilities.getBlockSide(facing, getFacingDirection()) != BlockSide.FRONT) {
    		if(getSideConfiguration(facing) == Mode.Output) {
    			if(slotsOutput != null) {
        			return true;
    			}
    		}else{
    			if(slotsInput != null) {
        			return true;
    			}
    		}
    	}
        return super.hasCapability(capability, facing);
    }
    @SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(facing == null) {
    		return null;
    	}
       	if(getSideConfiguration(facing) == Mode.Disabled || SideUtilities.getBlockSide(facing, getFacingDirection()) == BlockSide.FRONT) {
    		return null;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if(getSideConfiguration(facing) == Mode.Output) {
    			if(slotsOutput != null) {
        			return (T) slotsOutput;
    			}
    		}else{
    			if(slotsInput != null) {
        			return (T) slotsInput;
    			}
    		}
    	}
    	return super.getCapability(capability, facing);
    }
    
	/*Upgrade Handling*/
	public ItemStack getUpgrade(Item upgradeBase) {
		ItemStack upgrade = ItemStack.EMPTY;
		for(int i=0; i<slotsUpgrades.getSlots(); i++) {
			upgrade = slotsUpgrades.getStackInSlot(i);
			if(!upgrade.isEmpty()) {
				if(upgrade.getItem().getClass().isInstance(upgradeBase)) {
					return upgrade;
				}
			}
		}
		return ItemStack.EMPTY;
	}
	public boolean hasUpgrade(Item upgradeBase) {
		return !getUpgrade(upgradeBase).isEmpty();
	}
	@Override
	public ItemStack getUpgrade(ItemStack upgrade) {
		for(int i=0; i<slotsUpgrades.getSlots(); i++) {
			if(ItemStack.areItemStacksEqual(ItemHandlerHelper.copyStackWithSize(upgrade, 1), ItemHandlerHelper.copyStackWithSize(slotsUpgrades.getStackInSlot(i), 1))) {
				return slotsUpgrades.getStackInSlot(i);
			}
		}
		return ItemStack.EMPTY;
	}
	@Override
	public boolean hasUpgrade(ItemStack upgrade) {
		return !getUpgrade(upgrade).isEmpty();
	}
	@Override
	public List<ItemStack> getAllUpgrades() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		ItemStack upgrade = ItemStack.EMPTY;
		int slot = -1;
		for(int i=0; i<slotsUpgrades.getSlots(); i++) {
			slot = i;
			upgrade = slotsUpgrades.getStackInSlot(slot);
			if(!upgrade.isEmpty()) {
				list.add(upgrade);
			}
		}
		return list;
	}
	@Override
	public boolean canAcceptUpgrade(ItemStack upgrade) {
		return true;
	}
	
	/*Components*/
	public void registerComponent(ITileEntityComponent component) {
		components.add(component);
	}
	public boolean removeComponents(ITileEntityComponent component) {
		return components.remove(component);
	}
	public List<ITileEntityComponent> getComponents() {
		return components;
	}
	private void preProcessUpdateComponents() {
		for(int i=0; i<components.size(); i++) {
			components.get(i).preProcessUpdate();
		}
	}
	private void postProcessUpdateComponents() {
		for(int i=0; i<components.size(); i++) {
			components.get(i).postProcessUpdate();
		}
	}
	
	/*Redstone Control*/
	@Override
	public boolean isRedstoneControllable() {
		return true;
	}
	@Override
    public RedstoneMode getRedstoneMode() {
    	return redstoneMode;
    } 
	@Override
	public void setRedstoneMode(RedstoneMode newMode) {
		redstoneMode = newMode;
	}
	public boolean evauluateRedstoneSettings() {
		int redstoneSignal = getWorld().getStrongPower(pos);
		if(getRedstoneMode() == RedstoneMode.Ignore) {
			return true;
		}
		if(getRedstoneMode() == RedstoneMode.Low) {
			if(redstoneSignal <= 0) {
				return true;	
			}
		}
		if(getRedstoneMode() == RedstoneMode.High) {
			if(redstoneSignal > 0) {
				return true;	
			}
		}
		return false;
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
		return ioSideConfiguration.getSideConfiguration(facing);
	}
	@Override
	public Mode[] getSideConfigurations() {
		return ioSideConfiguration.getConfiguration();
	}
	@Override
	public void setSideConfiguration(Mode newMode, BlockSide side) {
		EnumFacing facing = SideUtilities.getEnumFacingFromSide(side, getFacingDirection());
		setSideConfiguration(newMode, facing);
	}
	@Override
	public void setSideConfiguration(Mode newMode, EnumFacing facing) {
		ioSideConfiguration.setSideConfiguration(newMode, facing);
	}
	public void onSidesConfigUpdate() {
		
	}
	public void resetSideConfiguration(){
		for(int i=0; i<6; i++) {
			setSideConfiguration(SideModeList.Mode.Disabled, EnumFacing.values()[i]);
		}
		onSidesConfigUpdate();
	}
	@Override
	public void incrementSideConfiguration(EnumFacing side){
		BlockSide blockSideEquiv = SideUtilities.getBlockSide(side, getFacingDirection());
		if(blockSideEquiv == BlockSide.FRONT && disableFaceInteraction) {
			setSideConfiguration(SideModeList.Mode.Disabled, side);
		}
		SideModeList.Mode currentSideMode = getSideConfiguration(side);
		if(currentSideMode == Mode.Disabled) {
			setSideConfiguration(SideModeList.Mode.Regular, side);
		}else{
			setSideConfiguration(SideModeList.Mode.values()[currentSideMode.ordinal()+1], side);
		}
		onSidesConfigUpdate();
		updateBlock();
	}
}