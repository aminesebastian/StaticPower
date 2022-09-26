package theking530.staticpower.blocks.tileentity;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import theking530.api.IBreakSerializeable;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent.SideIncrementDirection;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class StaticPowerBlockEntityBlock extends StaticPowerBlock implements EntityBlock {
	protected enum HasGuiType {
		NEVER, ALWAYS, SNEAKING_ONLY;
	}

	protected boolean shouldDropContents;
	public final ResourceLocation tier;

	protected StaticPowerBlockEntityBlock() {
		this(null, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticPowerBlockEntityBlock(ResourceLocation tier) {
		this(tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticPowerBlockEntityBlock(Properties properties) {
		this(null, properties);
	}

	protected StaticPowerBlockEntityBlock(ResourceLocation tier, Properties properties) {
		super(properties);
		this.shouldDropContents = true;
		this.tier = tier;
		if (getFacingType() != null) {
			this.registerDefaultState(stateDefinition.any().setValue(getFacingType(), Direction.NORTH));
		}
	}

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
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);

		if (te != null) {
			// System.out.println(side);
			HasGuiType guiType = hasGuiScreen(te, state, world, pos, player, hand, hit);
			// Ensure we meet the criteria for entering the GUI.
			if (guiType == HasGuiType.ALWAYS || (guiType == HasGuiType.SNEAKING_ONLY && player.isShiftKeyDown())) {
				if (!world.isClientSide) {
					enterGuiScreen(te, state, world, te.getContainerReferencedBlockPos(), player, hand, hit);
				}
				te.onGuiEntered(state, player, hand, hit);
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	@Nullable
	public abstract BlockEntity newBlockEntity(final BlockPos pos, final BlockState state);

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return BlockEntityBase::tick;
	}

	/**
	 * Server side only. This method givens the inheritor the opportunity to enter a
	 * GUI.By default, the standard GUI opening flow is followed calling openGui on
	 * {@link NetworkHooks}.
	 * 
	 * @param tileEntity
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @param hand
	 * @param hit
	 * @return
	 */
	public void enterGuiScreen(BlockEntityBase tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide) {
			NetworkHooks.openGui((ServerPlayer) player, tileEntity, pos);
		}
	}

	/**
	 * If the current activation situation matches the returned value here, it will
	 * cause the activation method to call
	 * {@link #enterGuiScreen(TileEntity, BlockState, World, BlockPos, PlayerEntity, Hand, BlockRayTraceResult)}
	 * and enter a GUI for the tile entity this block represents. OVerride that
	 * method if any additional checks must be performed when entering a Gui.
	 * 
	 * @param tileEntity
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @param hand
	 * @param hit
	 * @return
	 */
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
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
			BlockEntityBase TE = (BlockEntityBase) world.getBlockEntity(pos);
			if (TE.hasComponentOfType(SideConfigurationComponent.class)) {
				TE.getComponent(SideConfigurationComponent.class).modulateWorldSpaceSideMode(facing, SideIncrementDirection.FORWARD);
				return InteractionResult.SUCCESS;
			}
			return super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		}
	}

	@Override
	public InteractionResult sneakWrenchBlock(Player player, SneakWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		// If we're on the server and this machine has a tile entity of type
		// IBreakSerializeable.
		if (!world.isClientSide && world.getBlockEntity(pos) instanceof IBreakSerializeable) {
			ItemStack machineStack = IBreakSerializeable.createItemDrop(this, player, world, pos);
			// Drop the item.
			WorldUtilities.dropItem(world, pos, machineStack);

			// Swap this block to air (break it).
			world.removeBlockEntity(pos);
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return super.sneakWrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
	}

	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity tileEntity = blockAccess.getBlockEntity(pos);
		if (tileEntity instanceof BlockEntityBase) {
			return ((BlockEntityBase) tileEntity).getWeakPower(blockState, blockAccess, pos, side);
		}
		return 0;
	}

	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity tileEntity = blockAccess.getBlockEntity(pos);
		if (tileEntity instanceof BlockEntityBase) {
			return ((BlockEntityBase) tileEntity).getStrongPower(blockState, blockAccess, pos, side);
		}
		return 0;
	}
}