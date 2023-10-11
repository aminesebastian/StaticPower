package theking530.staticcore.blockentity.components.multiblock.newstyle;

public class MultiblockType<T extends AbstractMultiblockPattern> {
	private T pattern;

	public MultiblockType(T pattern) {
		this.pattern = pattern;
	}

	public T getPattern() {
		return pattern;
	}
}
