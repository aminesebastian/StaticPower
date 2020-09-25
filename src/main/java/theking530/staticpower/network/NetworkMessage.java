package theking530.staticpower.network;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

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
	public abstract void encode(PacketBuffer buffer);

	/**
	 * Override this method to decode this packet's data from the
	 * {@link PacketBuffer}. The instance this is called on is the one that the
	 * {@link #handle()} is then raised on.
	 * 
	 * @param buffer The buffer to read the data from.
	 */
	public abstract void decode(PacketBuffer buffer);

	/**
	 * Override this method to perform any actions once the data has been decoded.
	 * 
	 * @param ctx The context of the message.
	 */
	public abstract void handle(Supplier<Context> ctx);

	protected void writeStringOnServer(String componentName, PacketBuffer buf) {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("te_component_name", componentName);
		buf.writeCompoundTag(tag);
	}

	protected String readStringOnServer(PacketBuffer buf) {
		return buf.readCompoundTag().getString("te_component_name");
	}

	/**
	 * This method is raised by the packet handler and should not normally be
	 * overridden.
	 * 
	 * @param ctx The context of the message.
	 */
	public void _handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			handle(ctx);
		});
		ctx.get().setPacketHandled(true);
	}
}
