package theking530.staticpower.blockentities.power.wireconnector;

import java.util.List;

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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.SparseCableLink;
import theking530.staticpower.cables.SparseCableLink.SparseCableConnectionType;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.cables.power.PowerCableComponent;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.utilities.WorldUtilities;

public class WirePowerCableComponent extends PowerCableComponent {

	public WirePowerCableComponent(String name) {
		super(name, CableNetworkModuleTypes.POWER_WIRE_NETWORK_MODULE, false, StaticPowerVoltage.LOW, 100, 1);
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
			ServerCable cable = CableNetworkManager.get(getLevel()).getCable(getPos());
			for (SparseCableLink link : cable.getSparseLinks()) {
				ItemStack wireStack = ItemStack.of(link.data().getCompound("wire"));
				WorldUtilities.dropItem(getLevel(), getPos(), wireStack);
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
	protected void sparseConnectionAdded(SparseCableLink link) {

	}

	@Override
	protected void sparseConnectionsRemoved(List<SparseCableLink> links) {
		for (SparseCableLink link : links) {
			ItemStack wireStack = ItemStack.of(link.data().getCompound("wire"));
			WorldUtilities.dropItem(getLevel(), getPos(), wireStack);
		}

		if (links.size() > 0) {
			getLevel().playSound(null, getPos(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 0.5f, 0.75f);
		}
	}

	@Override
	protected ServerCable createCable() {
		return new ServerCable(getLevel(), getPos(), true, getSupportedNetworkModuleTypes());
	}

	protected Vector3D getWireAttachLocation() {
		Vector3D normal = new Vector3D(getTileEntity().getFacingDirection().getNormal());
		normal.multiply(0.2f);
		return new Vector3D(getPos().getX() + 0.5f - normal.getX(), getPos().getY() + 0.5f - normal.getY(), getPos().getZ() + 0.5f - normal.getZ());
	}

	public void renderConnections(PoseStack pose) {
		if (!isClientSide()) {
			return;
		}

		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.lineWidth(5);
		Color startcolor = new Color(1, 0.55f, 0.1f);
		Color endColor = startcolor;

		for (SparseCableLink link : getSparseLinks()) {
			if (link.connectionType() == SparseCableConnectionType.ENDING) {
				// Try to get the start. If we can't, stop.
				BlockEntityBase startBe = (BlockEntityBase) getLevel().getBlockEntity(link.linkToPosition());
				if (startBe == null) {
					StaticPower.LOGGER.error(String.format("Encountered ending SparseCableLink with a null starting BlockEntity at location: %1$s.", getPos().toString()));
					continue;
				}

				Vector3D start = startBe.getComponent(WirePowerCableComponent.class).getWireAttachLocation();
				Vector3D end = getWireAttachLocation();
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
