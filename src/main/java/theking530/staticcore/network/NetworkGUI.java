package theking530.staticcore.network;

import java.util.function.Consumer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.staticpower.container.StaticPowerContainer;

public class NetworkGUI {
	public static void openGui(ServerPlayerEntity player, INamedContainerProvider containerSupplier, Consumer<PacketBuffer> extraDataWriter) {
		NetworkHooks.openGui(player, containerSupplier, extraDataWriter);
		if (player.openContainer instanceof StaticPowerContainer) {
			((StaticPowerContainer) player.openContainer).setName(containerSupplier.getDisplayName());
		}
	}
}
