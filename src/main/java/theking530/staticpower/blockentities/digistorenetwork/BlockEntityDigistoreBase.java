package theking530.staticpower.blockentities.digistorenetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;

public abstract class BlockEntityDigistoreBase extends BlockEntityBase {
	public final DigistoreCableProviderComponent digistoreCableProvider;

	public BlockEntityDigistoreBase(BlockEntityTypeAllocator<? extends BlockEntityDigistoreBase> allocator, BlockPos pos,
			BlockState state) {
		this(allocator, pos, state, 0);

	}

	public BlockEntityDigistoreBase(BlockEntityTypeAllocator<? extends BlockEntityDigistoreBase> allocator, BlockPos pos,
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
	public boolean shouldSerializeWhenBroken(Player player) {
		return true;
	}
}
