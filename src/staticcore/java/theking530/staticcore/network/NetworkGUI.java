package theking530.staticcore.network;

import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;
import theking530.staticpower.container.StaticPowerContainer;

public class NetworkGUI {
	public static void openScreen(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
		NetworkHooks.openScreen(player, containerSupplier, extraDataWriter);
		if (player.containerMenu instanceof StaticPowerContainer) {
			((StaticPowerContainer) player.containerMenu).setName(containerSupplier.getDisplayName());
		}
	}
}
