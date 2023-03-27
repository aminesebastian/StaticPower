package theking530.staticpower.blockentities.nonpowered.conveyors.straight;

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
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlock;

public class BlockStraightConveyor extends AbstractConveyorBlock {
	public BlockStraightConveyor(ResourceLocation tier) {
		super(tier);
	}

	@Override
	public void cacheVoxelShapes() {
		for (Direction dir : Direction.values()) {
			ENTITY_SHAPES.put(dir, Block.box(0, 0, 0, 16, 8, 16));
			INTERACTION_SHAPES.put(dir, Block.box(0, 0, 0, 16, 8, 16));
		}
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityStraightConveyor.TYPE.create(pos, state);
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
