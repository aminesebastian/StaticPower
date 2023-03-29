package theking530.staticpower.blockentities.nonpowered.placer;

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
import theking530.staticpower.data.StaticPowerTiers;

public class BlockAutomaticPlacer extends StaticPowerMachineBlock {

	public BlockAutomaticPlacer() {
		super(StaticPowerTiers.BASIC);
	}

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
		return BlockEntityAutomaticPlacer.TYPE.create(pos, state);
	}
}
