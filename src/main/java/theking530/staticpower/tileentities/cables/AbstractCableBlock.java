package theking530.staticpower.tileentities.cables;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import theking530.api.wrench.RegularWrenchMode;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.CableWrapperProviderComponent;
import theking530.staticpower.tileentities.network.CableBoundsCache;
import theking530.staticpower.tileentities.network.CableNetworkManager;
import theking530.staticpower.utilities.Reference;

public abstract class AbstractCableBlock extends StaticPowerBlock implements ICustomModelSupplier {
	public static final BooleanProperty CABLE_NORTH = BooleanProperty.create("cable_north");
	public static final BooleanProperty CABLE_SOUTH = BooleanProperty.create("cable_south");
	public static final BooleanProperty CABLE_EAST = BooleanProperty.create("cable_east");
	public static final BooleanProperty CABLE_WEST = BooleanProperty.create("cable_west");
	public static final BooleanProperty CABLE_UP = BooleanProperty.create("cable_up");
	public static final BooleanProperty CABLE_DOWN = BooleanProperty.create("cable_down");

	public static final BooleanProperty TILE_ENTITY_NORTH = BooleanProperty.create("attachment_north");
	public static final BooleanProperty TILE_ENTITY_SOUTH = BooleanProperty.create("attachment_south");
	public static final BooleanProperty TILE_ENTITY_EAST = BooleanProperty.create("attachment_east");
	public static final BooleanProperty TILE_ENTITY_WEST = BooleanProperty.create("attachment_west");
	public static final BooleanProperty TILE_ENTITY_UP = BooleanProperty.create("attachment_up");
	public static final BooleanProperty TILE_ENTITY_DOWN = BooleanProperty.create("attachment_down");

	public static final BooleanProperty DISABLED_NORTH = BooleanProperty.create("disabled_north");
	public static final BooleanProperty DISABLED_SOUTH = BooleanProperty.create("disabled_south");
	public static final BooleanProperty DISABLED_EAST = BooleanProperty.create("disabled_east");
	public static final BooleanProperty DISABLED_WEST = BooleanProperty.create("disabled_west");
	public static final BooleanProperty DISABLED_UP = BooleanProperty.create("disabled_up");
	public static final BooleanProperty DISABLED_DOWN = BooleanProperty.create("disabled_down");

	public final ResourceLocation StraightModel;
	public final ResourceLocation ExtensionModel;
	public final ResourceLocation AttachmentModel;

	protected final CableBoundsCache CableBounds;

