package theking530.staticpower.blocks;

public class BurntLog extends StaticPowerRotatePillarBlock {

	public BurntLog(Properties properties) {
		super(properties);
	}

	@Override
	public BlockDropType getBlockDropType() {
		return BlockDropType.CUSTOM;
	}
}
