package theking530.staticpower.tileentities.digistorenetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.tileentities.TileEntityBase;

public abstract class BaseDigistoreTileEntity extends TileEntityBase {
	public final DigistoreCableProviderComponent digistoreCableProvider;

	public BaseDigistoreTileEntity(BlockEntityTypeAllocator<? extends BaseDigistoreTileEntity> allocator, BlockPos pos,
			BlockState state) {
		this(allocator, pos, state, 0);

	}

	public BaseDigistoreTileEntity(BlockEntityTypeAllocator<? extends BaseDigistoreTileEntity> allocator, BlockPos pos,
			BlockState state, int powerUsage) {
		super(allocator, pos, state);
		registerComponent(
				digistoreCableProvider = new DigistoreCableProviderComponent("DigistoreCableProviderComponent",
						powerUsage).setShouldControlOnState());
	}

	public boolean isManagerPresent() {
		return digistoreCableProvider.isManagerPresent();
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}
}
