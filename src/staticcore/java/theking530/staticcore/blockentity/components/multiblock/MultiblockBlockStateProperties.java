package theking530.staticcore.blockentity.components.multiblock;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class MultiblockBlockStateProperties {
	public static final BooleanProperty IS_MASTER = BooleanProperty.create("is_multiblock_master");
	public static final BooleanProperty IS_IN_VALID_MULTIBLOCK = BooleanProperty.create("is_in_valid_multiblock");

}
