package theking530.staticpower.network;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import theking530.staticpower.StaticPower;
import theking530.staticpower.utilities.Reference;

/**
 * Class responsible for containing all the registered packets.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerMessageHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel MAIN_PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);
	private static int currentMessageId = 0;

	public static void sendMessageToPlayerInArea(SimpleChannel channel, World world, BlockPos position, int radius, NetworkMessage message) {
		channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(position.getX(), position.getY(), position.getZ(), radius, world.getDimension().getType())), message);
	}

	/**
	 * Registers the provided message class as a valid packet.
	 * 
	 * @param <MSG> The type of the message (must extend {@link NetworkMessage}.
	 * @param type  The specific class of the message to register.
	 */
	public static <MSG extends NetworkMessage> void registerMessage(Class<MSG> type) {
		MAIN_PACKET_CHANNEL.registerMessage(currentMessageId++, type, (MSG message, PacketBuffer buff) -> {
			message.encode(buff);
		}, (PacketBuffer buff) -> {
			MSG pack = createNewInstance(type);
			pack.decode(buff);
			return pack;
		}, (MSG message, Supplier<Context> ctx) -> message._handle(ctx));
	}

	/**
	 * Creates an instance of the message type through reflection. It's not THAT
	 * expensive as its only runtime reflection instantiation and NOT runtime lookup
	 * AND instantiation.
	 * 
	 * @param <MSG>   The type of the message (must extend {@link NetworkMessage}.
	 * @param typeThe specific class of the message to instantiate.
	 * @return
	 */
	private static <MSG extends NetworkMessage> MSG createNewInstance(Class<MSG> type) {
		MSG pack = null;
		try {
			pack = type.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			StaticPower.LOGGER.error(String.format("An error occured when attempting to decode packet of type: %1$s.", type.toString()), e);
		}
		return pack;
	}
}
