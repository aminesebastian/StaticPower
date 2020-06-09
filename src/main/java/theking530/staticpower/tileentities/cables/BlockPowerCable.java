package theking530.staticpower.tileentities.cables;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;
import theking530.staticpower.utilities.Reference;

public class BlockPowerCable extends StaticPowerTileEntityBlock implements ICustomModelSupplier {
	public static final ResourceLocation CORE = new ResourceLocation(Reference.MOD_ID, "block/cables/power/core");
	public static final ResourceLocation STRAIGHT = new ResourceLocation(Reference.MOD_ID, "block/cables/power/straight");
	public static final ResourceLocation EXTENSION = new ResourceLocation(Reference.MOD_ID, "block/cables/power/extension");

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

	public BlockPowerCable(String name) {
		super(name, Block.Properties.create(Material.IRON).notSolid());

		setDefaultState(getDefaultState().with(CABLE_NORTH, false).with(CABLE_EAST, false).with(CABLE_SOUTH, false).with(CABLE_WEST, false).with(CABLE_UP, false).with(CABLE_DOWN, false)
				.with(ATTACHMENT_NORTH, false).with(ATTACHMENT_EAST, false).with(ATTACHMENT_SOUTH, false).with(ATTACHMENT_WEST, false).with(ATTACHMENT_UP, false).with(ATTACHMENT_DOWN, false));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityPowerCable();
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
		ModelLoader.addSpecialModel(CORE);
		ModelLoader.addSpecialModel(STRAIGHT);
		ModelLoader.addSpecialModel(EXTENSION);
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

	private BlockState getState(BlockState currentState, IWorld world, BlockPos pos) {
		return currentState.with(CABLE_NORTH, isConnectedToCableInDirection(world, pos, Direction.NORTH)).with(CABLE_EAST, isConnectedToCableInDirection(world, pos, Direction.EAST))
				.with(CABLE_SOUTH, isConnectedToCableInDirection(world, pos, Direction.SOUTH)).with(CABLE_WEST, isConnectedToCableInDirection(world, pos, Direction.WEST))
				.with(CABLE_UP, isConnectedToCableInDirection(world, pos, Direction.UP)).with(CABLE_DOWN, isConnectedToCableInDirection(world, pos, Direction.DOWN))
				.with(ATTACHMENT_NORTH, isConnectedToAttachableInDirection(world, pos, Direction.NORTH)).with(ATTACHMENT_EAST, isConnectedToAttachableInDirection(world, pos, Direction.EAST))
				.with(ATTACHMENT_SOUTH, isConnectedToAttachableInDirection(world, pos, Direction.SOUTH)).with(ATTACHMENT_WEST, isConnectedToAttachableInDirection(world, pos, Direction.WEST))
				.with(ATTACHMENT_UP, isConnectedToAttachableInDirection(world, pos, Direction.UP)).with(ATTACHMENT_DOWN, isConnectedToAttachableInDirection(world, pos, Direction.DOWN));
	}

	protected boolean isConnectedToCableInDirection(IWorld world, BlockPos pos, Direction direction) {
		if (world.getTileEntity(pos) instanceof TileEntityPowerCable) {
			TileEntityPowerCable cable = (TileEntityPowerCable) world.getTileEntity(pos);
			return cable.isValidExtenderForNetwork(pos.offset(direction), direction.getOpposite());
		}
		return false;
	}

	protected boolean isConnectedToAttachableInDirection(IWorld world, BlockPos pos, Direction direction) {
		if (world.getTileEntity(pos.offset(direction)) != null) {
			TileEntity attachable = world.getTileEntity(pos.offset(direction));
			return attachable.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).isPresent();
		}
		return false;
	}

	/**
	 * The existing model will be the core model.
	 */
	@Override
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel extensionModel = event.getModelRegistry().get(EXTENSION);
		IBakedModel straightModel = event.getModelRegistry().get(STRAIGHT);
		return new CableBakedModel(existingModel, extensionModel, straightModel);
	}
}
