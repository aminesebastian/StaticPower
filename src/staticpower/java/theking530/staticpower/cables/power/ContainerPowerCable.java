package theking530.staticpower.cables.power;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.metrics.IPowerMetricsSyncConsumer;
import theking530.api.energy.metrics.PowerTransferMetrics;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerPowerCable extends StaticPowerTileEntityContainer<BlockEntityPowerCable> implements IPowerMetricsSyncConsumer {
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
		this(windowId, inv, (BlockEntityPowerCable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPowerCable(int windowId, Inventory playerInventory, BlockEntityPowerCable owner) {
		super(TYPE, windowId, playerInventory, owner);
		metrics = new PowerTransferMetrics();
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
			if (this.containerListeners.size() > 0 && getPlayerInventory().player instanceof ServerPlayer) {
//				NetworkMessage msg = new ContainerPowerMetricsSyncPacket(this.containerId, module.getMetrics());
//				StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) getPlayerInventory().player), msg);
			}
		});
	}

	@Override
	public void recieveMetrics(PowerTransferMetrics metrics) {
		this.metrics = metrics;
	}
}
