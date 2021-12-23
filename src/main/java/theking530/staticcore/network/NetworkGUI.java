package theking530.staticcore.network;

import java.util.function.Consumer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraft.network.FriendlyByteBuf;
import theking530.staticpower.container.StaticPowerContainer;

public class NetworkGUI {
	public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
		NetworkHooks.openGui(player, containerSupplier, extraDataWriter);
		if (player.containerMenu instanceof StaticPowerContainer) {
			((StaticPowerContainer) player.containerMenu).setName(containerSupplier.getDisplayName());
		}
	}
}
