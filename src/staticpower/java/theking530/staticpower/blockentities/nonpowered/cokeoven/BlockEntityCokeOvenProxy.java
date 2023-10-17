package theking530.staticpower.blockentities.nonpowered.cokeoven;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModMultiblocks;

public class BlockEntityCokeOvenProxy extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCokeOvenProxy> TYPE = new BlockEntityTypeAllocator<>(
			"coke_oven_proxy", (type, pos, state) -> new BlockEntityCokeOvenProxy(pos, state), ModBlocks.CokeOvenBrick);

	public final MultiblockComponent<BlockEntityCokeOven> multiblockComponent;

	public BlockEntityCokeOvenProxy(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(multiblockComponent = new MultiblockComponent<BlockEntityCokeOven>("MultiblockComponent",
				ModMultiblocks.COKE_OVEN.get()));
	}

	public BlockPos getContainerReferencedBlockPos() {
		if (multiblockComponent.isWellFormed()) {
			return multiblockComponent.getMasterPosition();
		}
		return super.getContainerReferencedBlockPos();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		if (multiblockComponent.isWellFormed()) {
			BlockEntityCokeOven master = (BlockEntityCokeOven) getLevel()
					.getBlockEntity(multiblockComponent.getMasterPosition());
			return master.createMenu(windowId, inventory, player);
		}
		return null;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!multiblockComponent.isWellFormed()) {
			return LazyOptional.empty();
		}
		BlockEntityCokeOven master = (BlockEntityCokeOven) getLevel()
				.getBlockEntity(multiblockComponent.getMasterPosition());
		return master.getCapability(cap, side);
	}
}
