package theking530.staticpower.tileentities.nonpowered.conveyors.hopper;

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

public class BlockConveyorHopper extends AbstractConveyorBlock {

	public BlockConveyorHopper(String name) {
		super(name);
	}

	@Override
	public void cacheVoxelShapes() {
		for (Direction dir : Direction.values()) {
			VoxelShape shape = VoxelShapes.combine(Block.makeCuboidShape(0, 0, 0, 4, 8, 16), Block.makeCuboidShape(12, 0, 0, 16, 8, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 0, 0, 16, 8, 4), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 0, 12, 16, 8, 16), IBooleanFunction.OR);
			ENTITY_SHAPES.put(dir, shape);
			INTERACTION_SHAPES.put(dir, shape);
		}
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityConveyorHopper.TYPE.create();
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
