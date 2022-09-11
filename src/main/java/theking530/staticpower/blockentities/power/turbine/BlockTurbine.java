package theking530.staticpower.blockentities.power.turbine;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.client.rendering.blocks.DefaultMachineBakedModel;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockTurbine extends StaticPowerMachineBlock {

	public BlockTurbine() {
		super(StaticPowerTiers.BASIC);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityTurbine.TYPE.create(pos, state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new DefaultMachineBakedModel(existingModel).setSideConfigVisiblity(Direction.UP, false).setSideConfigVisiblity(Direction.DOWN, false);
	}
}
