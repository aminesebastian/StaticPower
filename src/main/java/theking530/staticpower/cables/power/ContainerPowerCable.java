package theking530.staticpower.cables.power;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.power.ContainerPowerMetricsSyncPacket;
import theking530.staticpower.tileentities.components.power.IPowerMetricsSyncConsumer;
import theking530.staticpower.tileentities.components.power.PowerTransferMetrics;

public class ContainerPowerCable extends StaticPowerTileEntityContainer<TileEntityPowerCable> implements IPowerMetricsSyncConsumer {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPowerCable, GuiPowerCable> TYPE = new ContainerTypeAllocator<>("power_cable", ContainerPowerCable::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPowerCable::new);
		}
	}
	/**
	 * How frequently we update the client side metrics.
	 */
	public static final int METRIC_UPDATE_INTERVAL = 20;
	/**
	 * Container to store all metrics.
	 */
	private PowerTransferMetrics metrics;
	private long nextUpdateTime;

	public ContainerPowerCable(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityPowerCable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPowerCable(int windowId, Inventory playerInventory, TileEntityPowerCable owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		nextUpdateTime = 0;
	}

	/**
	 * Gets the metrics that were synced to the client by the server.
	 * 
	 * @return
	 */
	public PowerTransferMetrics getMetrics() {
		return metrics;
	}

	/**
	 * This method is run every tick. In here, we keep track of when to update the
	 * client, and if required to, send over the data.
	 */
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		// Update the metrics.
		if (getTileEntity().getLevel().getGameTime() >= nextUpdateTime) {
			sendMetricsToClient();
			nextUpdateTime = getTileEntity().getLevel().getGameTime() + METRIC_UPDATE_INTERVAL;
		}
	}

	public void sendMetricsToClient() {
		// Send a packet to all listening players.
		getTileEntity().powerCableComponent.getPowerNetworkModule().ifPresent(module -> {
			for (ContainerListener listener : this.containerListeners) {
				if (listener instanceof ServerPlayer) {
					NetworkMessage msg = new ContainerPowerMetricsSyncPacket(this.containerId, module.getMetrics());
					StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) listener), msg);
				}
			}
		});
	}

	@Override
	public void recieveMetrics(PowerTransferMetrics metrics) {
		this.metrics = metrics;
	}
}
