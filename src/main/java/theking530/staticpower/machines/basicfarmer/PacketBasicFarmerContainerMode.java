package theking530.staticpower.machines.basicfarmer;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent.FluidContainerInteractionMode;

public class PacketBasicFarmerContainerMode implements IMessage{
	
    private FluidContainerInteractionMode MODE;
    private int x;
    private int y;
    private int z;

    public PacketBasicFarmerContainerMode() {}
    
    public PacketBasicFarmerContainerMode(FluidContainerInteractionMode mode, BlockPos pos) {
      MODE = mode;
      x = pos.getX();
      y = pos.getY();
      z = pos.getZ();
    }   
    @Override
    public void fromBytes(ByteBuf buf) {
      this.MODE = FluidContainerInteractionMode.values()[buf.readInt()];
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
    }    
    @Override
    public void toBytes(ByteBuf buf) {
      buf.writeInt(MODE.ordinal());
      buf.writeInt(x);
      buf.writeInt(y);
      buf.writeInt(z);
    }
    public static class Message implements IMessageHandler<PacketBasicFarmerContainerMode, IMessage> {
    @Override
    public IMessage onMessage(PacketBasicFarmerContainerMode message, MessageContext ctx) {
    		TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
    		if(te != null && te instanceof TileEntityBasicFarmer) {
    			TileEntityBasicFarmer entity = (TileEntityBasicFarmer)te;
    			entity.DRAIN_COMPONENT.setMode(message.MODE);
    			entity.updateBlock();
    		}
		return null;
    	}
    }
}

