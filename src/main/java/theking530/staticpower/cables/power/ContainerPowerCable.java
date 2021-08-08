package theking530.staticpower.cables.power;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.PacketDistributor;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.power.ContainerPowerMetricsSyncPacket;
import theking530.staticpower.tileentities.components.power.IPowerMetricsSyncConsumer;
import theking530.staticpower.tileentities.components.power.TransferMetrics;

public class ContainerPowerCable extends StaticPowerTileEntityContainer<TileEntityPowerCable> implements IPowerMetricsSyncConsumer {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPowerCable, GuiPowerCable> TYPE = new ContainerTypeAllocator<>("power_cable", ContainerPowerCable::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPowerCable::new);
		}
	}
	public static final int METRIC_UPDATE_INTERVAL = 20;
	private TransferMetrics secondsMetrics;
	private TransferMetrics minuteMetrics;
	private TransferMetrics hourlyMetrics;
	private long nextUpdateTime;

	public ContainerPowerCable(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityPowerCable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPowerCable(int windowId, PlayerInventory playerInventory, TileEntityPowerCable owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		nextUpdateTime = 0;
	}

	public TransferMetrics getSecondsMetrics() {
		return secondsMetrics;
	}

	public TransferMetrics getMinuteMetrics() {
		return minuteMetrics;
	}

	public TransferMetrics getHourlyMetrics() {
		return hourlyMetrics;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		// Update the metrics.
		if (getTileEntity().getWorld().getGameTime() >= nextUpdateTime) {
			sendMetricsToClient();
			nextUpdateTime = getTileEntity().getWorld().getGameTime() + METRIC_UPDATE_INTERVAL;
		}
	}

	public void sendMetricsToClient() {
		// Send a packet to all listening players.
		getTileEntity().powerCableComponent.getPowerNetworkModule().ifPresent(module -> {
			for (IContainerListener listener : this.listeners) {
				if (listener instanceof ServerPlayerEntity) {
					NetworkMessage msg = new ContainerPowerMetricsSyncPacket(this.windowId, module.getSecondsMetrics(), module.getMinutesMetrics(), module.getHoursMetrics());
					StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) listener), msg);
				}
			}
		});
	}

	@Override
	public void recieveMetrics(TransferMetrics secondsMetrics, TransferMetrics minuteMetrics, TransferMetrics hourlyMetrics) {
		this.secondsMetrics = secondsMetrics;
		this.minuteMetrics = minuteMetrics;
		this.hourlyMetrics = hourlyMetrics;
	}
}
