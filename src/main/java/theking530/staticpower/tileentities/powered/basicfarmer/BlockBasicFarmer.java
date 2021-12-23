package theking530.staticpower.tileentities.powered.basicfarmer;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;

import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock.HasGuiType;

public class BlockBasicFarmer extends StaticPowerMachineBlock {

	public BlockBasicFarmer(String name) {
		super(name);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return TileEntityBasicFarmer.TYPE.create();
	}
}
