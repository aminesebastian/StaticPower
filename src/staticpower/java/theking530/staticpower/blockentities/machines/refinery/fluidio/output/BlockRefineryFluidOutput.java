package theking530.staticpower.blockentities.machines.refinery.fluidio.output;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticpower.blockentities.machines.refinery.fluidio.BlockEntityRefineryFluidIO;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;

public class BlockRefineryFluidOutput extends StaticPowerMachineBlock {

	public BlockRefineryFluidOutput() {
		super(StaticCoreTiers.STATIC);
	}
	
	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRefineryFluidIO.OUTPUT_TYPE.create(pos, state);
	}
}
