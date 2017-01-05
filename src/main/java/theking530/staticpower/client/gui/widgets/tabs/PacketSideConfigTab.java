package theking530.staticpower.client.gui.widgets.tabs;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.SideModeList;
  
public class PacketSideConfigTab implements IMessage{
    private static int SIDE0;
    private static int SIDE1;
    private static int SIDE2;
    private static int SIDE3;
    private static int SIDE4;
    private static int SIDE5;
    private static int x;
    private static int y;
    private static int z;

    public PacketSideConfigTab() {}
    
    public PacketSideConfigTab(SideModeList.Mode[] sideModes, BlockPos pos) {
      this.SIDE0 = sideModes[0].ordinal();
      this.SIDE1 = sideModes[1].ordinal();
      this.SIDE2 = sideModes[2].ordinal();
      this.SIDE3 = sideModes[3].ordinal();
      this.SIDE4 = sideModes[4].ordinal();
      this.SIDE5 = sideModes[5].ordinal();
      this.x = pos.getX();
      this.y = pos.getY();
      this.z = pos.getZ();
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
      // the order is important
      this.SIDE0 = buf.readInt();
      this.SIDE1 = buf.readInt();
      this.SIDE2 = buf.readInt();
      this.SIDE3 = buf.readInt();
      this.SIDE4 = buf.readInt();
      this.SIDE5 = buf.readInt();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
      buf.writeInt(SIDE0);
      buf.writeInt(SIDE1);
      buf.writeInt(SIDE2);
      buf.writeInt(SIDE3);
      buf.writeInt(SIDE4);
      buf.writeInt(SIDE5);
      buf.writeInt(x);
      buf.writeInt(y);
      buf.writeInt(z);
    }
    public static class Message implements IMessageHandler<PacketSideConfigTab, IMessage> {
    @Override
    public IMessage onMessage(PacketSideConfigTab message, MessageContext ctx) {
    		TileEntity te = ctx.getServerHandler().playerEntity.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
    		if(te != null && te instanceof BaseTileEntity) {
    			BaseTileEntity entity = (BaseTileEntity)te;
    			entity.SIDE_MODES[0] = SideModeList.Mode.values()[message.SIDE0];
    			entity.SIDE_MODES[1] = SideModeList.Mode.values()[message.SIDE1];
    			entity.SIDE_MODES[2] = SideModeList.Mode.values()[message.SIDE2];
    			entity.SIDE_MODES[3] = SideModeList.Mode.values()[message.SIDE3];
    			entity.SIDE_MODES[4] = SideModeList.Mode.values()[message.SIDE4];
    			entity.SIDE_MODES[5] = SideModeList.Mode.values()[message.SIDE5];
    		}
		return null;
    	}
    }
}
