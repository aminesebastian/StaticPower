package theking530.staticpower.tileentities.nonpowered.conveyors.extractor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorBlock;

public class BlockConveyorExtractor extends AbstractConveyorBlock {

	public BlockConveyorExtractor(String name) {
		super(name);
	}

	@Override
	public void cacheVoxelShapes() {
		VoxelShape east = VoxelShapes.combine(Block.makeCuboidShape(0, 0, 0, 16, 8, 16), Block.makeCuboidShape(8, 14, 0, 16, 16, 16), IBooleanFunction.OR);
		east = VoxelShapes.combine(east, Block.makeCuboidShape(15, 0, 0, 16, 16, 16), IBooleanFunction.OR);
		east = VoxelShapes.combine(east, Block.makeCuboidShape(8, 0, 14, 16, 16, 16), IBooleanFunction.OR);
		east = VoxelShapes.combine(east, Block.makeCuboidShape(8, 0, 0, 16, 16, 2), IBooleanFunction.OR);
		ENTITY_SHAPES.put(Direction.EAST, east);
		INTERACTION_SHAPES.put(Direction.EAST, east);

		VoxelShape west = VoxelShapes.combine(Block.makeCuboidShape(0, 0, 0, 16, 8, 16), Block.makeCuboidShape(0, 14, 0, 8, 16, 16), IBooleanFunction.OR);
		west = VoxelShapes.combine(west, Block.makeCuboidShape(0, 0, 0, 1, 16, 16), IBooleanFunction.OR);
		west = VoxelShapes.combine(west, Block.makeCuboidShape(0, 0, 14, 8, 16, 16), IBooleanFunction.OR);
		west = VoxelShapes.combine(west, Block.makeCuboidShape(0, 0, 0, 8, 16, 2), IBooleanFunction.OR);
		ENTITY_SHAPES.put(Direction.WEST, west);
		INTERACTION_SHAPES.put(Direction.WEST, west);

		VoxelShape south = VoxelShapes.combine(Block.makeCuboidShape(0, 0, 0, 16, 8, 16), Block.makeCuboidShape(0, 14, 8, 16, 16, 16), IBooleanFunction.OR);
		south = VoxelShapes.combine(south, Block.makeCuboidShape(0, 0, 15, 16, 16, 16), IBooleanFunction.OR);
		south = VoxelShapes.combine(south, Block.makeCuboidShape(14, 0, 8, 16, 16, 16), IBooleanFunction.OR);
		south = VoxelShapes.combine(south, Block.makeCuboidShape(0, 0, 8, 2, 16, 16), IBooleanFunction.OR);
		ENTITY_SHAPES.put(Direction.SOUTH, south);
		INTERACTION_SHAPES.put(Direction.SOUTH, south);

		VoxelShape north = VoxelShapes.combine(Block.makeCuboidShape(0, 0, 0, 16, 8, 16), Block.makeCuboidShape(0, 14, 0, 16, 16, 8), IBooleanFunction.OR);
		north = VoxelShapes.combine(north, Block.makeCuboidShape(0, 0, 0, 16, 16, 1), IBooleanFunction.OR);
		north = VoxelShapes.combine(north, Block.makeCuboidShape(14, 0, 0, 16, 16, 8), IBooleanFunction.OR);
		north = VoxelShapes.combine(north, Block.makeCuboidShape(0, 0, 0, 2, 16, 8), IBooleanFunction.OR);
		ENTITY_SHAPES.put(Direction.NORTH, north);
		INTERACTION_SHAPES.put(Direction.NORTH, north);
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityConveyorExtractor.TYPE.create();
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new TranslationTextComponent("gui.staticpower.experience_hopper_tooltip").mergeStyle(TextFormatting.GREEN));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.experience_hopper_description")).mergeStyle(TextFormatting.BLUE));
	}
}
