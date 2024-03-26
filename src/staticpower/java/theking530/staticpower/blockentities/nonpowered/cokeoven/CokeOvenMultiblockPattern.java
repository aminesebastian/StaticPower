package theking530.staticpower.blockentities.nonpowered.cokeoven;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.fixed.FixedMultiblockPattern;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.init.ModBlocks;

public class CokeOvenMultiblockPattern extends FixedMultiblockPattern {
	public CokeOvenMultiblockPattern() {
		super('c');
		addDefinition('f', ModBlocks.CokeOvenBrick);
		addDefinition('c', ModBlocks.CokeOvenBrick);
		// @formatter:off
		addLayer()
		.addRow("fff")
		.addRow("fcf")
		.addRow("fff");
		addLayer()
		.addRow("fff")
		.addRow("f f")
		.addRow("fff");
		addLayer()
		.addRow("fff")
		.addRow("fff")
		.addRow("fff");
		// @formatter:on
	}

	@Override
	public void onWellFormedOnPlaceEvent(MultiblockState state, Level level) {
		super.onWellFormedOnPlaceEvent(state, level);
		List<Vector2D> offsets = new ArrayList<>();
		offsets.add(new Vector2D(1, 0));
		offsets.add(new Vector2D(0, 1));
		offsets.add(new Vector2D(2, 1));
		offsets.add(new Vector2D(1, 2));

		for (Vector2D offset : offsets) {
			BlockPos showFacePos = state.getInitialPos().offset(offset.getXi(), 1, offset.getYi());
			BlockState newState = level.getBlockState(showFacePos).setValue(BlockCokeOven.SHOW_FACE, true);
			level.setBlock(showFacePos, newState, 3);
		}
	}

	@Override
	public BlockState modifyBlockStateOnBroken(BlockState in, BlockPos pos, Level level) {
		return in.setValue(BlockCokeOven.SHOW_FACE, false);
	}
}
