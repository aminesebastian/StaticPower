package theking530.staticcore.blockentity.components.multiblock.newstyle;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticcore.blockentity.BlockEntityUpdateRequest;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;

public class NewMultiblockComponent<T extends BlockEntity> extends AbstractBlockEntityComponent {
	private MultiblockState state;

	private AbstractMultiblockPattern pattern;
	private Consumer<MultiblockState> stateChangedCallback;

	public NewMultiblockComponent(String name, AbstractMultiblockPattern pattern, MultiblockState initialState) {
		super(name);
		this.pattern = pattern;
		this.state = initialState;
	}

	public MultiblockState getState() {
		return state;
	}

	public AbstractMultiblockPattern getPattern() {
		return pattern;
	}

	public boolean isWellFormed() {
		return state.isWellFormed();
	}

	public void addedToMultiblock(MultiblockState state) {
		this.state = state;
		this.getBlockEntity().addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(false), true);
	}

	protected void onRemovedFromMultiblock(MultiblockState state) {
		this.state = state;
	}

	public BlockPos getMasterPosition() {
		return state.getMasterPos();
	}

	public boolean isMaster() {
		// check in this direction because getMasterPos() can be null.
		return getPos().equals(state.getMasterPos());
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();

		if (getLevel().isClientSide()) {
			return;
		}

		if (isMaster()) {
			MultiblockState newMultiblockState = pattern.isStateStillValid(state, getLevel());
			if (!newMultiblockState.equals(state)) {
				if (stateChangedCallback != null) {
					stateChangedCallback.accept(state);
				}

				if (!newMultiblockState.isWellFormed()) {
					pattern.onMultiblockBroken(state, getLevel());
				}
				state = newMultiblockState;
			}
		}
	}

	public NewMultiblockComponent<T> setStateChangedCallback(Consumer<MultiblockState> stateChangedCallback) {
		this.stateChangedCallback = stateChangedCallback;
		return this;
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		CompoundTag parent = super.serializeUpdateNbt(nbt, fromUpdate);
		parent.put("mb_state", state.serialize());
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		state = MultiblockState.deserialize(nbt.getCompound("mb_state"));
	}
}
