package theking530.staticpower.tileentities.cables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.tileentities.cables.network.CableBoundsCache;
import theking530.staticpower.tileentities.cables.network.CableNetworkManager;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class AbstractCableBlock extends StaticPowerBlock implements ICustomModelSupplier {

	public final CableBoundsCache CableBounds;

	public AbstractCableBlock(String name, String modelFolder, CableBoundsCache cableBoundsGenerator) {
		super(name, Block.Properties.create(Material.IRON).notSolid());
		CableBounds = cableBoundsGenerator;
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
	public void onStaticPowerNeighborChanged(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onStaticPowerNeighborChanged(state, world, pos, neighbor);
		if (!world.isRemote()) {
			AbstractCableWrapper cable = CableNetworkManager.get((ServerWorld) world).getCable(pos);
			if (cable != null && cable.getNetwork() != null) {
				cable.getNetwork().updateGraph((ServerWorld) world, pos);
			}
		}
	}

	@Override
	public ActionResultType sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (!world.isRemote) {
			Block.spawnDrops(world.getBlockState(pos), world, pos, world.getTileEntity(pos), player, wrench);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResultType wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);

		if (!world.isRemote) {
			AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
			if (component == null) {
				return ActionResultType.FAIL;
			}

			Direction hoveredDirection = CableBounds.getHoveredAttachmentDirection(pos, player);

			if (hoveredDirection != null) {
				ItemStack output = component.removeAttachment(hoveredDirection);
				if (!output.isEmpty()) {
					WorldUtilities.dropItem(world, pos, output, 1);
				}
			}
		}

		return ActionResultType.SUCCESS;
	}

	protected boolean isDisabledOnSide(IWorld world, BlockPos pos, Direction direction) {
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.isSideDisabled(direction);
		}
		return true;
	}
}
