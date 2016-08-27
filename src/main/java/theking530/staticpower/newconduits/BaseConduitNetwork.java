package theking530.staticpower.newconduits;

import java.util.HashMap;

import net.minecraft.util.math.BlockPos;

public class BaseConduitNetwork {

	HashMap<BaseConduitTileEntity, BlockPos> CONDUIT_MAP = new HashMap<BaseConduitTileEntity, BlockPos>();
	
	public BaseConduitNetwork() {
		
	}
	public void addConduit(BaseConduitTileEntity conduit) {
		if(!CONDUIT_MAP.containsKey(conduit)) {
			CONDUIT_MAP.put(conduit, conduit.getPos());
			conduit.NETWORK = this;
		}
	}
	public boolean removeConduit(BaseConduitTileEntity conduit) {
		return CONDUIT_MAP.remove(conduit, conduit.getPos());
	}
	public String toString(){
		return "Number of Conduits: " + CONDUIT_MAP.size() + "  Network ID: " + this.hashCode();
	}
}
