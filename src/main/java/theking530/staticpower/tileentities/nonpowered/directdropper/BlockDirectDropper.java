package theking530.staticpower.tileentities.nonpowered.directdropper;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;

public class BlockDirectDropper extends StaticPowerMachineBlock {

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return TileEntityDirectDropper.TYPE.create(pos, state);
	}
}
