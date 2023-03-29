package theking530.staticpower.blockentities.power.powermonitor;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.client.rendering.blocks.DefaultMachineBakedModel;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockPowerMonitor extends StaticPowerMachineBlock {
	protected static final VoxelShape Z_AXIS_SHAPE = Block.box(0.0D, 3D, 3.5D, 16.0D, 13.0D, 12.5D);
	protected static final VoxelShape X_AXIS_SHAPE = Block.box(3.5D, 3D, 0.0D, 12.5D, 13.0D, 16.0D);

	public BlockPowerMonitor() {
		super(StaticPowerTiers.LUMUM);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		DefaultMachineBakedModel model = new DefaultMachineBakedModel(existingModel);
		model.setSideOffset(BlockSide.TOP, new Vector3f(0, -3, 0));
		model.setSideOffset(BlockSide.BOTTOM, new Vector3f(0, -3, 0));
		model.setSideOffset(BlockSide.BACK, new Vector3f(-3, 0, 0));
		return model;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityPowerMonitor.TYPE.create(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facingDirection = state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING);
		if (facingDirection.getAxis() == Direction.Axis.Z) {
			return Z_AXIS_SHAPE;
		} else {
			return X_AXIS_SHAPE;
		}
	}
}
