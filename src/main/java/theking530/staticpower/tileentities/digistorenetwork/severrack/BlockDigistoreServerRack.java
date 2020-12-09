package theking530.staticpower.tileentities.digistorenetwork.severrack;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.client.rendering.blocks.ServerRackModel;

public class BlockDigistoreServerRack extends StaticPowerTileEntityBlock implements ICustomModelSupplier {

	public BlockDigistoreServerRack(String name) {
		super(name, Block.Properties.create(Material.IRON));
	}

	@Override
	@OnlyIn(Dist.CLIENT)	
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		return new ServerRackModel(existingModel);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityDigistoreServerRack.TYPE.create();
	}
}
