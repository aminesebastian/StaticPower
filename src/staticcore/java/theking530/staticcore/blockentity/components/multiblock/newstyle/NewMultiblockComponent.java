package theking530.staticcore.blockentity.components.multiblock.newstyle;

import java.util.function.Consumer;

import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.multiblock.IMultiBlockComponent;
import theking530.staticcore.blockentity.components.multiblock.MultiBlockEntry;

public class NewMultiblockComponent<T extends BlockEntity> extends AbstractBlockEntityComponent
		implements IMultiBlockComponent<T> {
	private AbstractMultiblockPattern pattern;
	private MultiBlockEntry<T> token;
	private MultiblockState state;
	private Consumer<MultiblockState> stateChangedCallback;

	public NewMultiblockComponent(String name, AbstractMultiblockPattern pattern) {
		super(name);
		this.pattern = pattern;
		this.state = MultiblockState.FAILED;
	}

	public AbstractMultiblockPattern getPattern() {
		return pattern;
	}

	public boolean isWellFormed() {
		return state.isWellFormed();
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		MultiblockState newState = pattern.checkWellFormed(getLevel(), getPos());
		if (!newState.equals(state)) {
			state = newState;
			if (stateChangedCallback != null) {
				stateChangedCallback.accept(state);
			}
			
			// If we're not well formed, remove this component.
			if(!state.isWellFormed()) {
				this.getBlockEntity().removeComponent(this);
			}
		}
	}

	public NewMultiblockComponent<T> setStateChangedCallback(Consumer<MultiblockState> stateChangedCallback) {
		this.stateChangedCallback = stateChangedCallback;
		return this;
	}

	@Override
	public void multiBlockValidated(MultiBlockEntry<T> token) {
		this.token = token;
	}

	@Override
	public void multiBlockBroken() {
		token = null;
	}

	@Override
	public MultiBlockEntry<T> getToken() {
		return token;
	}

	@Override
	public void onOwningBlockEntityUnloaded() {
		if (hasToken()) {
			getToken().remove();
		}
	}
}
