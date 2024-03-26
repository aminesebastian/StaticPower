package theking530.staticcore.blockentity.components.multiblock;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityUpdateRequest;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.multiblock.manager.IMultiblockManager;
import theking530.staticcore.blockentity.components.multiblock.manager.MultiblockManager;
import theking530.staticcore.blockentity.components.multiblock.manager.ServerMultiblockManager;

public class MultiblockComponent extends AbstractBlockEntityComponent {
	private AbstractMultiblockPattern pattern;
	private Consumer<MultiblockState> stateChangedCallback;

	public MultiblockComponent(String name, AbstractMultiblockPattern pattern) {
		super(name);
		this.pattern = pattern;
	}

	public MultiblockState getState() {
		IMultiblockManager mbManager = MultiblockManager.get(getLevel());
		if (mbManager != null) {
			MultiblockState state = mbManager.getMultiblockState(getPos());
			if (state != null) {
				return state;
			}
		}
		return MultiblockState.FAILED;
	}

	public AbstractMultiblockPattern getPattern() {
		return pattern;
	}

	public boolean isWellFormed() {
		return getState().isWellFormed();
	}

	@Override
	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onOwningBlockEntityBroken(state, newState, isMoving);
		if (isMaster() && !isClientSide()) {
			ServerMultiblockManager mbManager = MultiblockManager.get((ServerLevel) getLevel());
			mbManager.onMultiblockMasterBroken(getPos());
		}
	}

	public void addedToMultiblock(MultiblockState state) {
		getBlockEntity().addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(false), true);
	}

	public void onMultiblockStateChanged(MultiblockState oldState, MultiblockState newState) {
		if (stateChangedCallback != null) {
			stateChangedCallback.accept(newState);
		}
	}

	public void onRemovedFromMultiblock(MultiblockState state) {

	}

	public BlockPos getMasterPosition() {
		return getState().getMasterPos();
	}

	public boolean isMaster() {
		if (!isWellFormed()) {
			return false;
		}
		// check in this direction because getMasterPos() can be null.
		return getPos().equals(getState().getMasterPos());
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
	}

	public MultiblockComponent setStateChangedCallback(Consumer<MultiblockState> stateChangedCallback) {
		this.stateChangedCallback = stateChangedCallback;
		return this;
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		return super.serializeUpdateNbt(nbt, fromUpdate);
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
	}
}
