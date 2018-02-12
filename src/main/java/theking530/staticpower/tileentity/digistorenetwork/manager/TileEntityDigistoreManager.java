package theking530.staticpower.tileentity.digistorenetwork.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.tileentity.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentity.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.tileentity.digistorenetwork.networkextender.BlockDigistoreNetworkExtender;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {
	
	private Map<BlockPos, BaseDigistoreTileEntity> masterDigistoreList;
	private List<TileEntityDigistore> digistoreList;
	private List<BlockPos> extenderPositions;
	
	public TileEntityDigistoreManager() {

	}

	@Override
	public void process() {
		updateGrid();
	}
	public Map<BlockPos, BaseDigistoreTileEntity> getGrid() {
		return masterDigistoreList;
	}	
	
	
	private Map<BlockPos, BaseDigistoreTileEntity> createNewGrid() {
		return new HashMap<BlockPos, BaseDigistoreTileEntity>();
	}
	private void updateGrid() {
		masterDigistoreList = createNewGrid();
		digistoreList = new ArrayList<TileEntityDigistore>();
		extenderPositions = new ArrayList<BlockPos>();
		masterDigistoreList.put(getPos(), this);
		setManager(this);
		generateGridNeighbors(masterDigistoreList, getPos()); 
	}
	private void generateGridNeighbors(Map<BlockPos, BaseDigistoreTileEntity> grid, BlockPos currentPos) {
		for(EnumFacing facing : EnumFacing.values()) {
			BlockPos testPos = currentPos.offset(facing);
			TileEntity te = getWorld().getTileEntity(testPos);
			if(te != null && !te.isInvalid() && te instanceof BaseDigistoreTileEntity) {
				if(!grid.containsKey(testPos)) {
					if(te instanceof TileEntityDigistoreManager) {
						TileEntityDigistoreManager oldManager = (TileEntityDigistoreManager)te;
						oldManager.masterDigistoreList = createNewGrid();
						continue;
					}
					BaseDigistoreTileEntity digistore = (BaseDigistoreTileEntity)te;
					digistore.setManager(this);
					grid.put(testPos, digistore);
					if(te instanceof TileEntityDigistore) {
						digistoreList.add((TileEntityDigistore)te);
					}
					generateGridNeighbors(grid, testPos);
				}
			}else if(!extenderPositions.contains(testPos) && world.getBlockState(testPos).getBlock() instanceof BlockDigistoreNetworkExtender) {
				extenderPositions.add(testPos);
				generateGridNeighbors(grid, testPos);
			}
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		updateGrid();
    }
	@Override 
	public void onBroken(){
		for(Entry<BlockPos, BaseTileEntityDigistore> entry : masterDigistoreList) {
			entry.getValue().setManager(null);
		}
	}
	/*Capability Handling*/
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(getSideConfiguration(facing) == Mode.Disabled) {
    		return false;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
       	if(getSideConfiguration(facing) == Mode.Disabled) {
    		return null;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new IItemHandler() {
				public int getSlots() {
			    	return evauluateRedstoneSettings() ? digistoreList.size() : 0;
			    }
			    @Nonnull
			    public ItemStack getStackInSlot(int slot) {
			    	if(slot < digistoreList.size()) {  		
			    		return digistoreList.get(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(slot);
			    	}
			    	return ItemStack.EMPTY;
			    }
			    @Nonnull
			    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			    	for(TileEntityDigistore digistore : digistoreList) {
			    		if(ItemHandlerHelper.canItemStacksStack(digistore.getStoredItem(), stack) && !digistore.isFull()) {
				    		return digistore.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(slot, stack, simulate);
			    		}
			    	}
			    	return digistoreList.get(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(slot, stack, simulate);
			    }
			    @Nonnull
			    public ItemStack extractItem(int slot, int amount, boolean simulate) {
			    	if(slot < digistoreList.size()) {  		
			    		return digistoreList.get(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).extractItem(slot, amount, simulate);
			    	}
			    	return ItemStack.EMPTY;   	
			    }
			    public int getSlotLimit(int slot) {
			    	if(slot < digistoreList.size()) {  		
			    		return digistoreList.get(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getSlotLimit(slot);
			    	}
			    	return 0;
			    }
			});
    	}
    	return super.getCapability(capability, facing);
    }
}
