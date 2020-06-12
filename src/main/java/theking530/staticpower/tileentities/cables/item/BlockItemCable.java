package theking530.staticpower.tileentities.cables.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.tileentities.cables.AbstractCableBlock;
import theking530.staticpower.tileentities.components.CableWrapperProviderComponent;
import theking530.staticpower.tileentities.network.CableBoundsCache;
import theking530.staticpower.tileentities.network.factories.cables.CableTypes;

public class BlockItemCable extends AbstractCableBlock {

	public BlockItemCable(String name) {
		super(name, "item", new CableBoundsCache(2.0D, 3.0D, 2.0D));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityItemCable();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getCutout();
	}

	@Override
	protected boolean isConnectedToCableInDirection(IWorld world, BlockPos pos, Direction direction) {
		CableWrapperProviderComponent wrapperComponent = getCableWrapperComponent(world, pos.offset(direction));
		if (wrapperComponent != null) {
			return wrapperComponent.getCableType() == CableTypes.BASIC_ITEM;
		}
		return false;
	}

	@Override
	protected boolean isConnectedToAttachableInDirection(IWorld world, BlockPos pos, Direction direction) {
		if (world.getTileEntity(pos.offset(direction)) != null) {
			return world.getTileEntity(pos.offset(direction)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
		}
		return false;
	}
}
