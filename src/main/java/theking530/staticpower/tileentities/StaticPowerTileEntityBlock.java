package theking530.staticpower.tileentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.common.wrench.RegularWrenchMode;
import theking530.common.wrench.SneakWrenchMode;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent.SideIncrementDirection;
import theking530.staticpower.tileentities.utilities.interfaces.IBreakSerializeable;

public abstract class StaticPowerTileEntityBlock extends StaticPowerBlock {
	protected enum HasGuiType {
		NEVER, ALWAYS, SNEAKING_ONLY;
	}

	protected boolean shouldDropContents;

	protected StaticPowerTileEntityBlock(String name) {
		this(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticPowerTileEntityBlock(String name, Properties properies) {
		super(name, properies);
		this.shouldDropContents = true;
		this.setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getCutout();
	}

	@Override
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity tileEntity = world.getTileEntity(pos);

		// Check to ensure this is a tile entity base and the gui type indicates the
		// need for a gui.
		if (tileEntity instanceof TileEntityBase) {
			HasGuiType guiType = hasGuiScreen(tileEntity, state, world, pos, player, hand, hit);
			if (guiType != HasGuiType.NEVER) {
				// Ensure we meet the criteria for entering the GUI.
				if (guiType == HasGuiType.ALWAYS || (guiType == HasGuiType.SNEAKING_ONLY && player.isSneaking())) {

					// Only call this on the server.
					if (!world.isRemote) {
						enterGuiScreen((TileEntityBase) tileEntity, state, world, pos, player, hand, hit);
					}

					// Raise the on Gui entered method.
					if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
						((TileEntityBase) world.getTileEntity(pos)).onGuiEntered(state, player, hand, hit);
					}
					return ActionResultType.CONSUME;
				}
			}
		}
		// IF we didn't return earlier, continue the execution.
		return ActionResultType.PASS;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
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
	public void enterGuiScreen(TileEntityBase tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote) {
			NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, pos);
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
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public abstract TileEntity createTileEntity(final BlockState state, final IBlockReader world);

	@Override
	public ActionResultType wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (mode == RegularWrenchMode.ROTATE) {
			if (facing != Direction.UP && facing != Direction.DOWN) {
				if (facing != world.getBlockState(pos).get(FACING)) {
					world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing), 1 | 2);
				} else {
					world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing.getOpposite()), 1 | 2);
				}
			}
			return ActionResultType.SUCCESS;
		} else {
			TileEntityBase TE = (TileEntityBase) world.getTileEntity(pos);
			if (TE.hasComponentOfType(SideConfigurationComponent.class)) {
				TE.getComponent(SideConfigurationComponent.class).modulateWorldSpaceSideMode(facing, SideIncrementDirection.FORWARD);
				return ActionResultType.SUCCESS;
			}
			return super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		}
	}

	@Override
	public ActionResultType sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		// If we're on the server.
		if (!world.isRemote && world.getTileEntity(pos) instanceof IBreakSerializeable) {
			// Get a handle to the serializeable tile entity.
			IBreakSerializeable tempMachine = (IBreakSerializeable) world.getTileEntity(pos);

			if (tempMachine.shouldSerializeWhenBroken()) {
				// Create a new itemstack to represent this block.
				ItemStack machineStack = new ItemStack(asItem());

				// Create some new nbt to add to this stack. Then, create another new nbt to
				// hold our serializeable data.
				CompoundNBT topLevelNbt = new CompoundNBT();
				CompoundNBT serializeabltNbt = new CompoundNBT();

				// Serialize the tile entity and then store it on the serializeabltNbt. Then,
				// add the serializeabltNbt to the itemstack.
				tempMachine.serializeOnBroken(serializeabltNbt);
				topLevelNbt.put(IBreakSerializeable.SERIALIZEABLE_NBT, serializeabltNbt);

				// Drop the itemstack and swap this block to air (break it).
				ItemEntity droppedItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, machineStack);
				world.addEntity(droppedItem);
				world.setBlockState(pos, Blocks.AIR.getDefaultState());

				return ActionResultType.SUCCESS;
			}
		}
		return super.sneakWrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
	}
}