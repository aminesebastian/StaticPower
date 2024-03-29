package theking530.staticpower.blockentities.nonpowered.conveyors.hopper;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlock;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;

public class BlockConveyorHopper extends AbstractConveyorBlock {
	protected static VoxelShape PASSED_FILTER_SHAPE;
	protected static final VoxelShape NOT_PASSED_FILTER_SHAPE = Block.box(0, 0, 0, 16, 8, 16);
	protected final boolean filtered;

	static {
		PASSED_FILTER_SHAPE = Shapes.joinUnoptimized(Block.box(0, 0, 0, 4, 8, 16), Block.box(12, 0, 0, 16, 8, 16), BooleanOp.OR);
		PASSED_FILTER_SHAPE = Shapes.joinUnoptimized(PASSED_FILTER_SHAPE, Block.box(0, 0, 0, 16, 8, 4), BooleanOp.OR);
		PASSED_FILTER_SHAPE = Shapes.joinUnoptimized(PASSED_FILTER_SHAPE, Block.box(0, 0, 12, 16, 8, 16), BooleanOp.OR);
	}

	public BlockConveyorHopper(ResourceLocation tier, boolean filtered) {
		super(tier);
		this.filtered = filtered;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return filtered ? HasGuiType.ALWAYS : HasGuiType.NEVER;
	}

	@Override
	public void cacheVoxelShapes() {
		for (Direction dir : Direction.values()) {
			// Put the passed shapes in as default.
			ENTITY_SHAPES.put(dir, PASSED_FILTER_SHAPE);
			INTERACTION_SHAPES.put(dir, PASSED_FILTER_SHAPE);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		// If not filtered, just return the super call.
		return super.getShape(state, worldIn, pos, context);
	}

	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		// If we're a filtering hopper and the entity is an item.
		if (context instanceof EntityCollisionContext) {
			// Get the item over the hole.
			EntityCollisionContext eCtx = (EntityCollisionContext) context;
			if (eCtx.getEntity() instanceof ConveyorBeltEntity) {
				ConveyorBeltEntity entity = (ConveyorBeltEntity) eCtx.getEntity();
				if (filtered && entity != null) {
					// If the context is null (then an item entity), and the hopper tile entity is
					// still there.
					if (worldIn.getBlockEntity(pos) != null) {
						// Get the hopper and the item entity.
						BlockEntityConveyorHopper conveyor = (BlockEntityConveyorHopper) worldIn.getBlockEntity(pos);

						// For the item, check if the item passes the filter, if true, use the regular
						// shape, if not, cover the hole.
						return conveyor.doesItemPassFilter(entity) ? super.getShape(state, worldIn, pos, context) : NOT_PASSED_FILTER_SHAPE;
					}
				}
			}
		}

		return super.getCollisionShape(state, worldIn, pos, context);
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (filtered) {
			return BlockEntityConveyorHopper.FILTERED_TYPE.create(pos, state);
		} else {
			return BlockEntityConveyorHopper.TYPE.create(pos, state);
		}
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
