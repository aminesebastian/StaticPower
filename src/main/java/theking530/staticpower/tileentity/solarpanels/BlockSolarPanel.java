package theking530.staticpower.tileentity.solarpanels;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentity.BlockMachineBase;
import theking530.staticpower.utilities.Tier;

public class BlockSolarPanel extends BlockMachineBase {
	public Tier TIER;

	public BlockSolarPanel(String name, Tier tier) {
		super(name);
		TIER = tier;
	}

	@Override
	public void sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		ItemStack machineStack = new ItemStack(Item.getItemFromBlock(this));
		ItemEntity droppedItem = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), machineStack);
		world.addEntity(droppedItem);
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public boolean canBeWrenched(PlayerEntity player, World world, BlockPos pos, Direction facing, boolean sneaking) {
		return sneaking;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.SOLAR_PANEL_BASIC.create();
	}
}
