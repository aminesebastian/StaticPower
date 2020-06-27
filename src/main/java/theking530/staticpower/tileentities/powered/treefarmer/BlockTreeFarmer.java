package theking530.staticpower.tileentities.powered.treefarmer;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.client.rendering.blocks.GreenMachineBakedModel;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.StaticPowerDefaultMachineBlock;

public class BlockTreeFarmer extends StaticPowerDefaultMachineBlock {

	public BlockTreeFarmer(String name) {
		super(name);
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new GreenMachineBakedModel(existingModel);
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.TREE_FARM.create();
	}
}
