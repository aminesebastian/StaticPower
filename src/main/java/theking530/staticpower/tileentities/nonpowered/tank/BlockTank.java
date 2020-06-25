package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.rendering.blocks.TankMachineBakedModel;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;

public class BlockTank extends StaticPowerTileEntityBlock implements ICustomModelSupplier {

	public BlockTank(String name) {
		super(name);
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new TankMachineBakedModel(existingModel);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getTranslucent();
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.TANK.create();
	}
}
