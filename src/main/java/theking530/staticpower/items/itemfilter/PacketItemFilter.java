package theking530.staticpower.items.itemfilter;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemFilter implements IMessage{
    private boolean WHITE_LIST_MODE;

    public PacketItemFilter() {}
    
    public PacketItemFilter(boolean listMode) {
    	WHITE_LIST_MODE = listMode;
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
	    	if(ctx.getServerHandler().player.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY && ctx.getServerHandler().player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFilter) {
	    		ItemStack itemstack = ctx.getServerHandler().player.getHeldItem(EnumHand.MAIN_HAND);
	    		if(itemstack.hasTagCompound()) {
		    		System.out.println(itemstack.getTagCompound());
	    			itemstack.getTagCompound().setBoolean("WHITE_LIST_MODE", message.WHITE_LIST_MODE);
	    		}
	    	}
			return null;
    	}
    }
}