	public AbstractCableBlock(String name, String modelFolder, CableBoundsCache cableBoundsGenerator) {
		super(name, Block.Properties.create(Material.IRON).notSolid());
		CableBounds = cableBoundsGenerator;
		StraightModel = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/straight");
		ExtensionModel = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/extension");
		AttachmentModel = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/attachment");

		setDefaultState(getDefaultState().with(CABLE_NORTH, false).with(CABLE_EAST, false).with(CABLE_SOUTH, false).with(CABLE_WEST, false).with(CABLE_UP, false).with(CABLE_DOWN, false)
				.with(TILE_ENTITY_NORTH, false).with(TILE_ENTITY_EAST, false).with(TILE_ENTITY_SOUTH, false).with(TILE_ENTITY_WEST, false).with(TILE_ENTITY_UP, false).with(TILE_ENTITY_DOWN, false)
				.with(DISABLED_NORTH, false).with(DISABLED_EAST, false).with(DISABLED_SOUTH, false).with(DISABLED_WEST, false).with(DISABLED_UP, false).with(DISABLED_DOWN, false));
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return CableBounds.getShape(state, worldIn, pos, context);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(CABLE_NORTH, CABLE_EAST, CABLE_SOUTH, CABLE_WEST, CABLE_UP, CABLE_DOWN, TILE_ENTITY_NORTH, TILE_ENTITY_EAST, TILE_ENTITY_SOUTH, TILE_ENTITY_WEST, TILE_ENTITY_UP, TILE_ENTITY_DOWN,
				DISABLED_NORTH, DISABLED_SOUTH, DISABLED_EAST, DISABLED_WEST, DISABLED_UP, DISABLED_DOWN);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public void registerAdditionalModels() {
		ModelLoader.addSpecialModel(StraightModel);
		ModelLoader.addSpecialModel(ExtensionModel);
		ModelLoader.addSpecialModel(AttachmentModel);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return getState(getDefaultState(), ctx.getWorld(), ctx.getPos());
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		return getState(state, world, pos);
	}

	/**
	 * Provide the cable state.
	 * 
	 * @param currentState
	 * @param world
	 * @param pos
	 * @return
	 */
	private BlockState getState(BlockState currentState, IWorld world, BlockPos pos) {
		return currentState.with(CABLE_NORTH, isConnectedToCableInDirection(world, pos, Direction.NORTH)).with(CABLE_EAST, isConnectedToCableInDirection(world, pos, Direction.EAST))
				.with(CABLE_SOUTH, isConnectedToCableInDirection(world, pos, Direction.SOUTH)).with(CABLE_WEST, isConnectedToCableInDirection(world, pos, Direction.WEST))
				.with(CABLE_UP, isConnectedToCableInDirection(world, pos, Direction.UP)).with(CABLE_DOWN, isConnectedToCableInDirection(world, pos, Direction.DOWN))
				.with(TILE_ENTITY_NORTH, isConnectedToAttachableInDirection(world, pos, Direction.NORTH)).with(TILE_ENTITY_EAST, isConnectedToAttachableInDirection(world, pos, Direction.EAST))
				.with(TILE_ENTITY_SOUTH, isConnectedToAttachableInDirection(world, pos, Direction.SOUTH)).with(TILE_ENTITY_WEST, isConnectedToAttachableInDirection(world, pos, Direction.WEST))
				.with(TILE_ENTITY_UP, isConnectedToAttachableInDirection(world, pos, Direction.UP)).with(TILE_ENTITY_DOWN, isConnectedToAttachableInDirection(world, pos, Direction.DOWN))
				.with(DISABLED_NORTH, isDisabledOnSide(world, pos, Direction.NORTH)).with(DISABLED_EAST, isDisabledOnSide(world, pos, Direction.EAST))
				.with(DISABLED_SOUTH, isDisabledOnSide(world, pos, Direction.SOUTH)).with(DISABLED_WEST, isDisabledOnSide(world, pos, Direction.WEST))
				.with(DISABLED_UP, isDisabledOnSide(world, pos, Direction.UP)).with(DISABLED_DOWN, isDisabledOnSide(world, pos, Direction.DOWN));
	}

	/**
	 * The existing model will be the core model.
	 */
	@Override
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel extensionModel = event.getModelRegistry().get(ExtensionModel);
		IBakedModel straightModel = event.getModelRegistry().get(StraightModel);
		IBakedModel attachmentModel = event.getModelRegistry().get(AttachmentModel);
		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	/**
	 * Get the cable wrapper at the provided location if one exists, otherwise
	 * returns null.
	 * 
	 * @param world The world to check for the cable wrapper component.
	 * @param pos   The location to check.
	 * @return The cable wrapper component if one is found, null otherwise.
	 */
	protected @Nullable CableWrapperProviderComponent getCableWrapperComponent(IWorld world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof TileEntityBase) {
			TileEntityBase tileEntityBase = (TileEntityBase) world.getTileEntity(pos);
			if (tileEntityBase.hasComponentOfType(CableWrapperProviderComponent.class)) {
				return tileEntityBase.getComponent(CableWrapperProviderComponent.class);
			}
		}
		return null;
	}

	@Override
	public void onStaticPowerNeighborChanged(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onStaticPowerNeighborChanged(state, world, pos, neighbor);
		if (!world.isRemote() && world instanceof ServerWorld) {
			AbstractCableWrapper cable = CableNetworkManager.get((ServerWorld) world).getCable(pos);

			if (cable != null && cable.getNetwork() != null) {
				cable.getNetwork().updateGraph((ServerWorld) world, pos);
			}
		}
	}

	@Override
	public void wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);

		// Get the cable component.
		CableWrapperProviderComponent cableComponent = getCableWrapperComponent(world, pos);

		// Set the disabled state of the side.
		if (cableComponent != null) {
			Direction dir = CableBounds.getHoveredAttachmentDirection(pos, player);
			cableComponent.setSideDisabledState(dir, !cableComponent.isSideDisabled(dir));
		}
	}

	protected boolean isDisabledOnSide(IWorld world, BlockPos pos, Direction direction) {
		CableWrapperProviderComponent cableComponent = getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.isSideDisabled(direction);
		}
		return true;
	}

	protected abstract boolean isConnectedToCableInDirection(IWorld world, BlockPos pos, Direction direction);

	protected abstract boolean isConnectedToAttachableInDirection(IWorld world, BlockPos pos, Direction direction);
}
