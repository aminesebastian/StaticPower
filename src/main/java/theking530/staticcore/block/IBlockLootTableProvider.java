package theking530.staticcore.block;

public interface IBlockLootTableProvider {
	public enum BlockDropType {
		NONE, SELF, CUSTOM
	}

	public default BlockDropType getBlockDropType() {
		return BlockDropType.SELF;
	}
}
