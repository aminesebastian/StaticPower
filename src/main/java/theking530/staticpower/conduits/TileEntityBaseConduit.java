package theking530.staticpower.conduits;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.conduits.itemconduit.TileEntityItemConduit;

public class TileEntityBaseConduit extends TileEntity implements IConduit {
	
	public EnumFacing[] connections = new EnumFacing[6];
	public EnumFacing[] receivers = new EnumFacing[6];
	
	public int[] SIDE_MODES = {0,0,0,0,0,0};
	
	
	public void updateConduitRenderConnections() {
		if(isConduit(pos.offset(EnumFacing.UP))) connections[0] = EnumFacing.UP;
		else connections[0] = null;
		if(isConduit(pos.offset(EnumFacing.DOWN))) connections[1] = EnumFacing.DOWN;
		else connections[1] = null;
		if(isConduit(pos.offset(EnumFacing.NORTH))) connections[2] = EnumFacing.NORTH;
		else connections[2] = null;
		if(isConduit(pos.offset(EnumFacing.SOUTH))) connections[3] = EnumFacing.SOUTH;
		else connections[3] = null;
		if(isConduit(pos.offset(EnumFacing.EAST))) connections[4] = EnumFacing.EAST;
		else connections[4] = null;
		if(isConduit(pos.offset(EnumFacing.WEST))) connections[5] = EnumFacing.WEST;
		else connections[5] = null;		
	}	
	public void updateRecieverRenderConnections() {
		if(isReciever(pos.offset(EnumFacing.UP))) receivers[0] = EnumFacing.UP;
		else receivers[0] = null;
		if(isReciever(pos.offset(EnumFacing.DOWN))) receivers[1] = EnumFacing.DOWN;
		else receivers[1] = null;
		if(isReciever(pos.offset(EnumFacing.NORTH))) receivers[3] = EnumFacing.NORTH;
		else receivers[3] = null;
		if(isReciever(pos.offset(EnumFacing.SOUTH))) receivers[2] = EnumFacing.SOUTH;
		else receivers[2] = null;
		if(isReciever(pos.offset(EnumFacing.EAST))) receivers[4] = EnumFacing.EAST;
		else receivers[4] = null;
		if(isReciever(pos.offset(EnumFacing.WEST))) receivers[5] = EnumFacing.WEST;
		else receivers[5] = null;		
	}	
	@Override
	public boolean isConduit(BlockPos pos) {
		return this.worldObj.getTileEntity(pos) instanceof TileEntityItemConduit;
	}		
	@Override
	public boolean isReciever(BlockPos pos) {
		TileEntity te = worldObj.getTileEntity(pos);
		if(te instanceof IInventory) {
			if(te instanceof TileEntityItemConduit) {
				return false;
			}
			return true;
		}
		return false;
	}	
	public boolean straightConnection(EnumFacing[] directions) {
		EnumFacing mainDirection = null;
		boolean isOpposite = false;
		
		for (int i = 0; i < directions.length; i++) {
			if(mainDirection == null && directions[i] != null) mainDirection = directions[i];
			
			if(directions[i] != null && mainDirection != directions[i]) {
				if(!isOpposite(mainDirection, directions[i])) {
					return false;
				} else {
					isOpposite = true;
				}
			}
		}	
		return isOpposite;
	}	
	public boolean isOpposite(EnumFacing firstDirection, EnumFacing secondDirection) {
		if(firstDirection.equals(EnumFacing.NORTH) && (secondDirection.equals(EnumFacing.SOUTH)) || (firstDirection.equals(EnumFacing.SOUTH) && (secondDirection.equals(EnumFacing.NORTH)))) {
			return true;
		}
		if(firstDirection.equals(EnumFacing.UP) && (secondDirection.equals(EnumFacing.DOWN)) || (firstDirection.equals(EnumFacing.DOWN) && (secondDirection.equals(EnumFacing.UP)))) {
			return true;
		}
		if(firstDirection.equals(EnumFacing.EAST) && (secondDirection.equals(EnumFacing.WEST)) || (firstDirection.equals(EnumFacing.WEST) && (secondDirection.equals(EnumFacing.EAST)))) {
			return true;
		}
		return false;
	}
	public boolean disconected(EnumFacing from) {
		if(SIDE_MODES[from.ordinal()] == 2){
			return true;
		}
		return false;
	}
	public boolean canPull(EnumFacing from) {
		if(SIDE_MODES[from.ordinal()] == 1){
			return true;
		}
		return false;
	}
	public boolean canOutput(EnumFacing from) {
		if(SIDE_MODES[from.ordinal()] == 0){
			return true;
		}
		return false;
	}
	public boolean isConnected() {
		for(int i = 0; i < receivers.length; i++){
			if(receivers[i] == null) {
				return false;
			}
		}
		return true;
	}
	public void incrementSideMode(EnumFacing side) {
		TileEntity temp = worldObj.getTileEntity(pos.offset(side));
		if(temp != null && isReciever(temp.getPos())) {
			if(SIDE_MODES[side.ordinal()] < 2) {
				SIDE_MODES[side.ordinal()]++;
			}else{
				SIDE_MODES[side.ordinal()] = 0;
			}
		}
		System.out.println(SIDE_MODES[side.ordinal()]);
	}
}
