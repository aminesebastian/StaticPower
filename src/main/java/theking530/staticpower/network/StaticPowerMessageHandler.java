package theking530.staticpower.network;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.StaticPower;

/**
 * Class responsible for containing all the registered packets.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerMessageHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerMessageHandler.class);
	public static final SimpleChannel MAIN_PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(StaticPower.MOD_ID, "main"), () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int currentMessageId = 0;

	public static void sendMessageToPlayerInArea(SimpleChannel channel, Level world, BlockPos position, int radius, NetworkMessage message) {
		channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(position.getX(), position.getY(), position.getZ(), radius, world.dimension())), message);
	}

	public static void sendToAllPlayersInDimension(SimpleChannel channel, Level world, NetworkMessage message) {
		channel.send(PacketDistributor.DIMENSION.with(() -> world.dimension()), message);
	}

	public static void sendMessageToPlayer(SimpleChannel channel, ServerPlayer player, NetworkMessage message) {
		channel.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static void sendMessageToPlayer(SimpleChannel channel, ServerPlayer player, Packet<ClientGamePacketListener> message) {
		channel.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static void sendToServer(SimpleChannel channel, NetworkMessage message) {
		channel.sendToServer(message);
	}

	/**
	 * Registers the provided message class as a valid packet.
	 * 
	 * @param <MSG> The type of the message (must extend {@link NetworkMessage}.
	 * @param type  The specific class of the message to register.
	 */
	public static <MSG extends NetworkMessage> void registerMessage(Class<MSG> type) {
		MAIN_PACKET_CHANNEL.registerMessage(currentMessageId++, type, (MSG message, FriendlyByteBuf buff) -> {
			message.encode(buff);
		}, (FriendlyByteBuf buff) -> {
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
	private static <MSG> MSG createNewInstance(Class<MSG> type) {
		MSG pack = null;
		try {
			pack = type.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOGGER.error(String.format("An error occured when attempting to decode packet of type: %1$s. Ensure that there is a parameterless constructor defined!", type.toString()), e);
		}
		return pack;
	}
}
