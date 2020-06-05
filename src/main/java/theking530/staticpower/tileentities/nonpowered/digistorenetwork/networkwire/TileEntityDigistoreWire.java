package theking530.staticpower.tileentity.digistorenetwork.networkwire;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import theking530.staticpower.tileentity.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreWire extends TileEntity  implements ITickable{
	
	public Direction[] connections = new Direction[6];
	public Direction[] receivers = new Direction[6];
	
	@Override
	public void update() {
		updateConduitRenderConnections();
		updateRecieverRenderConnections();
	}
	
	public void updateConduitRenderConnections() {
		if(isConduit(Direction.UP)) connections[0] = Direction.UP;
		else connections[0] = null;
		if(isConduit(Direction.DOWN)) connections[1] = Direction.DOWN;
		else connections[1] = null;
		if(isConduit(Direction.NORTH)) connections[2] = Direction.NORTH;
		else connections[2] = null;
		if(isConduit(Direction.SOUTH)) connections[3] = Direction.SOUTH;
		else connections[3] = null;
		if(isConduit(Direction.EAST)) connections[4] = Direction.EAST;
		else connections[4] = null;
		if(isConduit(Direction.WEST)) connections[5] = Direction.WEST;
		else connections[5] = null;	
		
	}	
	public void updateRecieverRenderConnections() {
		if(isReciever(Direction.UP)) receivers[0] = Direction.UP;
		else receivers[0] = null;
		if(isReciever(Direction.DOWN)) receivers[1] = Direction.DOWN;
		else receivers[1] = null;
		if(isReciever(Direction.NORTH)) receivers[3] = Direction.NORTH;
		else receivers[3] = null;
		if(isReciever(Direction.SOUTH)) receivers[2] = Direction.SOUTH;
		else receivers[2] = null;
		if(isReciever(Direction.EAST)) receivers[4] = Direction.EAST;
		else receivers[4] = null;
		if(isReciever(Direction.WEST)) receivers[5] = Direction.WEST;
		else receivers[5] = null;		
	}
	public boolean isConduit(Direction side) {
		return getWorld().getTileEntity(getPos().offset(side)) != null && getWorld().getTileEntity(getPos().offset(side)) instanceof TileEntityDigistoreWire;
	}		
	public boolean isReciever(Direction side) {
		return getWorld().getTileEntity(getPos().offset(side)) != null && getWorld().getTileEntity(getPos().offset(side)) instanceof BaseDigistoreTileEntity;
	}	
	public boolean straightConnection(Direction[] directions) {
		Direction mainDirection = null;
		boolean isOpposite = false;
		
		for (int i = 0; i < directions.length; i++) {
			if(mainDirection == null && directions[i] != null) mainDirection = directions[i];
			
			if(directions[i] != null && mainDirection != directions[i]) {
				if(mainDirection.getOpposite() != directions[i]) {
					return false;
				} else {
					isOpposite = true;
				}
			}
		}	
		return isOpposite;
	}
}
