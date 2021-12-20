package theking530.staticpower.tileentities.powered.powermonitor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.client.rendering.blocks.DefaultMachineBakedModel;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class BlockPowerMonitor extends StaticPowerMachineBlock {
	protected static final VoxelShape Z_AXIS_SHAPE = Block.makeCuboidShape(0.0D, 3D, 3.5D, 16.0D, 13.0D, 12.5D);
	protected static final VoxelShape X_AXIS_SHAPE = Block.makeCuboidShape(3.5D, 3D, 0.0D, 12.5D, 13.0D, 16.0D);

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
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		DefaultMachineBakedModel model = new DefaultMachineBakedModel(existingModel);
		model.setSideOffset(BlockSide.TOP, new Vector3f(0, -3, 0));
		model.setSideOffset(BlockSide.BOTTOM, new Vector3f(0, -3, 0));
		model.setSideOffset(BlockSide.BACK, new Vector3f(-3, 0, 0));
		return model;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityPowerMonitor.TYPE.create();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction facingDirection = state.get(StaticPowerTileEntityBlock.FACING);
		if (facingDirection.getAxis() == Direction.Axis.Z) {
			return Z_AXIS_SHAPE;
		} else {
			return X_AXIS_SHAPE;
		}
	}
}
