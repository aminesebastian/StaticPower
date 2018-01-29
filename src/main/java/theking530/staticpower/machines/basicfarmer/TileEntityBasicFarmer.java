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
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityBasicFarmer extends BaseMachineWithTank {

	public int INITIAL_RANGE = 2;
	public int RANGE = INITIAL_RANGE;
	public int BLOCKS_PER_TICK = 1;
	public int GROWTH_BONUS_CHANCE = 50;
	public BlockPos CURRENT_COORD;
	private Random RAND;
	private ArrayList<ItemStack> FARMED_STACKS;
	public DrainToBucketComponent DRAIN_COMPONENT;
	
	public TileEntityBasicFarmer() {
		initializeBaseMachineWithTank(2, 20, 100000, 100, 10, 0, 4, 10, 10000);		
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", slotsInput, 2, slotsOutput, 9, this, TANK, FLUID_TO_CONTAINER_RATE);
		DRAIN_COMPONENT.setMode(FluidContainerInteractionMode.FillFromContainer);
		setBatterySlot(3);
		CURRENT_COORD = getStartingCoord();
		RAND = new Random();
		FARMED_STACKS = new ArrayList<ItemStack>();
	}
	@Override
	public void process(){
		DRAIN_COMPONENT.update();
		updateGrowthChange();
		if(processingTimer < processingTime && canFarm()) {
			processingTimer++;
		}else{
			if(FARMED_STACKS.size() <= 1 && energyStorage.getEnergyStored() >= getProcessingCost() * BLOCKS_PER_TICK) {
				for(int i=0; i<BLOCKS_PER_TICK; i++) {
					incrementPosition();
					checkFarmingPlot(CURRENT_COORD);
				}
				useEnergy(getProcessingCost() * BLOCKS_PER_TICK);
				TANK.drain(1 * BLOCKS_PER_TICK, true);
				updateBlock();
				processingTimer = 0;
			}else{
				if(!getWorld().isRemote) {
					for(int i=FARMED_STACKS.size()-1; i>=0; i--) {
						ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(slotsOutput, FARMED_STACKS.get(i), 0, 8);
						if(insertedStack == ItemStack.EMPTY) {
							FARMED_STACKS.remove(i);
						}else{
							FARMED_STACKS.set(i, insertedStack);
						}
					}
				}	
			}
		}	
	}
	public void updateGrowthChange() {
		if(TANK.getFluid() != null) {
			if(TANK.getFluid().getFluid() == ModFluids.StaticFluid) {
				GROWTH_BONUS_CHANCE = 15;
			}else if(TANK.getFluid().getFluid() == ModFluids.EnergizedFluid){
				GROWTH_BONUS_CHANCE = 25;
			}else if(TANK.getFluid().getFluid() == ModFluids.LumumFluid){
				GROWTH_BONUS_CHANCE = 50;
			}else{
				GROWTH_BONUS_CHANCE = 0;
			}
		}else{
			GROWTH_BONUS_CHANCE = 1;
		}
	}
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("CURR_X")) {
            CURRENT_COORD = new BlockPos(new Vec3i(nbt.getInteger("CURR_X"), nbt.getInteger("CURR_Y"), nbt.getInteger("CURR_Z")));	
        }
        if(nbt.hasKey("FARMED_COUNT")) {
            for(int i=0; i<nbt.getInteger("FARMED_COUNT"); i++) {
            	FARMED_STACKS.add(new ItemStack(nbt.getCompoundTag("FARMED"+i)));
            }
        }
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		nbt.setInteger("CURR_X", CURRENT_COORD.getX());
		nbt.setInteger("CURR_Y", CURRENT_COORD.getY());
		nbt.setInteger("CURR_Z", CURRENT_COORD.getZ());
		nbt.setInteger("FARMED_COUNT", FARMED_STACKS.size());
		for(int i=0; i<FARMED_STACKS.size(); i++) {
			nbt.setTag("FARMED" + i, FARMED_STACKS.get(i).writeToNBT(new NBTTagCompound()));
		}
    	return nbt;
	}
	
	public void upgradeHandler(){
		rangeUpgrade();
		super.upgradeHandler();
	}
	public void rangeUpgrade() {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(slotsUpgrades.getStackInSlot(i) != null) {
				if(slotsUpgrades.getStackInSlot(i).getItem() instanceof BaseRangeUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseRangeUpgrade tempUpgrade = (BaseRangeUpgrade) slotsUpgrades.getStackInSlot(slot).getItem();
			RANGE = (int) (INITIAL_RANGE*tempUpgrade.getMultiplier(slotsUpgrades.getStackInSlot(slot), 0));
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
		if(energyStorage.getEnergyStored() >= getProcessingCost() && slotsInput.getStackInSlot(0) != null && slotsInput.getStackInSlot(0).getItem() instanceof ItemHoe) {
			return true;
		}
		return false;
	}
	public void useHoe(){
		if(slotsInput.getStackInSlot(0) != ItemStack.EMPTY && slotsInput.getStackInSlot(0).getItem() instanceof ItemHoe) {
			if(slotsInput.getStackInSlot(0).attemptDamageItem(1, RAND, null)) {
				slotsInput.setStackInSlot(0, ItemStack.EMPTY);
				getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);		
			}	
		}
	}
	public void useAxe(){
		if(slotsInput.getStackInSlot(1) != ItemStack.EMPTY && slotsInput.getStackInSlot(1).getItem() instanceof ItemAxe) {
			if(slotsInput.getStackInSlot(0).attemptDamageItem(1, RAND, null)) {
				slotsInput.setStackInSlot(0, ItemStack.EMPTY);
				getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);		
			}	
		}
	}
	public boolean checkFarmingPlot(BlockPos pos) {
		boolean grown = false;
		if(!getWorld().isRemote) {
			grown = growCrop(pos);
		}
		if(grown) {
    		getWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + 0.5D, pos.getY() + 1.0D,  pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		}
		getWorld().spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		if(isFarmableBlock(pos)) {
			harvestGenericCrop(pos);			
			harvestSugarCane(pos);
			harvestCactus(pos);
			harvestNetherWart(pos);
		}
		return true;
	}
	public boolean isFarmableBlock(BlockPos pos) {
		if(getWorld().getBlockState(pos) == null) {
			return false;
		}
		if(getWorld().getBlockState(pos).getBlock() instanceof BlockCrops) {
			return true;
		}
		if(getWorld().getBlockState(pos).getBlock() instanceof BlockReed){	 
			return true;
		}
		if(getWorld().getBlockState(pos).getBlock() instanceof BlockCactus){	  
			return true;
		}
		if(getWorld().getBlockState(pos).getBlock() instanceof BlockNetherWart) {
			return true;
		}
		return false;		
	}
	public boolean harvestGenericCrop(BlockPos pos) {
		if(getWorld().getBlockState(pos).getBlock() instanceof BlockCrops) {
			BlockCrops tempCrop = (BlockCrops)getWorld().getBlockState(pos).getBlock();
			if(tempCrop.isMaxAge(getWorld().getBlockState(pos))) {
				getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
				getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
	        	if(!getWorld().isRemote) {
					FARMED_STACKS.addAll(getBlockDrops(pos));
					getWorld().setBlockState(pos, tempCrop.withAge(0), 3);	
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
		return false;
	}
	public boolean harvestSugarCane(BlockPos pos) {
		if(getWorld().getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockReed){	   
        	if(!getWorld().isRemote) {       		
		        NonNullList<ItemStack> drops1 = NonNullList.create();
				getWorld().getBlockState(pos.add(0, 1, 0)).getBlock().getDrops(drops1, getWorld(), pos.add(0, 1, 0), getWorld().getBlockState(pos.add(0, 1, 0)), 0);
				FARMED_STACKS.addAll(getBlockDrops(pos.add(0, 1, 0)));
				getWorld().setBlockToAir(pos.add(0, 1, 0));
        		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
        		useAxe();
            	       		
				if(getWorld().getBlockState(pos.add(0, 2, 0)).getBlock() instanceof BlockReed) {
					FARMED_STACKS.addAll(getBlockDrops(pos.add(0, 2, 0)));
					getWorld().setBlockToAir(pos.add(0, 2, 0));	
					getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
					useAxe();
				}
        	}
			getWorld().playSound(null, pos, getWorld().getBlockState(pos.add(0, 1, 0)).getBlock().getSoundType(getWorld().getBlockState(pos.add(0, 1, 0)), world, pos.add(0, 1, 0), null).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
			getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.add(0, 1, 0).getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			return true;
		}
		return false;
	}
	public boolean harvestCactus(BlockPos pos) {
		if(getWorld().getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockCactus){	      
        	if(!getWorld().isRemote) {
				FARMED_STACKS.addAll(getBlockDrops(pos.add(0, 1, 0)));
				getWorld().setBlockToAir(pos.add(0, 1, 0));	
				useHoe();
        		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
        		       		
				if(getWorld().getBlockState(pos.add(0, 2, 0)).getBlock() instanceof BlockCactus) {
					FARMED_STACKS.addAll(getBlockDrops(pos.add(0, 2, 0)));
					getWorld().setBlockToAir(pos.add(0, 2, 0));	
					getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
				}
        	}
			getWorld().playSound(null, pos, getWorld().getBlockState(pos.add(0, 1, 0)).getBlock().getSoundType(getWorld().getBlockState(pos.add(0, 1, 0)), world, pos.add(0, 1, 0), null).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
			getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.add(0, 1, 0).getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
        	return true;
		}
		return false;
	}
	public boolean harvestNetherWart(BlockPos pos) {
		if(getWorld().getBlockState(pos).getBlock() instanceof BlockNetherWart) {
			BlockNetherWart tempNetherwart = (BlockNetherWart) getWorld().getBlockState(pos).getBlock();
			if(tempNetherwart.getMetaFromState(getWorld().getBlockState(pos)) >= 3) {
				getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
				getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
	        	if(!getWorld().isRemote) {
					FARMED_STACKS.addAll(getBlockDrops(pos));
		        	getWorld().setBlockState(pos, Blocks.NETHER_WART.getDefaultState(), 2);
					useHoe();
					getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
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
			return true;
		}
		return false;
	}
	public boolean growCrop(BlockPos pos) {
		if(RAND.nextInt(100) <= GROWTH_BONUS_CHANCE) {
	        if(getWorld().getBlockState(pos) != null && getWorld().getBlockState(pos).getBlock() instanceof IGrowable) {
	        	IGrowable tempCrop = (IGrowable) getWorld().getBlockState(pos).getBlock();
	        	if(tempCrop.canGrow(getWorld(), pos, getWorld().getBlockState(pos), true)) {
	        		tempCrop.grow(getWorld(), RAND, pos, getWorld().getBlockState(pos));
	        		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
	        		return true;
	        	}
	        }	
		}
		return false;
	}
	public List<ItemStack> getBlockDrops(BlockPos pos) {
        NonNullList<ItemStack> ret = NonNullList.create();
		getWorld().getBlockState(CURRENT_COORD).getBlock().getDrops(ret, getWorld(), CURRENT_COORD, getWorld().getBlockState(CURRENT_COORD), 0);
		return ret;
	}
	@Override
	public String getName() {
		return "Basic farmer";
	}
    @SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if(facing == EnumFacing.UP) {
    			return (T) slotsInput;
    		}else{
    			return (T) slotsOutput;
    		}
    	}
    	return super.getCapability(capability, facing);
    }
    
}
