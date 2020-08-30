package theking530.staticpower.tileentities.digistorenetwork.digistore;

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
import theking530.staticpower.client.rendering.blocks.DigistoreModel;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreBlock;

public class BlockDigistore extends BaseDigistoreBlock {

	public BlockDigistore(String name) {
		super(name);
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.SNEAKING_ONLY;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.DIGISTORE.create();
	}
	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new DigistoreModel(existingModel);
	}
}
