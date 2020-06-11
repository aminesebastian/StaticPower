package theking530.staticpower.tileentities.cables;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.CableWrapperProviderComponent;
import theking530.staticpower.tileentities.network.CableStateWrapper;
import theking530.staticpower.utilities.Reference;

public abstract class AbstractCableBlock extends StaticPowerTileEntityBlock implements ICustomModelSupplier {
	public final ResourceLocation StraightModel;
	public final ResourceLocation Extension;

	public static final BooleanProperty CABLE_NORTH = BooleanProperty.create("cable_north");
	public static final BooleanProperty CABLE_SOUTH = BooleanProperty.create("cable_south");
	public static final BooleanProperty CABLE_EAST = BooleanProperty.create("cable_east");
	public static final BooleanProperty CABLE_WEST = BooleanProperty.create("cable_west");
	public static final BooleanProperty CABLE_UP = BooleanProperty.create("cable_up");
	public static final BooleanProperty CABLE_DOWN = BooleanProperty.create("cable_down");

	public static final BooleanProperty ATTACHMENT_NORTH = BooleanProperty.create("attachment_north");
	public static final BooleanProperty ATTACHMENT_SOUTH = BooleanProperty.create("attachment_south");
	public static final BooleanProperty ATTACHMENT_EAST = BooleanProperty.create("attachment_east");
	public static final BooleanProperty ATTACHMENT_WEST = BooleanProperty.create("attachment_west");
	public static final BooleanProperty ATTACHMENT_UP = BooleanProperty.create("attachment_up");
	public static final BooleanProperty ATTACHMENT_DOWN = BooleanProperty.create("attachment_down");

	public AbstractCableBlock(String name, String modelFolder) {
		super(name, Block.Properties.create(Material.IRON).notSolid());
		StraightModel = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/straight");
		Extension = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/extension");

		setDefaultState(getDefaultState().with(CABLE_NORTH, false).with(CABLE_EAST, false).with(CABLE_SOUTH, false).with(CABLE_WEST, false).with(CABLE_UP, false).with(CABLE_DOWN, false)
				.with(ATTACHMENT_NORTH, false).with(ATTACHMENT_EAST, false).with(ATTACHMENT_SOUTH, false).with(ATTACHMENT_WEST, false).with(ATTACHMENT_UP, false).with(ATTACHMENT_DOWN, false));
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return new CableStateWrapper(state).getShape(2.0D);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(CABLE_NORTH, CABLE_EAST, CABLE_SOUTH, CABLE_WEST, CABLE_UP, CABLE_DOWN, ATTACHMENT_NORTH, ATTACHMENT_EAST, ATTACHMENT_SOUTH, ATTACHMENT_WEST, ATTACHMENT_UP, ATTACHMENT_DOWN);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public void registerAdditionalModels() {
		ModelLoader.addSpecialModel(StraightModel);
		ModelLoader.addSpecialModel(Extension);
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
				.with(ATTACHMENT_NORTH, isConnectedToAttachableInDirection(world, pos, Direction.NORTH)).with(ATTACHMENT_EAST, isConnectedToAttachableInDirection(world, pos, Direction.EAST))
				.with(ATTACHMENT_SOUTH, isConnectedToAttachableInDirection(world, pos, Direction.SOUTH)).with(ATTACHMENT_WEST, isConnectedToAttachableInDirection(world, pos, Direction.WEST))
				.with(ATTACHMENT_UP, isConnectedToAttachableInDirection(world, pos, Direction.UP)).with(ATTACHMENT_DOWN, isConnectedToAttachableInDirection(world, pos, Direction.DOWN));
	}

	/**
	 * The existing model will be the core model.
	 */
	@Override
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel extensionModel = event.getModelRegistry().get(Extension);
		IBakedModel straightModel = event.getModelRegistry().get(StraightModel);
		return new CableBakedModel(existingModel, extensionModel, straightModel);
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

	protected abstract boolean isConnectedToCableInDirection(IWorld world, BlockPos pos, Direction direction);

	protected abstract boolean isConnectedToAttachableInDirection(IWorld world, BlockPos pos, Direction direction);
}
