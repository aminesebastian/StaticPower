package theking530.staticpower.tileentities;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent.SideIncrementDirection;
import theking530.staticpower.tileentities.utilities.interfaces.IBreakSerializeable;

public class StaticPowerTileEntityBlock extends StaticPowerBlock {
	protected boolean shouldDropContents;

	protected StaticPowerTileEntityBlock(String name) {
		super(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f, 5.0f).sound(SoundType.METAL));
		this.shouldDropContents = true;
		this.setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		// Set the block to face the player.
		world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);

		// Check if this block's tile entity wants to be deserialized.
		if (world.getTileEntity(pos) instanceof IBreakSerializeable) {
			// Get a handle to the deserializeable tile entity.
			IBreakSerializeable tempMachine = (IBreakSerializeable) world.getTileEntity(pos);

			// If the stack has an NBT tag and has a serializeable tile entity's contents,
			// deserialize the tile entity.
			if (stack.hasTag() && stack.getTag().contains(IBreakSerializeable.SERIALIZEABLE_NBT)) {
				if (tempMachine.shouldDeserializeWhenPlaced(stack.getTag().getCompound(IBreakSerializeable.SERIALIZEABLE_NBT), world, pos, state, placer, stack)) {
					tempMachine.deserializeOnPlaced(stack.getTag().getCompound(IBreakSerializeable.SERIALIZEABLE_NBT), world, pos, state, placer, stack);
				}
			}
		}
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
		if (mode == RegularWrenchMode.ROTATE) {
			if (facing != Direction.UP && facing != Direction.DOWN) {
				if (facing != world.getBlockState(pos).get(FACING)) {
					world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing), 1 | 2);
				} else {
					world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing.getOpposite()), 1 | 2);
				}
			}
		} else {
			TileEntityBase TE = (TileEntityBase) world.getTileEntity(pos);
			if (TE.hasComponentOfType(SideConfigurationComponent.class)) {
				TE.getComponent(SideConfigurationComponent.class).modulateWorldSpaceSideMode(facing, SideIncrementDirection.FORWARD);
			}
		}
	}

	@Override
	public void sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
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
			}
		}
	}
}