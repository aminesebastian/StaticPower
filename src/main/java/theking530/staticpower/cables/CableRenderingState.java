package theking530.staticpower.cables;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class CableRenderingState {
	/**
	 * Cache for the connection states. This is updated every time a new baked model
	 * is requested AND also, on first placement.
	 */
	public final CableConnectionState[] connectionStates;
	/** Container for all the attachments on this cable. */
	public final ResourceLocation[] attachments;
	public final ItemStack[] attachmentItems;
	/** Container for all the covers on this cable. */
	public final BlockState[] covers;
	public final boolean[] disabledSides;
	public final BlockPos pos;
	public BlockAndTintGetter world;

	public CableRenderingState(CableConnectionState[] connectionStates, ResourceLocation[] attachments, ItemStack[] attachmentItems, ItemStack[] covers, boolean[] disabledSides,
			BlockPos pos) {
		super();
		this.connectionStates = connectionStates;
		this.attachments = attachments;
		this.attachmentItems = attachmentItems;
		// Translate the itemstacks into block states.
		this.covers = new BlockState[6];
		for (int i = 0; i < covers.length; i++) {
			if (covers[i].isEmpty()) {
				this.covers[i] = null;
			} else {
				this.covers[i] = CableCover.getBlockStateForCover(covers[i]);
			}
		}

		this.disabledSides = disabledSides;
		this.pos = pos;
	}

	public boolean hasCoverOnSide(Direction side) {
		return covers[side.ordinal()] != null;
	}

	public void setRenderingWorld(BlockAndTintGetter world) {
		this.world = world;
	}
}
