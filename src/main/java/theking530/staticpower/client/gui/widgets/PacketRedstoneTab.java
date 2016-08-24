package theking530.staticpower.client.gui.widgets;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import theking530.staticpower.tileentity.BaseTileEntity;

  
public class PacketRedstoneTab implements IMessage{
    private static int REDSTONE_MODE;
    private static int x;
    private static int y;
    private static int z;

    public PacketRedstoneTab(int REDSTONE_MODE, BlockPos pos) {
      this.REDSTONE_MODE = REDSTONE_MODE;
      this.x = pos.getX();
      this.y = pos.getY();
      this.z = pos.getZ();
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
      // the order is important
      this.REDSTONE_MODE = buf.readInt();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
      buf.writeInt(REDSTONE_MODE);
      buf.writeInt(x);
      buf.writeInt(y);
      buf.writeInt(z);
    }
    public static class Message implements IMessageHandler<PacketRedstoneTab, IMessage> {
    @Override
    public IMessage onMessage(PacketRedstoneTab message, MessageContext ctx) {
    		TileEntity te = ctx.getServerHandler().playerEntity.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
    		if(te != null && te instanceof BaseTileEntity) {
    			BaseTileEntity entity = (BaseTileEntity)te;
    			entity.REDSTONE_MODE = message.REDSTONE_MODE;
    		}
		return null;
    	}
    }
}
