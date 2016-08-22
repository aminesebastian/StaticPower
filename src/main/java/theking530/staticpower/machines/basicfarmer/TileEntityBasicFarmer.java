package theking530.staticpower.machines.basicfarmer;

import javax.annotation.Nullable;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.machines.BaseMachineWithTank;

public class TileEntityBasicFarmer extends BaseMachineWithTank {

	public int RANGE = 3;
	public BlockPos CURRENT_COORD;
	
	public TileEntityBasicFarmer() {
		initializeBaseMachineWithTank(2, 100000, 10, 2, 4, new int[0], new int[0], new int[]{1,2,3}, 10000);
		CURRENT_COORD = getStartingCoord();
	}
	@Override
	public void process(){
		if(PROCESSING_TIMER < PROCESSING_TIME) {
			PROCESSING_TIMER++;
		}else{
			incrementPosition();
			checkFarmingPlot(CURRENT_COORD);
			PROCESSING_TIMER = 0;
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
	public void checkFarmingPlot(BlockPos pos) {
		worldObj.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5D, pos.getY() + 1.0D, 
				pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
		if(worldObj.getBlockState(pos).getBlock() != null && worldObj.getBlockState(pos).getBlock() instanceof BlockCrops) {
			BlockCrops tempCrop = (BlockCrops)worldObj.getBlockState(pos).getBlock();
			if(!tempCrop.canGrow(worldObj, pos, worldObj.getBlockState(pos), true)) {
				tempCrop.dropBlockAsItem(worldObj, pos, worldObj.getBlockState(pos), 0); 
	        	worldObj.setBlockState(pos, tempCrop.withAge(0), 2);
			}
		}
	}
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);      
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		return nbt;	
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
}
