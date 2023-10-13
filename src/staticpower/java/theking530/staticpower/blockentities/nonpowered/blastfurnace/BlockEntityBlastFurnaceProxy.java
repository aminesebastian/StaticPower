package theking530.staticpower.blockentities.nonpowered.blastfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.newstyle.NewMultiblockComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModMultiblocks;

public class BlockEntityBlastFurnaceProxy extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBlastFurnaceProxy> TYPE = new BlockEntityTypeAllocator<>(
			"blast_furnace_proxy", (type, pos, state) -> new BlockEntityBlastFurnaceProxy(pos, state),
			ModBlocks.BlastFurnace);

	public final NewMultiblockComponent<BlockEntityBlastFurnace> multiblockComponent;

	public BlockEntityBlastFurnaceProxy(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(multiblockComponent = new NewMultiblockComponent<BlockEntityBlastFurnace>(
				"MultiblockComponent", ModMultiblocks.BLAST_FURNACE.get(), MultiblockState.FAILED));
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
			BlockEntityBlastFurnace master = (BlockEntityBlastFurnace) getLevel()
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

		BlockEntityBlastFurnace master = (BlockEntityBlastFurnace) getLevel()
				.getBlockEntity(multiblockComponent.getMasterPosition());
		return master.getCapability(cap, side);
	}
}
