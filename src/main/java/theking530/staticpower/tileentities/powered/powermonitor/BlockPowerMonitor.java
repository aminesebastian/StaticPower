package theking530.staticpower.tileentities.powered.powermonitor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;

public class BlockPowerMonitor extends StaticPowerMachineBlock {

	public BlockPowerMonitor(String name) {
		super(name);
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {

	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getTileEntity(pos) instanceof TileEntityPowerMonitor) {
			TileEntityPowerMonitor battery = (TileEntityPowerMonitor) blockAccess.getTileEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getTileEntity(pos) instanceof TileEntityPowerMonitor) {
			TileEntityPowerMonitor battery = (TileEntityPowerMonitor) blockAccess.getTileEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityPowerMonitor.TYPE.create();
	}
}
