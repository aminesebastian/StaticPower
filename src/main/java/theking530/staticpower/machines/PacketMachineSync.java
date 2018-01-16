package theking530.staticpower.machines;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import theking530.staticpower.tileentity.BaseTileEntity;
  
public class PacketMachineSync implements IMessage{

	public NBTTagCompound TAG;
	public int X;
	public int Y;
	public int Z;
	
    public PacketMachineSync() {}
    
    public PacketMachineSync(BaseTileEntity TE) {
    	TAG = TE.writeToSyncNBT(new NBTTagCompound());
    	X = TE.getPos().getX();
    	Y = TE.getPos().getY();
    	Z = TE.getPos().getZ();
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	TAG = ByteBufUtils.readTag(buf);
    	X = buf.readInt();
    	Y = buf.readInt();
    	Z = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
    	ByteBufUtils.writeTag(buf, TAG);
    	buf.writeInt(X);
    	buf.writeInt(Y);
    	buf.writeInt(Z);
    }
    public static class Message implements IMessageHandler<PacketMachineSync, IMessage> {
    @Override
    public IMessage onMessage(PacketMachineSync message, MessageContext ctx) {
	 	TileEntity tile = ctx.getServerHandler().player.getEntityWorld().getTileEntity(new BlockPos(message.X, message.Y, message.Z));
		if(tile != null && tile instanceof BaseTileEntity) {
			BaseTileEntity te = (BaseTileEntity)tile;					
			te.readFromSyncNBT(message.TAG);
		}
		return null;
    	}
    }
}
