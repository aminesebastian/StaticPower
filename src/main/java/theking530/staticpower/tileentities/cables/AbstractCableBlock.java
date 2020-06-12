package theking530.staticpower.tileentities.cables;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import theking530.api.wrench.RegularWrenchMode;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.tileentities.components.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.network.CableBoundsCache;
import theking530.staticpower.tileentities.network.CableNetworkManager;
import theking530.staticpower.utilities.Reference;

public abstract class AbstractCableBlock extends StaticPowerBlock implements ICustomModelSupplier {

	public final ResourceLocation StraightModel;
	public final ResourceLocation ExtensionModel;
	public final ResourceLocation AttachmentModel;

	protected final CableBoundsCache CableBounds;

	public AbstractCableBlock(String name, String modelFolder, CableBoundsCache cableBoundsGenerator) {
		super(name, Block.Properties.create(Material.IRON).notSolid());
		CableBounds = cableBoundsGenerator;
		StraightModel = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/straight");
		ExtensionModel = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/extension");
		AttachmentModel = new ResourceLocation(Reference.MOD_ID, "block/cables/" + modelFolder + "/attachment");
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return CableBounds.getShape(state, worldIn, pos, context);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public void registerAdditionalModels() {
		ModelLoader.addSpecialModel(StraightModel);
		ModelLoader.addSpecialModel(ExtensionModel);
		ModelLoader.addSpecialModel(AttachmentModel);
	}

	/**
	 * The existing model will be the core model.
	 */
	@Override
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel extensionModel = event.getModelRegistry().get(ExtensionModel);
		IBakedModel straightModel = event.getModelRegistry().get(StraightModel);
		IBakedModel attachmentModel = event.getModelRegistry().get(AttachmentModel);
		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public void onStaticPowerNeighborChanged(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onStaticPowerNeighborChanged(state, world, pos, neighbor);
		if (!world.isRemote() && world instanceof ServerWorld) {
			AbstractCableWrapper cable = CableNetworkManager.get((ServerWorld) world).getCable(pos);

			if (cable != null && cable.getNetwork() != null) {
				cable.getNetwork().updateGraph((ServerWorld) world, pos);
			}
		}
	}

	@Override
	public void wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);

		// Get the cable component.
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);

		// Set the disabled state of the side.
		if (cableComponent != null) {
			Direction dir = CableBounds.getHoveredAttachmentDirection(pos, player);
			cableComponent.setSideDisabledState(dir, !cableComponent.isSideDisabled(dir));
		}
	}

	protected boolean isDisabledOnSide(IWorld world, BlockPos pos, Direction direction) {
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.isSideDisabled(direction);
		}
		return true;
	}
}
