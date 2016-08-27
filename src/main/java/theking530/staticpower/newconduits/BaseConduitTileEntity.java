package theking530.staticpower.newconduits;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BaseConduitTileEntity extends TileEntity implements ITickable{

	public BaseConduitNetwork NETWORK;
	public boolean PLACED = false;
	
	@Override
	public void update() {
		if(!PLACED) {
			onPlaced();	
		}
	}
	public void onPlaced(){ 
		PLACED = false;
		for(int i=0; i<6; i++) {
			if(worldObj.getTileEntity(pos.offset(EnumFacing.values()[i])) != null && worldObj.getTileEntity(pos.offset(EnumFacing.values()[i])) instanceof BaseConduitTileEntity) {
				BaseConduitTileEntity te = (BaseConduitTileEntity)worldObj.getTileEntity(pos.offset(EnumFacing.values()[i]));
				if(te.NETWORK != null) {
					te.NETWORK.addConduit(this);
					PLACED = true;
					break;
				}
			}
		}
		if(!PLACED) {
			NETWORK = new BaseConduitNetwork();
			NETWORK.addConduit(this);
		}
	}
	public void onNeighborUpdate(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		if(NETWORK.CONDUIT_MAP.containsKey(worldObj.getTileEntity(neighbor))) {
			NETWORK.CONDUIT_MAP.remove(worldObj.getTileEntity(neighbor));		
		}
	}
}
