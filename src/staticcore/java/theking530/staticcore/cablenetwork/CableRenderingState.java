package theking530.staticcore.cablenetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.cablenetwork.attachment.CableCover;
import theking530.staticcore.cablenetwork.data.ClientCableConnectionState;

public class CableRenderingState {
	/**
	 * Cache for the connection states. This is updated every time a new baked model
	 * is requested AND also, on first placement.
	 */
	private final ClientCableConnectionState[] connectionStates;
	private final ResourceLocation[] attachmentModels;
	private final BlockState[] coverBlockStates;
	private final BlockPos pos;
	private BlockAndTintGetter level;

	public CableRenderingState(ClientCableConnectionState[] connectionStates, ResourceLocation[] attachmentModels, BlockPos pos) {
		super();
		this.connectionStates = connectionStates;
		this.attachmentModels = attachmentModels;
		this.pos = pos;
		this.coverBlockStates = new BlockState[6];
		for (Direction side : Direction.values()) {
			// No need to check for #hasCover, this returns null for empty covers.
			ClientCableConnectionState state = connectionStates[side.ordinal()];
			coverBlockStates[side.ordinal()] = CableCover.getBlockStateForCover(state.getCover());
		}
	}

	public boolean hasCover(Direction side) {
		return connectionStates[side.ordinal()].hasCover();
	}

	public ItemStack getCover(Direction side) {
		return connectionStates[side.ordinal()].getCover();
	}

	public BlockState getCoverBlockState(Direction side) {
		return coverBlockStates[side.ordinal()];
	}

	public boolean hasAttachment(Direction side) {
		return connectionStates[side.ordinal()].hasAttachment();
	}

	public ItemStack getAttachment(Direction side) {
		return connectionStates[side.ordinal()].getAttachment();
	}

	public ResourceLocation getAttachmentModelId(Direction side) {
		return attachmentModels[side.ordinal()];
	}

	public BlockPos getCableBlockPos() {
		return pos;
	}

	public BlockAndTintGetter getLevel() {
		return level;
	}

	public void setRenderingLevel(BlockAndTintGetter level) {
		this.level = level;
	}
}
