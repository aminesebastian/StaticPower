package theking530.staticpower.machines.basicfarmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityBasicFarmer extends BaseMachineWithTank {

	public int INITIAL_RANGE = 3;
	public int RANGE = INITIAL_RANGE;
	public int BLOCKS_PER_TICK = 2;
	public int FARMING_COST = 100;
	public int GROWTH_BONUS_CHANCE = 50;
	public BlockPos CURRENT_COORD;
	private Random RAND;
	private ArrayList<ItemStack> FARMED_STACKS = new ArrayList();
	
	public TileEntityBasicFarmer() {
		initializeBaseMachineWithTank(2, 40, 100000, 500, 10, 0, 4, 10, 10000);		
		setBatterySlot(3);
		CURRENT_COORD = getStartingCoord();
		RAND = new Random();
	}
	@Override
	public void process(){
		if(TANK.getFluid() != null) {
			if(TANK.getFluid().getFluid() == ModFluids.StaticFluid) {
				GROWTH_BONUS_CHANCE = 50;
			}else if(TANK.getFluid().getFluid() == ModFluids.EnergizedFluid){
				GROWTH_BONUS_CHANCE = 25;
			}else if(TANK.getFluid().getFluid() == ModFluids.LumumFluid){
				GROWTH_BONUS_CHANCE = 15;
			}
		}else{
			GROWTH_BONUS_CHANCE = 100;
		}
		if(PROCESSING_TIMER < PROCESSING_TIME) {
			PROCESSING_TIMER++;
		}else{
			if(FARMED_STACKS.size() <= 0 && STORAGE.getEnergyStored() >= getProcessingCost()) {
				for(int i=0; i<BLOCKS_PER_TICK; i++) {
					if(STORAGE.getEnergyStored() >= getProcessingCost()) {
						incrementPosition();
						STORAGE.extractEnergy(getProcessingCost(), false);
						drain(2, true);
						if(!checkFarmingPlot(CURRENT_COORD)){
							break;
						}
					}else{
						break;
					}
				}
				PROCESSING_TIMER = 0;
			}else{
				for(int k=FARMED_STACKS.size()-1; k>=0; k--) {
					ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(SLOTS_OUTPUT, FARMED_STACKS.get(k), 0, 8);
					if(insertedStack == null) {
						FARMED_STACKS.remove(k);
					}else{
						FARMED_STACKS.set(k, insertedStack);
					}
				}	
			}
		}
	}
	public void upgradeHandler(){
		rangeUpgrade();
		super.upgradeHandler();
	}
	public void rangeUpgrade() {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(SLOTS_UPGRADES.getStackInSlot(i) != null) {
				if(SLOTS_UPGRADES.getStackInSlot(i).getItem() instanceof BaseRangeUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseRangeUpgrade tempUpgrade = (BaseRangeUpgrade) SLOTS_UPGRADES.getStackInSlot(slot).getItem();
			RANGE = (int) (INITIAL_RANGE*tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 0));
		}else{
			RANGE = INITIAL_RANGE;
		}
	}
	private void incrementPosition() {
		if(CURRENT_COORD == getEndingCoord()) {
			CURRENT_COORD = getStartingCoord();
			return;
		}
		if(getAdjustedCurrentPos().getX() >= getAdjustedEndingPos().getX()-1) {
			CURRENT_COORD = new BlockPos(getStartingCoord().getX(), CURRENT_COORD.getY(), CURRENT_COORD.getZ() + getZDirection());
		}
		if(getAdjustedCurrentPos().getZ() >= getAdjustedEndingPos().getZ()+1) {
			CURRENT_COORD = getStartingCoord();
		}
		if(getAdjustedCurrentPos().getX() <= getAdjustedEndingPos().getX()){
			CURRENT_COORD = CURRENT_COORD.add(getXDirection(), 0, 0);
			return;
		}
	}
	private BlockPos getAdjustedCurrentPos() {
		BlockPos temp1 = CURRENT_COORD.subtract(getStartingCoord());
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.abs(temp1.getY()), Math.abs(temp1.getZ()));
		return absPos;
	}
	private BlockPos getAdjustedEndingPos() {
		BlockPos temp1 = getEndingCoord().subtract(getStartingCoord());
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.abs(temp1.getY()), Math.abs(temp1.getZ()));
		return absPos;
	}
	private BlockPos getEndingCoord() {
		return new BlockPos(pos.getX() + RANGE+1, pos.getY(), pos.getZ() + RANGE);
	}
	private BlockPos getStartingCoord() {
		return new BlockPos(pos.getX() - RANGE-1, pos.getY(), pos.getZ() - RANGE);
	}
	private int getXDirection(){
		if(getStartingCoord().getX() > getEndingCoord().getX()) {
			return -1;
		}else{
			return 1;
		}
	}
	private int getZDirection(){
		if(getStartingCoord().getZ() > getEndingCoord().getZ()) {
			return -1;
		}else{
			return 1;
		}
	}
	public boolean canFarm() {
		if(STORAGE.getEnergyStored() >= FARMING_COST && SLOTS_INPUT.getStackInSlot(0) != null && SLOTS_INPUT.getStackInSlot(0).getItem() instanceof ItemHoe) {
			return true;
		}
		return false;
	}
	public void useHoe(){
		if(SLOTS_INPUT.getStackInSlot(0) != null && SLOTS_INPUT.getStackInSlot(0).getItem() instanceof ItemHoe) {
			if(SLOTS_INPUT.getStackInSlot(0).attemptDamageItem(1, RAND)) {
				SLOTS_INPUT.setStackInSlot(0, null);
				worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, 
						SoundCategory.BLOCKS, 1.0F, 1.0F, false);		
			}	
		}
	}
	public boolean checkFarmingPlot(BlockPos pos) {
		if(!canFarm()) {
			return false;
		}
		if(RAND.nextInt(GROWTH_BONUS_CHANCE) == 2) {
	        if(worldObj.getBlockState(pos) != null && worldObj.getBlockState(pos).getBlock() instanceof IGrowable) {
	        	IGrowable tempCrop = (IGrowable) worldObj.getBlockState(pos).getBlock();
	        	if(tempCrop.canGrow(worldObj, pos, worldObj.getBlockState(pos), true)) {
	        		worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + 0.5D, pos.getY() + 1.0D, 
	        				pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
	        		tempCrop.grow(worldObj, RAND, pos, worldObj.getBlockState(pos));
	        	}
	        }	
		}
		worldObj.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5D, pos.getY() + 0.25D, 
				pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		if(worldObj.getBlockState(pos).getBlock() != null) {
			if(worldObj.getBlockState(pos).getBlock() instanceof BlockCrops) {
				BlockCrops tempCrop = (BlockCrops)worldObj.getBlockState(pos).getBlock();
				if(!tempCrop.canGrow(worldObj, pos, worldObj.getBlockState(pos), true)) {
					worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), worldObj.getBlockState(pos).getBlock().getSoundType().getBreakSound(), 
							SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.getY() + 1.0D, 
							pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		        	FARMED_STACKS.addAll(getCurrentBlockDrops());
					worldObj.setBlockState(pos, tempCrop.withAge(0), 2);	
					useHoe();
					if(FARMED_STACKS.size() > 0) {
						for(int i=FARMED_STACKS.size()-1; i>=0; i--) {
							if(FARMED_STACKS.get(i).getItem() instanceof IPlantable) {
								FARMED_STACKS.remove(i);
								break;
							}
						}
					}
				}
			}else if(worldObj.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockReed){	      
				if(worldObj.getBlockState(pos.add(0, 2, 0)).getBlock() instanceof BlockReed) {
					FARMED_STACKS.addAll(worldObj.getBlockState(pos.add(0, 2, 0)).getBlock().getDrops(worldObj, pos.add(0, 2, 0), worldObj.getBlockState(pos.add(0, 2, 0)), 0));
					worldObj.setBlockToAir(pos.add(0, 2, 0));	
				}
				worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), worldObj.getBlockState(pos.add(0, 1, 0)).getBlock().getSoundType().getBreakSound(), 
						SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.add(0, 1, 0).getY() + 1.0D, 
						pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
				FARMED_STACKS.addAll(worldObj.getBlockState(pos.add(0, 1, 0)).getBlock().getDrops(worldObj, pos.add(0, 1, 0), worldObj.getBlockState(pos.add(0, 1, 0)), 0));
				worldObj.setBlockToAir(pos.add(0, 1, 0));	
				useHoe();
			}else if(worldObj.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockCactus){	      
				if(worldObj.getBlockState(pos.add(0, 2, 0)).getBlock() instanceof BlockCactus) {
					FARMED_STACKS.addAll(worldObj.getBlockState(pos.add(0, 2, 0)).getBlock().getDrops(worldObj, pos.add(0, 2, 0), worldObj.getBlockState(pos.add(0, 2, 0)), 0));
					worldObj.setBlockToAir(pos.add(0, 2, 0));	
				}
				worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), worldObj.getBlockState(pos.add(0, 1, 0)).getBlock().getSoundType().getBreakSound(), 
						SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.add(0, 1, 0).getY() + 1.0D, 
						pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
				FARMED_STACKS.addAll(worldObj.getBlockState(pos.add(0, 1, 0)).getBlock().getDrops(worldObj, pos.add(0, 1, 0), worldObj.getBlockState(pos.add(0, 1, 0)), 0));
				worldObj.setBlockToAir(pos.add(0, 1, 0));	
				useHoe();
			}else if(worldObj.getBlockState(pos).getBlock() instanceof BlockNetherWart) {
				BlockNetherWart tempNetherwart = (BlockNetherWart) worldObj.getBlockState(pos).getBlock();
				if(tempNetherwart.getMetaFromState(worldObj.getBlockState(pos)) >= 3) {
					worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), worldObj.getBlockState(pos).getBlock().getSoundType().getBreakSound(), 
							SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.getY() + 1.0D, 
							pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		        	FARMED_STACKS.addAll(getCurrentBlockDrops());
		        	worldObj.setBlockState(pos, Blocks.NETHER_WART.getDefaultState(), 2);
					useHoe();
					if(FARMED_STACKS.size() > 0) {
						for(int i=FARMED_STACKS.size()-1; i>=0; i--) {
							if(FARMED_STACKS.get(i).getItem() instanceof IPlantable) {
								FARMED_STACKS.remove(i);
								break;
							}
						}
					}
				}
			}
		}
		return true;
	}
	public List<ItemStack> getCurrentBlockDrops() {
		return worldObj.getBlockState(CURRENT_COORD).getBlock().getDrops(worldObj, CURRENT_COORD, worldObj.getBlockState(CURRENT_COORD), 0);
	}
	@Override
	public String getName() {
		return "Basic farmer";
	}
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if(facing == EnumFacing.DOWN) {
    			return (T) SLOTS_OUTPUT;
    		}else{
    			return (T) SLOTS_INPUT;
    		}
    	}
    	return super.getCapability(capability, facing);
    }
    
}
