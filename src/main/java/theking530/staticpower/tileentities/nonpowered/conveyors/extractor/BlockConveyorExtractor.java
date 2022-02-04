package theking530.staticpower.tileentities.nonpowered.conveyors.extractor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorBlock;

public class BlockConveyorExtractor extends AbstractConveyorBlock {

	public BlockConveyorExtractor(String name) {
		super(name);
	}

	@Override
	public void cacheVoxelShapes() {
		VoxelShape east = Shapes.joinUnoptimized(Block.box(0, 0, 0, 16, 8, 16), Block.box(8, 14, 0, 16, 16, 16), BooleanOp.OR);
		east = Shapes.joinUnoptimized(east, Block.box(15, 0, 0, 16, 16, 16), BooleanOp.OR);
		east = Shapes.joinUnoptimized(east, Block.box(8, 0, 14, 16, 16, 16), BooleanOp.OR);
		east = Shapes.joinUnoptimized(east, Block.box(8, 0, 0, 16, 16, 2), BooleanOp.OR);
		ENTITY_SHAPES.put(Direction.EAST, east);
		INTERACTION_SHAPES.put(Direction.EAST, east);

		VoxelShape west = Shapes.joinUnoptimized(Block.box(0, 0, 0, 16, 8, 16), Block.box(0, 14, 0, 8, 16, 16), BooleanOp.OR);
		west = Shapes.joinUnoptimized(west, Block.box(0, 0, 0, 1, 16, 16), BooleanOp.OR);
		west = Shapes.joinUnoptimized(west, Block.box(0, 0, 14, 8, 16, 16), BooleanOp.OR);
		west = Shapes.joinUnoptimized(west, Block.box(0, 0, 0, 8, 16, 2), BooleanOp.OR);
		ENTITY_SHAPES.put(Direction.WEST, west);
		INTERACTION_SHAPES.put(Direction.WEST, west);

		VoxelShape south = Shapes.joinUnoptimized(Block.box(0, 0, 0, 16, 8, 16), Block.box(0, 14, 8, 16, 16, 16), BooleanOp.OR);
		south = Shapes.joinUnoptimized(south, Block.box(0, 0, 15, 16, 16, 16), BooleanOp.OR);
		south = Shapes.joinUnoptimized(south, Block.box(14, 0, 8, 16, 16, 16), BooleanOp.OR);
		south = Shapes.joinUnoptimized(south, Block.box(0, 0, 8, 2, 16, 16), BooleanOp.OR);
		ENTITY_SHAPES.put(Direction.SOUTH, south);
		INTERACTION_SHAPES.put(Direction.SOUTH, south);

		VoxelShape north = Shapes.joinUnoptimized(Block.box(0, 0, 0, 16, 8, 16), Block.box(0, 14, 0, 16, 16, 8), BooleanOp.OR);
		north = Shapes.joinUnoptimized(north, Block.box(0, 0, 0, 16, 16, 1), BooleanOp.OR);
		north = Shapes.joinUnoptimized(north, Block.box(14, 0, 0, 16, 16, 8), BooleanOp.OR);
		north = Shapes.joinUnoptimized(north, Block.box(0, 0, 0, 2, 16, 8), BooleanOp.OR);
		ENTITY_SHAPES.put(Direction.NORTH, north);
		INTERACTION_SHAPES.put(Direction.NORTH, north);
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return TileEntityConveyorExtractor.TYPE.create(pos, state);
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new TranslatableComponent("gui.staticpower.experience_hopper_tooltip").withStyle(ChatFormatting.GREEN));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.experience_hopper_description")).withStyle(ChatFormatting.BLUE));
	}
}
