package theking530.staticpower.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import theking530.api.wrench.IWrenchable;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.IItemBlockProvider;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.tileentity.ISideConfigurable.SideIncrementDirection;

public class BlockMachineBase extends StaticPowerBlock implements IWrenchable, IItemBlockProvider {
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	protected boolean shouldDropContents;

	protected BlockMachineBase(String name) {
		super(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f, 5.0f).sound(SoundType.METAL));
		this.shouldDropContents = true;
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);

		if (worldIn.getTileEntity(pos) instanceof TileEntityBase) {
			TileEntityBase tempMachine = (TileEntityBase) worldIn.getTileEntity(pos);
			if (stack.hasTag()) {
				if (tempMachine.shouldDeserializeWhenPlaced(stack.getTag(), worldIn, pos, state, placer, stack)) {
					tempMachine.deserializeOnPlaced(stack.getTag(), worldIn, pos, state, placer, stack);
				}
			}
		}
	}

	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
//		if (shouldDropContents && worldIn.getTileEntity(pos) instanceof TileEntityBase) {
//			TileEntityBase tileentity = (TileEntityBase) worldIn.getTileEntity(pos);
//			if (!tileentity.wasWrenchedDoNotBreak) {
//				IItemHandler tempHandler = tileentity.slotsInput;
//				if (tempHandler != null) {
//					for (int i = 0; i < tempHandler.getSlots(); i++) {
//						if (tempHandler.getStackInSlot(i) != null) {
//							WorldUtilities.dropItem(worldIn, pos, tempHandler.extractItem(i, tempHandler.getStackInSlot(i).getCount(), false));
//						}
//					}
//				}
//				tempHandler = tileentity.slotsOutput;
//				if (tempHandler != null) {
//					for (int i = 0; i < tempHandler.getSlots(); i++) {
//						if (tempHandler.getStackInSlot(i) != null) {
//							WorldUtilities.dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), tempHandler.extractItem(i, tempHandler.getStackInSlot(i).getCount(), false));
//						}
//					}
//				}
//				tempHandler = tileentity.slotsUpgrades;
//				if (tempHandler != null) {
//					for (int i = 0; i < tempHandler.getSlots(); i++) {
//						if (tempHandler.getStackInSlot(i) != null) {
//							WorldUtilities.dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), tempHandler.extractItem(i, tempHandler.getStackInSlot(i).getCount(), false));
//						}
//					}
//				}
//				tempHandler = tileentity.slotsInternal;
//				if (tempHandler != null) {
//					for (int i = 0; i < tempHandler.getSlots(); i++) {
//						if (tempHandler.getStackInSlot(i) != null) {
//							WorldUtilities.dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), tempHandler.extractItem(i, tempHandler.getStackInSlot(i).getCount(), false));
//						}
//					}
//				}
//			}
//		}
		// super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (hasTileEntity()) {
			StaticPower.LOGGER
					.error("StaticPowerBlock inherting from BlockMachineBase indicates that is has a tile entity, but the createTileEntity method has not been overriden and will return null.");
		}
		return null;
	}

	@Override
	public boolean canBeWrenched(PlayerEntity player, World world, BlockPos pos, Direction facing, boolean sneaking) {
		return true;
	}

	@Override
	public void wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (!world.isRemote) {
			if (mode == RegularWrenchMode.ROTATE) {
				if (facing != Direction.UP && facing != Direction.DOWN) {
					if (facing != world.getBlockState(pos).get(FACING)) {
						world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing), 2);
					} else {
						world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing.getOpposite()), 2);
					}
				}
			} else {
				TileEntityBase TE = (TileEntityBase) world.getTileEntity(pos);
				TE.incrementSideConfiguration(facing, SideIncrementDirection.FORWARD);
				TE.updateBlock();
			}
		}
		world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), 2);
	}

	@Override
	public void sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (!world.isRemote) {
			CompoundNBT nbt = new CompoundNBT();
			ItemStack machineStack = new ItemStack(Item.getItemFromBlock(this));
			if (world.getTileEntity(pos) instanceof TileEntityBase) {
				TileEntityBase tempMachine = (TileEntityBase) world.getTileEntity(pos);
				tempMachine.wasWrenchedDoNotBreak = true;
				if (tempMachine.shouldSerializeWhenBroken()) {
					tempMachine.serializeOnBroken(nbt);
					machineStack.setTag(nbt);
				}
			}
			ItemEntity droppedItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, machineStack);
			world.addEntity(droppedItem);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}
}