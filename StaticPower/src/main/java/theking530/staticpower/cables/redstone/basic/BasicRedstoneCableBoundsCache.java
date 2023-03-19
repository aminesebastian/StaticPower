package theking530.staticpower.cables.redstone.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.utilities.math.Vector3D;

public class BasicRedstoneCableBoundsCache extends CableBoundsCache {
	private final Vector3D inputDefaultAttachmentBounds;
	private final Vector3D outputDefaultAttachmentBounds;

	public BasicRedstoneCableBoundsCache(double cableRadius, Vector3D inputDefaultAttachmentBounds, Vector3D outputDefaultAttachmentBounds) {
		super(cableRadius, null);
		this.inputDefaultAttachmentBounds = inputDefaultAttachmentBounds;
		this.outputDefaultAttachmentBounds = outputDefaultAttachmentBounds;
	}

	@Override
	protected Vector3D getDefaultAttachmentBounds(BlockGetter world, BlockPos pos, Direction side) {
		// Get the component at the location.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component instanceof RedstoneCableComponent) {
			RedstoneCableComponent redstoneCable = (RedstoneCableComponent) component;
			return redstoneCable.getConfiguration().getSideConfig(side).isInputSide() ? inputDefaultAttachmentBounds : outputDefaultAttachmentBounds;
		}
		return inputDefaultAttachmentBounds;
	}
}
