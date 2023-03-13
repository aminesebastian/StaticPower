package theking530.staticcore.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public abstract class NetworkMessage {
	/**
	 * Must ALWAYS have a default constructor for the decode phase.
	 */
	public NetworkMessage() {
	}

	/**
	 * Override this method to encode this packet's data to the
	 * {@link PacketBuffer}.
	 * 
	 * @param buffer The buffer to write the data to.
	 */
	public abstract void encode(FriendlyByteBuf buffer);

	/**
	 * Override this method to decode this packet's data from the
	 * {@link PacketBuffer}. The instance this is called on is the one that the
	 * {@link #handle()} is then raised on.
	 * 
	 * @param buffer The buffer to read the data from.
	 */
	public abstract void decode(FriendlyByteBuf buffer);

	/**
	 * Override this method to perform any actions once the data has been decoded.
	 * 
	 * @param ctx The context of the message.
	 */
	public abstract void handle(Supplier<Context> ctx);

	/**
	 * This method is raised by the packet handler and should not normally be
	 * overridden.
	 * 
	 * @param ctx The context of the message.
	 */
	public void _handle(Supplier<Context> ctx) {
		handle(ctx);
		ctx.get().setPacketHandled(true);
	}
}
