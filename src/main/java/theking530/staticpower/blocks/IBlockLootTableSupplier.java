package theking530.staticpower.blocks;

public interface IBlockLootTableSupplier {
	public enum BlockDropType {
		NONE, SELF, CUSTOM
	}

	public default BlockDropType getBlockDropType() {
		return BlockDropType.SELF;
	}
}
