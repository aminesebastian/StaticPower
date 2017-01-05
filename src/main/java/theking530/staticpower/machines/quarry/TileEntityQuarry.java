package theking530.staticpower.machines.quarry;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseQuarryingUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.power.StaticEnergyStorage;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.InventoryUtilities;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.WorldUtilities;

public class TileEntityQuarry extends BaseMachineWithTank {
	
	public BlockPos STARTING_COORD;
	public BlockPos ENDING_COORD;
	public BlockPos CURRENT_COORD;
	
	public int INITIAL_BLOCKS_PER_TICK = 1;
	public int BLOCKS_PER_TICK = INITIAL_BLOCKS_PER_TICK;
	
	private ArrayList<ItemStack> QUARRIED_STACKS = new ArrayList();
	public DrainToBucketComponent DRAIN_COMPONENT;
	
	private boolean testing = false;
	
	public TileEntityQuarry() {
		initializeBaseMachineWithTank(2, 100, 100000, 1000, 10, 1, 1, 1, 10000);
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", SLOTS_INPUT, 0, SLOTS_OUTPUT, 0, this, TANK, FLUID_TO_CONTAINER_RATE);
		DRAIN_COMPONENT.setMode(FluidContainerInteractionMode.FillFromContainer);
	}
	@Override
	public void process(){
		if(!worldObj.isRemote) {
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
					if(PROCESSING_TIMER >= PROCESSING_TIME && QUARRIED_STACKS.size() <= 0 && STORAGE.getEnergyStored() >= PROCESSING_ENERGY_MULT*100) {
						for(int i=0; i<BLOCKS_PER_TICK; i++) {
							incrementPosition();
							QUARRIED_STACKS.addAll(getCurrentBlockDrops());
							mineBlock();
							STORAGE.extractEnergy(getProcessingCost(), false);
							if(getFortuneMultiplier() > 0) {
								TANK.drain(1, true);
							}
						}
						updateBlock();
						PROCESSING_TIMER = 0;		
					}else{
						PROCESSING_TIMER++;
					}		
				}	
			}
			if(QUARRIED_STACKS.size() > 0) {
				if(worldObj.getTileEntity(pos.add(0,1,0)) != null && worldObj.getTileEntity(pos.add(0,1,0)).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
					for(int k=QUARRIED_STACKS.size()-1; k>=0; k--) {
						if(getInternalStack(0) != null && getInternalStack(0).getItem() instanceof ItemFilter) {
							ItemFilter tempFilter = (ItemFilter)getInternalStack(0).getItem();
							if(tempFilter.evaluateFilter(getInternalStack(0), QUARRIED_STACKS.get(k))) {
								QUARRIED_STACKS.remove(k);
							}else{
								if(InventoryUtilities.canFullyInsertItemIntoInventory(worldObj.getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k))) {
									InventoryUtilities.fullyInsertItemIntoInventory(worldObj.getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k));
									QUARRIED_STACKS.remove(k);
								}	
							}
						}else{
							if(InventoryUtilities.canFullyInsertItemIntoInventory(worldObj.getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k))) {
								InventoryUtilities.fullyInsertItemIntoInventory(worldObj.getTileEntity(pos.add(0,1,0)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), QUARRIED_STACKS.get(k));
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
			if(SLOTS_UPGRADES.getStackInSlot(i) != null) {
				if(SLOTS_UPGRADES.getStackInSlot(i).getItem() instanceof BaseQuarryingUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseQuarryingUpgrade tempUpgrade = (BaseQuarryingUpgrade) SLOTS_UPGRADES.getStackInSlot(slot).getItem();
			BLOCKS_PER_TICK = (int) tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 0);
			PROCESSING_ENERGY_MULT = (int) (INITIAL_PROCESSING_ENERGY_MULT*tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 1));
		}else{
			BLOCKS_PER_TICK = INITIAL_BLOCKS_PER_TICK;
			PROCESSING_ENERGY_MULT = INITIAL_PROCESSING_ENERGY_MULT;
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
	private void mineBlock() {
		if(worldObj.getBlockState(CURRENT_COORD).getBlock() != Blocks.BEDROCK 
				&& worldObj.getBlockState(CURRENT_COORD).getBlock() != Blocks.AIR
				&& worldObj.getBlockState(CURRENT_COORD).getBlock() != ModBlocks.Quarry) {
			worldObj.playSound(CURRENT_COORD.getX(), CURRENT_COORD.getY(), CURRENT_COORD.getZ(), worldObj.getBlockState(CURRENT_COORD).getBlock().getSoundType().getBreakSound(), 
					SoundCategory.BLOCKS, 0.5F, 1.0F, false);
			worldObj.setBlockToAir(CURRENT_COORD);		
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
    private BlockPos getLocalStartingPos() {
		return new BlockPos(0,0,0);
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
		if (worldObj.isRemote && isAbleToMine()) { 
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, STARTING_COORD.getX() + 0.5D, STARTING_COORD.getY() + 1.0D, 
					STARTING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, ENDING_COORD.getX() + 0.5D, ENDING_COORD.getY() + 1.0D, 
					ENDING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, STARTING_COORD.getX() + 0.5D, STARTING_COORD.getY() + 1.0D, 
					ENDING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, ENDING_COORD.getX() + 0.5D, ENDING_COORD.getY() + 1.0D, 
					STARTING_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}
	public List<ItemStack> getCurrentBlockDrops() {
		if(worldObj.isRemote) {
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, CURRENT_COORD.getX() + 0.5D, CURRENT_COORD.getY() + 1.0D, 
				CURRENT_COORD.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		}
		return worldObj.getBlockState(CURRENT_COORD).getBlock().getDrops(worldObj, CURRENT_COORD, worldObj.getBlockState(CURRENT_COORD), getFortuneMultiplier());
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
