package theking530.staticpower.items.itemfilter;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemFilter implements IMessage{
    private static boolean WHITE_LIST_MODE;
	private static InventoryItemFilter INV_FILTER;
	
    public PacketItemFilter() {}
    
    public PacketItemFilter(InventoryItemFilter invFilter, boolean listMode) {
    	WHITE_LIST_MODE = listMode;
    	INV_FILTER = invFilter;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
      WHITE_LIST_MODE = buf.readBoolean();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
      buf.writeBoolean(WHITE_LIST_MODE);
    }
    public static class Message implements IMessageHandler<PacketItemFilter, IMessage> {
	    @Override
	    public IMessage onMessage(PacketItemFilter message, MessageContext ctx) {
	    	INV_FILTER.setWhiteListMode(message.WHITE_LIST_MODE);
			return null;
    	}
    }
}
