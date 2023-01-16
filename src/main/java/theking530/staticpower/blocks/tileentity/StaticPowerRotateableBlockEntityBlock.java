package theking530.staticpower.blocks.tileentity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import theking530.api.IBreakSerializeable;
import theking530.api.wrench.RegularWrenchMode;

public abstract class StaticPowerRotateableBlockEntityBlock extends StaticPowerBlockEntityBlock {

	protected StaticPowerRotateableBlockEntityBlock() {
		super();
	}

	protected StaticPowerRotateableBlockEntityBlock(ResourceLocation tier) {
		this(tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticPowerRotateableBlockEntityBlock(Properties properties) {
		this(null, properties);
	}

	protected StaticPowerRotateableBlockEntityBlock(ResourceLocation tier, Properties properties) {
		super(tier, properties);
	}

	@Override
	protected BlockState getDefaultState() {
		if (getFacingType() != null) {
			return stateDefinition.any().setValue(getFacingType(), Direction.NORTH);
		}
		return super.getDefaultState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		if (getFacingType() != null) {
			builder.add(getFacingType());
		}
	}

	@Nullable
	public DirectionProperty getFacingType() {
		return HORIZONTAL_FACING;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = super.getStateForPlacement(context);
		if (getFacingType() != null) {
			if (getFacingType() == HORIZONTAL_FACING) {
				return blockstate.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
			} else if (getFacingType() == FACING) {
				return blockstate.setValue(FACING, context.getClickedFace());
			}
		}
		return blockstate;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);

		if (world.getBlockEntity(pos) != null) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te instanceof IBreakSerializeable) {
				IBreakSerializeable.deserializeToTileEntity(world, pos, state, placer, stack);
			}
		}
	}

	/**
	 * This method can be overriden to determine a different facing direction when
	 * the block is first placed.
	 * 
	 * @param world
	 * @param pos
	 * @param state
	 * @param placer
	 * @param stack
	 */
	protected void setFacingBlockStateOnPlacement(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (mode == RegularWrenchMode.ROTATE && getFacingType() != null) {
			if (facing != Direction.UP && facing != Direction.DOWN) {
				if (facing != world.getBlockState(pos).getValue(getFacingType())) {
					world.setBlock(pos, world.getBlockState(pos).setValue(getFacingType(), facing), 1 | 2);
				} else {
					world.setBlock(pos, world.getBlockState(pos).setValue(getFacingType(), facing.getOpposite()), 1 | 2);
				}
			}
			return InteractionResult.SUCCESS;
		} else {
			return super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		}
	}
}