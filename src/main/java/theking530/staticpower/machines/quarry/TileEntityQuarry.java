package theking530.staticpower.machines.quarry;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BaseQuarryingUpgrade;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.utils.InventoryUtilities;
import theking530.staticpower.utils.WorldUtilities;

public class TileEntityQuarry extends BaseMachineWithTank {
	
	public BlockPos STARTING_COORD;
	public BlockPos ENDING_COORD;
	public BlockPos CURRENT_COORD;
	
	public int INITIAL_BLOCKS_PER_TICK = 1;
	public int BLOCKS_PER_TICK = INITIAL_BLOCKS_PER_TICK;
	
	private ArrayList<ItemStack> QUARRIED_STACKS = new ArrayList<ItemStack>();
	public DrainToBucketComponent DRAIN_COMPONENT;
	
	private boolean testing = false;
	
	public TileEntityQuarry() {
		initializeBaseMachineWithTank(2, 100, 100000, 1000, 10, 1, 1, 1, 10000);
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", slotsInput, 0, slotsOutput, 0, this, TANK, FLUID_TO_CONTAINER_RATE);
		DRAIN_COMPONENT.setMode(FluidContainerInteractionMode.FillFromContainer);
	}
	@Override
	public void process(){
		if(!getWorld().isRemote) {
			DRAIN_COMPONENT.update();
			if(testing) {
				STARTING_COORD = pos.offset(EnumFacing.SOUTH, 5);	
				STARTING_COORD = STARTING_COORD.offset(EnumFacing.WEST, 1);	
				ENDING_COORD = pos.offset(EnumFacing.NORTH, 5);
				ENDING_COORD = ENDING_COORD.offset(EnumFacing.WEST, 10);
				CURRENT_COORD = new BlockPos(STARTING_COORD.getX(), getInitialYCoordinate(), STARTING_COORD.getZ());
				testing = false;
			}
			if(isAbleToMine()) {
				drawStartingAndEndingCoords();
				if(!isDoneMining()) {
					if(processingTimer >= processingTime && QUARRIED_STACKS.size() <= 0 && energyStorage.getEnergyStored() >= processingEnergyMult*100) {
						for(int i=0; i<BLOCKS_PER_TICK; i++) {
							incrementPosition();
							QUARRIED_STACKS.addAll(getCurrentBlockDrops());
							mineBlock();
							energyStorage.extractEnergy(getProcessingCost(), false);
							if(getFortuneMultiplier() > 0) {
								TANK.drain(1, true);
							}
						}
						updateBlock();
						processingTimer = 0;		
					}else{
						processingTimer++;
					}		
				}	
			}
			if(QUARRIED_STACKS.size() > 0) {
				if(getWorld().getTileEntity(pos.add(0,1,0)) != null && getWorld().getTileEntity(pos.add(0,1,0)).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
					for(int k=QUARRIED_STACKS.size()-1; k>=0; k--) {
						if(getInternalStack(0) != ItemStack.EMPTY && getInternalStack(0).getItem() instanceof ItemFilter) {
							ItemFilter tempFilter = (ItemFilter)getInternalStack(0).getItem();
							if(tempFilter.evaluateFilter(getInternalStack(0), QUARRIED_STACKS.get(k))) {
								QUARRIED_STACKS.remove(k);
							}else{
								if(InventoryUtilities.canFullyInsertItemIntoInventory(getWorld().getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k))) {
									InventoryUtilities.fullyInsertItemIntoInventory(getWorld().getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k));
									QUARRIED_STACKS.remove(k);
								}	
							}
						}else{
							if(InventoryUtilities.canFullyInsertItemIntoInventory(getWorld().getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k))) {
								InventoryUtilities.fullyInsertItemIntoInventory(getWorld().getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k));
								QUARRIED_STACKS.remove(k);
							}
						}
					}	
				}
			}
		}
	}	
	public void upgradeHandler(){
		quarryingUpgrade();
		super.upgradeHandler();
	}
	public void quarryingUpgrade() {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(slotsUpgrades.getStackInSlot(i) != null) {
				if(slotsUpgrades.getStackInSlot(i).getItem() instanceof BaseQuarryingUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseQuarryingUpgrade tempUpgrade = (BaseQuarryingUpgrade) slotsUpgrades.getStackInSlot(slot).getItem();
			BLOCKS_PER_TICK = (int) tempUpgrade.getMultiplier(slotsUpgrades.getStackInSlot(slot), 0);
			processingEnergyMult = (int) (initialProcessingEnergyMult*tempUpgrade.getMultiplier(slotsUpgrades.getStackInSlot(slot), 1));
		}else{
			BLOCKS_PER_TICK = INITIAL_BLOCKS_PER_TICK;
			processingEnergyMult = initialProcessingEnergyMult;
		}
	}
	private void incrementPosition() {
		if(CURRENT_COORD.getY() <= 0) {
			return;
		}
		if(CURRENT_COORD == ENDING_COORD) {
			return;
		}
		if(getAdjustedCurrentPos().getX() >= getAdjustedEndingPos().getX()-1) {
			CURRENT_COORD = new BlockPos(STARTING_COORD.getX(), CURRENT_COORD.getY(), CURRENT_COORD.getZ() + getZDirection());
			//System.out.println("Incrementing Z");
		}
		if(getAdjustedCurrentPos().getZ() >= getAdjustedEndingPos().getZ()) {
			CURRENT_COORD = new BlockPos(CURRENT_COORD.getX(), CURRENT_COORD.getY()-1, STARTING_COORD.getZ()+getZDirection());
			//System.out.println("Incrementing Y");
		}
		if(getAdjustedCurrentPos().getX() < getAdjustedEndingPos().getX()){
			CURRENT_COORD = CURRENT_COORD.add(getXDirection(), 0, 0);
			//System.out.println("Incrementing X");
			return;
		}
	}
	@SuppressWarnings("deprecation")
	private void mineBlock() {
		if(getWorld().getBlockState(CURRENT_COORD).getBlock() != Blocks.BEDROCK 
				&& getWorld().getBlockState(CURRENT_COORD).getBlock() != Blocks.AIR
				&& getWorld().getBlockState(CURRENT_COORD).getBlock() != ModBlocks.Quarry) {
			getWorld().playSound(CURRENT_COORD.getX(), CURRENT_COORD.getY(), CURRENT_COORD.getZ(), getWorld().getBlockState(CURRENT_COORD).getBlock().getSoundType().getBreakSound(), 
					SoundCategory.BLOCKS, 0.5F, 1.0F, false);
			getWorld().setBlockToAir(CURRENT_COORD);		
		}
	}
	public void setCoordinates(BlockPos starting, BlockPos ending) {
		STARTING_COORD = starting;
		CURRENT_COORD = STARTING_COORD;
		ENDING_COORD = ending;
	}
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);      
        CURRENT_COORD = WorldUtilities.readBlockPosFromNBT(nbt, "CURRENT");
        ENDING_COORD = WorldUtilities.readBlockPosFromNBT(nbt, "ENDING");
        STARTING_COORD = WorldUtilities.readBlockPosFromNBT(nbt, "STARTING");
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if(isAbleToMine()) {
        	WorldUtilities.writeBlockPosToNBT(nbt, CURRENT_COORD, "CURRENT");   	
        	WorldUtilities.writeBlockPosToNBT(nbt, ENDING_COORD, "ENDING");
        	WorldUtilities.writeBlockPosToNBT(nbt, STARTING_COORD, "STARTING");
        }
		return nbt;	
	}
    public boolean isAbleToMine(){
    	if(STARTING_COORD != null && CURRENT_COORD != null && ENDING_COORD != null) {
        	if(STARTING_COORD.getY() == 0 && STARTING_COORD.getX() == 0 && STARTING_COORD.getZ() == 0) {
        		return false;
        	}	
    		return true;
    	}
    	return false;
    }
	public boolean isDoneMining() {
		if(CURRENT_COORD != null) {
			if(CURRENT_COORD.getY() == 0) {
				return true;
			}	
		}
		return false;
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
    private BlockPos getAdjustedEndingPos() {
		BlockPos temp1 = ENDING_COORD.subtract(STARTING_COORD);
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.abs(temp1.getY()), Math.abs(temp1.getZ()));
		return absPos;
	}
	private BlockPos getAdjustedCurrentPos() {
		BlockPos temp1 = CURRENT_COORD.subtract(STARTING_COORD);
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.abs(temp1.getY()), Math.abs(temp1.getZ()));
		return absPos;
	}
	private int getInitialYCoordinate() {
		return Math.max(STARTING_COORD.getY(), ENDING_COORD.getY());
	}
	private int getXDirection(){
		if(STARTING_COORD.getX() > ENDING_COORD.getX()) {
			return -1;
		}else{
			return 1;
		}
	}
	private int getZDirection(){
		if(STARTING_COORD.getZ() > ENDING_COORD.getZ()) {
			return -1;
		}else{
			return 1;
		}
	}
	private void drawStartingAndEndingCoords() {
		if (getWorld().isRemote && isAbleToMine()) { 
			getWorld().spawnParticle(EnumParticleTypes.REDSTONE, STARTING_COORD.getX() + 0.5D, STARTING_COORD.getY() + 1.0D, 
					STARTING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			getWorld().spawnParticle(EnumParticleTypes.REDSTONE, ENDING_COORD.getX() + 0.5D, ENDING_COORD.getY() + 1.0D, 
					ENDING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			
			getWorld().spawnParticle(EnumParticleTypes.REDSTONE, STARTING_COORD.getX() + 0.5D, STARTING_COORD.getY() + 1.0D, 
					ENDING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			getWorld().spawnParticle(EnumParticleTypes.REDSTONE, ENDING_COORD.getX() + 0.5D, ENDING_COORD.getY() + 1.0D, 
					STARTING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}
	public List<ItemStack> getCurrentBlockDrops() {
		if(getWorld().isRemote) {
			getWorld().spawnParticle(EnumParticleTypes.REDSTONE, CURRENT_COORD.getX() + 0.5D, CURRENT_COORD.getY() + 1.0D, 
				CURRENT_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		}
        NonNullList<ItemStack> ret = NonNullList.create();
		getWorld().getBlockState(CURRENT_COORD).getBlock().getDrops(ret, getWorld(), CURRENT_COORD, getWorld().getBlockState(CURRENT_COORD), 0);
		return ret;
	}

	@Override
	public String getName() {
		return "Quarry";
	}
	public int getFortuneMultiplier() {
		if(TANK.getFluid() != null) {
			if(TANK.getFluid().getFluid() == ModFluids.StaticFluid) {
				return 1;
			}
			if(TANK.getFluid().getFluid() == ModFluids.EnergizedFluid) {
				return 2;
			}
			if(TANK.getFluid().getFluid() == ModFluids.LumumFluid) {
				return 3;
			}
		}
		return 0;
	}
}
