package theking530.staticpower.blockentities.power.wireconnector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.SparseCableLink;
import theking530.staticpower.cables.SparseCableLink.SparseCableConnectionType;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.cables.power.PowerCableComponent;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.utilities.WorldUtilities;

public class WirePowerCableComponent extends PowerCableComponent {
	private final Map<BlockPos, SparseCableLink> sparseLinks;

	public WirePowerCableComponent(String name) {
		super(name, CableNetworkModuleTypes.POWER_WIRE_NETWORK_MODULE, false, StaticPowerVoltage.LOW, 100, 1);
		sparseLinks = new HashMap<BlockPos, SparseCableLink>();
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		super.initializeCableProperties(cable);
	}

	@Override
	public void onInitializedInWorld(Level world, BlockPos pos, boolean firstTimePlaced) {
		super.onInitializedInWorld(world, pos, firstTimePlaced);
		CustomRenderer.TEMP_CABLES.add(this);
	}

	@Override
	public void onOwningTileEntityRemoved() {
		CustomRenderer.TEMP_CABLES.remove(this);

		if (!isClientSide()) {
			for (SparseCableLink link : sparseLinks.values()) {
				BlockEntityBase be = (BlockEntityBase) getLevel().getBlockEntity(link.linkToPosition());
				if (be != null && be.hasComponentOfType(WirePowerCableComponent.class)) {
					WirePowerCableComponent cableComp = be.getComponent(WirePowerCableComponent.class);
					cableComp.syncConnectionStateToClient();
				}
				WorldUtilities.dropItem(getLevel(), getPos(), new ItemStack(ModItems.CoilCopper.get()));
			}
		}

		super.onOwningTileEntityRemoved();
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		return CableConnectionState.NONE;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	protected ServerCable createCable() {
		return new ServerCable(getLevel(), getPos(), true, getSupportedNetworkModuleTypes());
	}

	public boolean isConnectedTo(BlockPos location) {
		if (!isClientSide()) {
			ServerCable trakcedCable = CableNetworkManager.get(getLevel()).getCable(location);
			if (trakcedCable != null) {
				return trakcedCable.isLinkedTo(location);
			}
		}
		return this.sparseLinks.containsKey(location);
	}

	public boolean addConnectedConnector(BlockPos location) {
		if (!isClientSide()) {
			ServerCable trakcedCable = this.getCable().get();
			if (trakcedCable != null && trakcedCable.addSparseLink(location)) {
				syncConnectionStateToClient();

				BlockEntityBase be = (BlockEntityBase) getLevel().getBlockEntity(location);
				if (be != null && be.hasComponentOfType(WirePowerCableComponent.class)) {
					WirePowerCableComponent cableComp = be.getComponent(WirePowerCableComponent.class);
					cableComp.syncConnectionStateToClient();
				}

				return true;
			}
		}
		return false;
	}

	public boolean removeConnectedConnector(BlockPos location) {
		if (!isClientSide()) {
			ServerCable trakcedCable = CableNetworkManager.get(getLevel()).getCable(location);
			if (trakcedCable != null && trakcedCable.removeSparseLink(location)) {
				syncConnectionStateToClient();

				BlockEntityBase be = (BlockEntityBase) getLevel().getBlockEntity(location);
				if (be != null && be.hasComponentOfType(WirePowerCableComponent.class)) {
					WirePowerCableComponent cableComp = be.getComponent(WirePowerCableComponent.class);
					cableComp.syncConnectionStateToClient();
				}

				return true;
			}
		}
		return false;
	}

	public void syncConnectionStateToClient() {
		if (!isClientSide()) {
			ServerCable trakcedCable = CableNetworkManager.get(getLevel()).getCable(getPos());

			sparseLinks.clear();
			for (SparseCableLink link : trakcedCable.getSparseLinks()) {
				sparseLinks.put(link.linkToPosition(), link);
			}

			WireConnectionSyncPacket syncPacket = new WireConnectionSyncPacket(getPos(), trakcedCable.getSparseLinks());
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 64, syncPacket);
		} else {
			throw new RuntimeException("We should not be attempting to sync the connection state from the client!");
		}
	}

	public void syncConnectionStateFromServer(Collection<SparseCableLink> links) {
		if (isClientSide()) {
			sparseLinks.clear();
			for (SparseCableLink link : links) {
				sparseLinks.put(link.linkToPosition(), link);
			}
		} else {
			throw new RuntimeException("We should not be receiving the connection state sync on the server!");
		}
	}

	public void renderConnections(PoseStack pose) {
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.lineWidth(10);
		Color startcolor = new Color(1, 0.7f, 0.1f);
		Color endColor = startcolor;

		for (SparseCableLink link : sparseLinks.values()) {
			if (link.connectionType() == SparseCableConnectionType.ENDING) {
				Vector3D start = new Vector3D(getPos().getX() + 0.5f, getPos().getY() + 0.5f, getPos().getZ() + 0.5f);
				Vector3D end = new Vector3D(link.linkToPosition().getX() + 0.5f, link.linkToPosition().getY() + 0.5f, link.linkToPosition().getZ() + 0.5f);
				Vector3D normal = new Vector3D(1, 0, 0);

				Tesselator tessellator = Tesselator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuilder();
				bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
				bufferbuilder.vertex(pose.last().pose(), start.getX(), start.getY(), start.getZ())
						.color(startcolor.getRed(), startcolor.getGreen(), startcolor.getBlue(), startcolor.getAlpha()).normal(normal.getX(), normal.getY(), 0).endVertex();
				bufferbuilder.vertex(pose.last().pose(), end.getX(), end.getY(), end.getZ()).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha())
						.normal(normal.getX(), normal.getY(), 0).endVertex();
				tessellator.end();
			}
		}

		RenderSystem.enableCull();
	}
}
