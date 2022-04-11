package theking530.staticpower.tileentities.nonpowered.conveyors.supplier;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorBlock;

public class BlockConveyorSupplier extends AbstractConveyorBlock {

	public BlockConveyorSupplier(String name, ResourceLocation tier) {
		super(name, tier);
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
		if (tier == StaticPowerTiers.BASIC) {
			return TileEntityConveyorSupplier.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return TileEntityConveyorSupplier.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return TileEntityConveyorSupplier.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return TileEntityConveyorSupplier.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return TileEntityConveyorSupplier.TYPE_LUMUM.create(pos, state);
		}
		return null;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		super.getAdvancedTooltip(stack, worldIn, tooltip);
	}
}
