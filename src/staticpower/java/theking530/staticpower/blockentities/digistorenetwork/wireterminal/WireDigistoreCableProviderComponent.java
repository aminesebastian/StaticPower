package theking530.staticpower.blockentities.digistorenetwork.wireterminal;

import java.util.Collection;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.SparseCableLink;
import theking530.staticcore.cablenetwork.SparseCableLink.SparseCableConnectionType;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.power.wireconnector.WirePowerCableComponent;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.rendering.WireRenderCache;
import theking530.staticpower.client.rendering.renderers.WireRenderer;
import theking530.staticpower.items.wirecoils.PowerWireCoil;

public class WireDigistoreCableProviderComponent extends DigistoreCableProviderComponent {

	public WireDigistoreCableProviderComponent(String name) {
		super(name);
	}

	@Override
	protected void initializeCableProperties(Cable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		super.initializeCableProperties(cable, context, state, placer, stack);

		Direction attachToSide = state.getValue(StaticPowerBlock.FACING).getOpposite();
		for (Direction side : Direction.values()) {
			if (side != attachToSide) {
				cable.setDisabledStateOnSide(side, true);
			}
		}
	}

	@Override
	public void setSideDisabledState(Direction side, boolean disabledState) {
		// DO NOT DELETE THIS, WE CLEAR THIS METHOD OUT FOR A REASON.
		// Wire connectors should not have their sides configurable.
		// TODO: Expose a flag on the ServerCable to disable side configuration.
	}

	@Override
	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		if (!isClientSide()) {
			Cable cable = CableNetworkAccessor.get(getLevel()).getCable(getPos());
			for (SparseCableLink link : cable.getSparseLinks()) {
				ItemStack wireStack = ItemStack.of(link.data().getCompound("wire"));
				WorldUtilities.dropItem(getLevel(), getPos(), wireStack);
			}
		} else {
			WireRenderer.removeWireRenderCache(getPos());
		}

		super.onOwningBlockEntityBroken(state, newState, isMoving);
	}

	@Override
	public void clientSparseLinkArrayChanged() {
		// Clear out any cached values.
		WireRenderer.removeWireRenderCache(getPos());
		addSparseLinksToRenderer(getSparseLinks());
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
	public boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	public boolean isSpraseCable() {
		return true;
	}

	@Override
	public void onOwningBlockEntityLoaded(Level level, BlockPos pos, BlockState state) {
		super.onOwningBlockEntityLoaded(level, pos, state);

		if (getLevel().isClientSide()) {
			addSparseLinksToRenderer(getSparseLinks());
		}
	}

	public Vec3 getWireAttachLocation() {
		Vector3D normal = new Vector3D(getBlockEntity().getFacingDirection().getNormal());
		normal.multiply(0.2f);
		return new Vec3(getPos().getX() + 0.5f - normal.getX(), getPos().getY() + 0.5f - normal.getY(), getPos().getZ() + 0.5f - normal.getZ());
	}

	private void addSparseLinksToRenderer(Collection<SparseCableLink> links) {
		for (SparseCableLink link : links) {
			if (link.connectionType() == SparseCableConnectionType.ENDING) {
				// Try to get the start. If we can't, stop.
				BlockEntityBase startBe = (BlockEntityBase) getLevel().getBlockEntity(link.linkToPosition());
				if (startBe == null) {
					StaticPower.LOGGER.error(String.format("Encountered ending SparseCableLink with a null starting BlockEntity at location: %1$s.", getPos().toString()));
					continue;
				}

				// Capture the start, end, and color values and then push the render request.
				Vec3 start = startBe.getComponent(WirePowerCableComponent.class).getWireAttachLocation();
				Vec3 end = getWireAttachLocation().add(0.001f, 0.001f, 0.001f);
				ItemStack wireStack = ItemStack.of(link.data().getCompound("wire"));
				SDColor color = ((PowerWireCoil) wireStack.getItem()).getColor();
				float thickness = ((PowerWireCoil) wireStack.getItem()).getWireThickness();

				WireRenderer.addWireRenderCache(new WireRenderCache(link.linkId(), startBe.getBlockPos(), getPos(), start, end, color, thickness, 5));
			}
		}
	}

}
