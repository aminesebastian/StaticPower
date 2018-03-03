package theking530.staticpower.tileentity.chest;

import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityBaseChest extends BaseTileEntity{

    public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
    public int ticksSinceSync;
	
	@Override
	public String getName() {
		return "container.StaticChest";
	}					
	@Override
	public boolean isSideConfigurable() {
		return false;
	}
	/* CAPABILITIES */
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
    @SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return (T) slotsOutput;
    	}
    	return super.getCapability(capability, facing);
    }  
}
